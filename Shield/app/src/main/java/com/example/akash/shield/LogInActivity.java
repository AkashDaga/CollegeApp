package com.example.akash.shield;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

public class LogInActivity extends AppCompatActivity implements ServerResponseCallback{

    ImageView profilePic;
    TextView signUp,forgotPassword;
    EditText email,password;
    String Semail,Spassword;
    Button logIn,reset;
    String sLOgInurl="http://192.168.0.8:8084/MCETSuraksha/ServletLogIn";
    User mUser;
    private Boolean sResultStatus;
    public circularProgress mCircularProgress;
    public SharedPreferenceInventory myInventory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mCircularProgress=new circularProgress(this);
        myInventory=new SharedPreferenceInventory(this);

        profilePic=(ImageView)findViewById(R.id.ivProfilepicinLogin);
        byte[] decodedString=Base64.decode(myInventory.getProfilePicConvertedBase64(), Base64.DEFAULT);
        Bitmap userImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        profilePic.setImageBitmap(userImage);

        signUp=(TextView)findViewById(R.id.tvSignUp);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),ChooseSignUpModeActivity.class);
                startActivity(intent);
            }
        });

        forgotPassword=(TextView)findViewById(R.id.tvResetPassword);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),OtpForRestPasswordActivity.class);
                startActivity(intent);
            }
        });

        email=(EditText)findViewById(R.id.etUserId);


        password=(EditText)findViewById(R.id.editText);

        logIn=(Button)findViewById(R.id.btLogIn);
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCircularProgress.showProgressDialog();

                Spassword=password.getText().toString().trim();
                Semail=email.getText().toString().trim();

                ObjectMapper mObjectMapper = new ObjectMapper();
                User objUser = new User();
                objUser.setPassword(Spassword);
                objUser.setEmail(Semail);


                String sLgInUrlValueToBePassed = "";

                mObjectMapper.enable(SerializationFeature.INDENT_OUTPUT);
                try {
                    sLgInUrlValueToBePassed = mObjectMapper.writeValueAsString(objUser);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

                Log.e("req", sLgInUrlValueToBePassed);

                VolleyTaskManager mVolleyTaskManager
                        = new VolleyTaskManager(LogInActivity.this);
                mVolleyTaskManager.doRegistration(sLOgInurl, sLgInUrlValueToBePassed, com.android.volley.Request.Method.POST, false, false, false);
            }
        });



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
        sResultStatus = mUser.getLogInResult();
        if(sResultStatus==true){

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

            String msg="logged in successfully";
            dbHelper.addNewNotification(Date, Time, msg);
            mCircularProgress.hideProgressDialog();
            Intent intent=new Intent(getApplicationContext(),SetMessageActivity.class);
            startActivity(intent);
        }
        else{
            mCircularProgress.hideProgressDialog();
            Toast.makeText(getApplicationContext(),"Wrong user id or password",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void getResponse(JSONArray jsonArray) {

    }

    @Override
    public void getDownloadedImage(Bitmap bitmap) {

    }
}