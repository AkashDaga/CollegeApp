package com.example.akash.fragment;


import android.app.Dialog;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.example.akash.shield.R;
import com.example.akash.shield.ServerResponseCallback;
import com.example.akash.shield.VolleyTaskManager;
import com.facebook.CallbackManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import org.json.JSONArray;

/**
 * A simple {@link Fragment} subclass.
 */
public class GoogleFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,ServerResponseCallback {
    View rootView;
    Button gmail;

    Dialog proImageOptionDialog;
    private CallbackManager callbackManager;

    private VolleyTaskManager mVolleyTaskManager;
    private User mUser;

    private EditText etName, etMobileNo, etEmail, etPIN;
    private TextView tvDOB;

    private ImageView ivProfile;
    private String sName = "", sPhone = "", sEmail = "", sDOB = "", sPhoto = "", sPIN = "";

    private GoogleApiClient mGoogleApiClient;
    private ConnectionResult mConnectionResult;

    private boolean isSignInClicked,isIntentInProgress;
    private int RC_SIGN_IN = 0;


    public GoogleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }



        rootView=inflater.inflate(R.layout.fragment_google, container, false);
        gmail=(Button)rootView.findViewById(R.id.btnGoogle);
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();

        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSignInWithGplus();

            }
        });

        return rootView;
    }

    private void doSignInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            isSignInClicked = true;
            doResolveSignInError();
            mGoogleApiClient.connect();
        }
    }

    private void doResolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                isIntentInProgress = true;
                mConnectionResult.startResolutionForResult(getActivity(), RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                isIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }

    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onVolleyErrorOccurred(VolleyError response) {

    }

    @Override
    public void getResponse(String response) {

    }

    @Override
    public void getResponse(JSONArray jsonArray) {

    }

    @Override
    public void getDownloadedImage(Bitmap bitmap) {

    }
}
