package com.example.akash.fragment;


import android.app.Dialog;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.akash.shield.R;
import com.example.akash.shield.VolleyTaskManager;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class FacebookFragment extends Fragment {
    View rootView;
    Button facebok;

    Dialog proImageOptionDialog;
    private CallbackManager callbackManager;

    private VolleyTaskManager mVolleyTaskManager;
    private User mUser;

    private EditText etName, etMobileNo, etEmail, etPIN;
    private TextView tvDOB;

    private ImageView ivProfile;
    private String sName = "", sPhone = "", sEmail = "", sDOB = "", sPhoto = "", sPIN = "";

    public FacebookFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        rootView=inflater.inflate(R.layout.fragment_facebook, container, false);

        FacebookSdk.sdkInitialize(getActivity());
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onError(FacebookException error) {
                        Log.e("Error", error.getMessage());
                        if (error instanceof FacebookAuthorizationException) {
                            if (AccessToken.getCurrentAccessToken() != null) {
                                LoginManager.getInstance().logOut();
                            }
                        }
                    }

            @Override
            public void onSuccess(LoginResult loginResult) {
                doFacebookGraphRequest(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.e("Error", "Cancelled");
            }
        });

        facebok=(Button)rootView.findViewById(R.id.btnFacebook);
        facebok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doUpdateView();
               // proImageOptionDialog.cancel();

            }
        });

        return rootView;
    }

    private void doFacebookGraphRequest(AccessToken accessToken) {

        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code
                        String sEmail = "";
                        String sName = "";
                        String sDOB = "";
                        String sId = "";
                        String sProfilePicUrl = "";
                        try {
                            if(object.has("email")) {
                                sEmail = object.getString("email");
                            }
                            if(object.has("birthday")) {
                                sDOB = object.getString("birthday");
                            }
                            if(object.has("id")) {
                                sId = object.getString("id");
                            }
                            if(object.has("name")) {
                                sName = object.getString("name");
                            }
                            sName = sName.replaceAll("\\s", "");
                            if(object.has("birthday")) {
                                sDOB = object.getString("birthday");
                            }
                            Date date;
                            if(!TextUtils.isEmpty(sDOB)){

                                try {
                                    date = new SimpleDateFormat("MM/dd/yyyy").parse(sDOB);
                                    sDOB = new SimpleDateFormat("yyyy-MM-dd").format(date);
                                } catch (java.text.ParseException e) {
                                    e.printStackTrace();
                                }


                            }
                            sProfilePicUrl = String.format("https://graph.facebook.com/%s/picture",sId);
                            Log.e("Data: ",sDOB+ sEmail + sName + sProfilePicUrl );
                            doSetEditTextFields(sName, sProfilePicUrl, sEmail, sDOB);
                        } catch (Exception e) {

                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email,name,birthday");
        request.setParameters(parameters);
        request.executeAsync();

    }

    private void doSetEditTextFields(String sPersonName, String sPersonPhotoUrl, String sPersonEmail, String sPersonDOB) {

        etName.setText(sPersonName);
        etEmail.setText(sPersonEmail);
        tvDOB.setText(sPersonDOB);

        Log.e("Img Url", sPersonPhotoUrl);

       // mVolleyTaskManager = new VolleyTaskManager(getActivity());
        //mVolleyTaskManager.getImageFromUrl(sPersonPhotoUrl, true, true);

    }

    private void doUpdateView() {
        onLogInClicked();
    }

    private void onLogInClicked() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile", "user_birthday"));

    }

}
