package com.example.akash.shield;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity implements ServerResponseCallback{
    final private String OTPurl="http://192.168.137.94:8084/MCETSuraksha/ServletOTP";
    public static EditText EnterOTP;
    private ImageView profile;
    Button next,resend;
    public String sOTP,otp,id;
    public SharedPreferenceInventory myInventory;
    private TextView TextViewTime;
    private Bundle bundle;
    public User mUser;
    public circularProgress mCircularProgres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        TextViewTime=(TextView)findViewById(R.id.TVTime);
        EnterOTP=(EditText)findViewById(R.id.OTPRecieve);

        bundle=getIntent().getExtras();
        myInventory=new SharedPreferenceInventory(this);
        mUser=new User();
        mCircularProgres=new circularProgress(this);

        profile=(ImageView)findViewById(R.id.ivOTP);
        byte[] decodedString= Base64.decode(myInventory.getProfilePicConvertedBase64(), Base64.DEFAULT);
        Bitmap userImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        profile.setImageBitmap(userImage);

        otp= (String) bundle.get("OTP");
        Log.e("getOTP: ", otp);
        id=(String) bundle.get("ID");
        Log.e("getID: ", id);

        TextViewTime.setText("00:10:00");
        final CountDownClass timer=new CountDownClass(600000,1000);
        timer.start();

        next=(Button)findViewById(R.id.buttonSubmit);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCircularProgres.showProgressDialog();
                String sOtp = EnterOTP.getText().toString();
                if(id.equals("007")){
                if (sOtp.length() == 6) {
                    if (sOtp.equals(otp)) {
                        mCircularProgres.hideProgressDialog();
                        Toast.makeText(getApplicationContext(), "You Entered a valid OTP", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), SetMessageActivity.class);
                        startActivity(intent);
                    }
                    else if(sOtp.equals(sOTP)){
                        mCircularProgres.hideProgressDialog();
                        Toast.makeText(getApplicationContext(), "You Entered a valid OTP", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), SetMessageActivity.class);
                        startActivity(intent);
                    }

                    else {
                        mCircularProgres.hideProgressDialog();
                        Toast.makeText(getApplicationContext(), "Please Enter a valid OTP", Toast.LENGTH_LONG).show();
                    }
                }
                }

            }
        });
        EnterOTP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String sOtp = EnterOTP.getText().toString();
                if (sOtp.length() == 6) {
                    if (sOtp.equals(otp)) {
                        mCircularProgres.hideProgressDialog();
                        Toast.makeText(getApplicationContext(), "You Entered a valid OTP", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), SetMessageActivity.class);
                        startActivity(intent);
                    }
                    else if(sOtp.equals(sOTP)){
                        mCircularProgres.hideProgressDialog();
                        Toast.makeText(getApplicationContext(), "You Entered a valid OTP", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), SetMessageActivity.class);
                        startActivity(intent);
                    }

                    else {
                        mCircularProgres.hideProgressDialog();
                        Toast.makeText(getApplicationContext(), "Please Enter a valid OTP", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

        resend=(Button)findViewById(R.id.buttonResend);
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCircularProgres.showProgressDialog();
                sendOTP();
                final CountDownClass timer1=new CountDownClass(600000,1000);
                timer1.start();
                next.setEnabled(true);
            }
        });
      //  sendOTP();

    }

    private void sendOTP() {
        ObjectMapper mObjectMapper = new ObjectMapper();
        User objUser = new User();
        objUser.setMobileNo(myInventory.getPhoneNumber());


        String sRegistrationUrlValueToBePassed = "";

        mObjectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            sRegistrationUrlValueToBePassed = mObjectMapper.writeValueAsString(objUser);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        VolleyTaskManager mVolleyTaskManager = new VolleyTaskManager(OTPActivity.this);
        mVolleyTaskManager.doRegistration(OTPurl, sRegistrationUrlValueToBePassed, com.android.volley.Request.Method.POST, true, true, true);

    }

    @Override
    public void onVolleyErrorOccurred(VolleyError response) {

    }

    @Override
    public void getResponse(String response) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
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

        sOTP=mUser.getNumber();
        Log.e("sOTP:",sOTP);
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

        String msg="OTP veification successfully done";
        dbHelper.addNewNotification(Date,Time,msg);
        mCircularProgres.hideProgressDialog();

    }

    @Override
    public void getResponse(JSONArray jsonArray) {

    }

    @Override
    public void getDownloadedImage(Bitmap bitmap) {

    }
    public class CountDownClass extends CountDownTimer{

        public CountDownClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long millis=millisUntilFinished;
            String hms=String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

            TextViewTime.setText(hms);
        }

        @Override
        public void onFinish() {
            TextViewTime.setText("Your OTP is deactivated");
            next.setEnabled(false);
        }
    }
}
