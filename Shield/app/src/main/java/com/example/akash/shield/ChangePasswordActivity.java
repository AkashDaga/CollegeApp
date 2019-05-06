package com.example.akash.shield;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.akash.adapters.NotificationDBHelper;
import com.example.akash.fragment.User;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.json.JSONArray;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ChangePasswordActivity extends AppCompatActivity implements ServerResponseCallback{
    ImageView profile;
    EditText password,RePassword;
    String Spassword,SrePassword;
    Button submit,reset;
    SharedPreferenceInventory myInventory;
    circularProgress mCircularProgress;
    String updateURL="http://192.168.0.8:8084/MCETSuraksha/ServletRegister";
    User mUser;
    Boolean sResultStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mUser=new User();

        myInventory=new SharedPreferenceInventory(getApplicationContext());
        byte[] decodedString= Base64.decode(myInventory.getProfilePicConvertedBase64(), Base64.DEFAULT);
        Bitmap userImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        profile.setImageBitmap(userImage);

        password=(EditText)findViewById(R.id.etNewPassword);
        RePassword=(EditText)findViewById(R.id.etRetypePassword);

        submit=(Button)findViewById(R.id.btPwdUpdate);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spassword=password.getText().toString();
                SrePassword=RePassword.getText().toString();
                if(Spassword.equals(SrePassword)){

                    doPasswordUpdate();

                }
            }
        });

        reset=(Button)findViewById(R.id.btPwdReset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password.setText("");
                RePassword.setText("");
            }
        });




    }

    private void doPasswordUpdate() {
        mCircularProgress.showProgressDialog();
        ObjectMapper mObjectMapper = new ObjectMapper();
        User objUser = new User();
        objUser.setPassword(Spassword);
        objUser.setRetype_password(SrePassword);
        String sLgInUrlValueToBePassed = "";

        mObjectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            sLgInUrlValueToBePassed = mObjectMapper.writeValueAsString(objUser);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        Log.e("req", sLgInUrlValueToBePassed);
        VolleyTaskManager mVolleyTaskManager
                = new VolleyTaskManager(ChangePasswordActivity.this);
        mVolleyTaskManager.doRegistration(updateURL, sLgInUrlValueToBePassed, com.android.volley.Request.Method.POST, false, false, false);
    }

    @Override
    public void onVolleyErrorOccurred(VolleyError response) {

    }

    @Override
    public void getResponse(String response) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        Log.e("Response",response);
        try {
            mUser= objectMapper.readValue(response,
                    User.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sResultStatus = mUser.getPasswordUpdte();
        if(sResultStatus.equals("true")){

            NotificationDBHelper dbHelper=new NotificationDBHelper(getApplicationContext());
            //For Date

            final Calendar c= Calendar.getInstance();
            int yy = c.get(Calendar.YEAR);
            int mm= c.get(Calendar.MONTH);
            int dd= c.get(Calendar.DAY_OF_MONTH);
            StringBuilder builder=new StringBuilder().append(yy).append(" ")
                    .append(" - ").append(mm+1).append(" - ").append(dd);
            String Date=builder.toString();

            //For Time
            String Time= new SimpleDateFormat("HH:mm:ss").format(new Date());

            String msg="password changed successfully";
            dbHelper.addNewNotification(Date, Time, msg);
            mCircularProgress.hideProgressDialog();
            Intent intent=new Intent(getApplicationContext(),AppDrawerActivity.class);
            startActivity(intent);
        }
        else{
            mCircularProgress.hideProgressDialog();
            Toast.makeText(getApplicationContext(), "password doesnt match", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void getResponse(JSONArray jsonArray) {

    }

    @Override
    public void getDownloadedImage(Bitmap bitmap) {

    }
}
