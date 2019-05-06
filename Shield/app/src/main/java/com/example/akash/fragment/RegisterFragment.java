package com.example.akash.fragment;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.example.akash.adapters.NotificationDBHelper;
import com.example.akash.shield.OTPActivity;
import com.example.akash.shield.R;
import com.example.akash.shield.ResponseAlert;
import com.example.akash.shield.ServerResponseCallback;
import com.example.akash.shield.SharedPreferenceInventory;
import com.example.akash.shield.VolleyTaskManager;
import com.example.akash.shield.circularProgress;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.android.gms.plus.Plus;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegisterFragment extends Fragment  {
    View rootView;
    private EditText etName,etGender,etDOB, etEmail, etPassword, etRetypePassword, etPin, etPhone;
    private String sName, sEmail, sPassword, sRetypePassword, sPin, sPhone, sDOB,sGender,sPhoto;

    private Calendar mCalendar;
    private int iDay, iMonth, iYear;
    public circularProgress mCircularProgress;
    private ImageView ivProfile;
    Button btnRegister, btnReset;
    Dialog proImageOptionDialog;
    private int CAPTURE_NEW_PICTURE = 1111;
    private int ACTIVITY_SELECT_IMAGE = 101;
    final private int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    final private int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 2;
    int CameraPermission, WriteExternalStoragePermission;
    public User mUser;
    public SharedPreferenceInventory myInventory;
    String sRegistrationUrl="http://192.168.0.8:8084/MCETSuraksha/ServletRegister";

    String sRegistrationUrlValueToBePassed = "";
    private SimpleDateFormat format;

    public RegisterFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        rootView = inflater.inflate(R.layout.fragment_register, container, false);
        mUser=new User();

        myInventory=new SharedPreferenceInventory(getActivity());
        mCircularProgress=new circularProgress(getActivity());

        etName = (EditText) rootView.findViewById(R.id.etName);
        etEmail = (EditText) rootView.findViewById(R.id.etEmailId);
        etPassword = (EditText) rootView.findViewById(R.id.etPassword);
        etRetypePassword = (EditText) rootView.findViewById(R.id.etRetypePassword);
        etPin = (EditText) rootView.findViewById(R.id.etPin);
        etPhone = (EditText) rootView.findViewById(R.id.etPhoneNumber);
        etGender=(EditText)rootView.findViewById(R.id.etGender);
        etDOB = (EditText) rootView.findViewById(R.id.etBirthday);
        etDOB.setInputType(InputType.TYPE_NULL);
        etDOB.requestFocus();
        ivProfile = (ImageView) rootView.findViewById(R.id.ivProfilePic);
        btnRegister = (Button) rootView.findViewById(R.id.btSignUp);
        btnReset = (Button) rootView.findViewById(R.id.btReset);

        proImageOptionDialog = new Dialog(getActivity());
        proImageOptionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        proImageOptionDialog.setContentView(R.layout.profile_image_option);

        etPhone.setRawInputType(Configuration.KEYBOARD_12KEY);

        format=new SimpleDateFormat("dd-MM-yyyy",Locale.US);

        etDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });




        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.profile);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bm, 200, 200, false);
        Bitmap circledBitmap = getCircledBitmap(resizedBitmap, 1);
        ivProfile.setImageBitmap(circledBitmap);
        doSaveInSharedPreference(circledBitmap);



        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proImageOptionDialog.show();

                Button btnCapture = (Button) proImageOptionDialog.findViewById(R.id.btnCapture);
                Button btnBrowse = (Button) proImageOptionDialog.findViewById(R.id.btnBrowse);

                btnCapture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        permissionCheckCamera();
                    }
                });
                btnBrowse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        permissionCheckWriteExternalStorage();
                    }
                });

            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sName=etName.getText().toString().trim();
                sEmail=etEmail.getText().toString().trim();
                sPassword=etPassword.getText().toString().trim();
                sRetypePassword=etRetypePassword.getText().toString().trim();
                sPin=etPin.getText().toString().trim();
                sPhone=etPhone.getText().toString().trim();
                sDOB=etDOB.getText().toString().trim();
                sGender=etGender.getText().toString();


                Log.e("Name", sName);
                Log.e("Email", sEmail);
                Log.e("Password", sPassword);
                Log.e("Retype password", sRetypePassword);
                Log.e("Pin", sPin);
                Log.e("phone", sPhone);
                Log.e("DOB", sDOB);
                Log.e("Gender", sGender);


                if(TextUtils.isEmpty(sPhone) || sPhone.length() < 10 || sPhone.startsWith("00000") || sPhone.contains("9999999999"))
                {
                    etPhone.setError("Please enter a valid phone number!");
                }
                else if(!isValidEmail(sEmail))
                {
                    etEmail.setError("Please enter a valid E-mail to register!");
                }
                else if(TextUtils.isEmpty(sPin) || sPin.length() < 6)
                {
                    // TODO review this
                    etPin.setError("You must have 6 characters in your PIN!");
                }
                else
                    doRegisterCustomer();
            }
        });



        return rootView;
    }

    private void doSaveInSharedPreference(Bitmap mBitmap) {
        ivProfile.setImageBitmap(mBitmap);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();

        //make byte array in base64
        sPhoto = Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void doRegisterCustomer() {
        mCircularProgress.showProgressDialog();
        ObjectMapper mObjectMapper = new ObjectMapper();
        User objUser = new User();
        objUser.setName(sName);
        objUser.setPassword(sPassword);
        objUser.setRetype_password(sRetypePassword);
        objUser.setPIN(sPin);
        objUser.setMobileNo(sPhone);
        objUser.setDOB(sDOB);
        objUser.setEmail(sEmail);
        objUser.setGender(sGender);



        mObjectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            sRegistrationUrlValueToBePassed = mObjectMapper.writeValueAsString(objUser);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        Log.e("req", sRegistrationUrlValueToBePassed);
        StringRequest mStringRequest = new StringRequest(Request.Method.POST, sRegistrationUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String sResponse) {

//                if(isToHideDialog) {
//                    hideProgressDialog();
//                }


                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.enable(SerializationFeature.INDENT_OUTPUT);


                try {
                    mUser = objectMapper.readValue(sResponse,User.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }



                if(mUser.getResult() == true)
                    {
                        Toast.makeText(getContext(),"Registration Successfully done",Toast.LENGTH_LONG).show();
                        Log.e("Result:", sResponse + "fff");

                        myInventory.setEmail(sEmail);
                        myInventory.setUserPIN(sPin);
                        myInventory.setUserName(sName);
                        myInventory.setPhoneNumber(sPhone);
                        myInventory.setProfileImageConvertedBase64(sPhoto);

                        NotificationDBHelper dbHelper=new NotificationDBHelper(getActivity());
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

                        String msg="Your Profile is created";
                        dbHelper.addNewNotification(Date, Time, msg);

                        mCircularProgress.hideProgressDialog();
                        String num=mUser.getNumber();
                        Log.e("otp is: ",num);
                        Intent intent=new Intent(getActivity(),OTPActivity.class);
                        intent.putExtra("OTP",num);
                        intent.putExtra("ID","007");
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(getContext(),"Something goes wrong, resend OTP",Toast.LENGTH_LONG).show();
                        Intent intent=new Intent(getActivity(),OTPActivity.class);
                        mCircularProgress.hideProgressDialog();
                        startActivity(intent);
                    }

                }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                ResponseAlert mResponseAlert = new ResponseAlert(getActivity());

//                hideProgressDialog();
//                VolleyLog.d(TAG, "Error: " + error.getMessage());

                if (error instanceof TimeoutError) {
                    Log.d("error ocurred", "TimeoutError");
                    mResponseAlert.showWarningAlert("Timeout Error", "Unable to connect server ");
                } else if (error instanceof NoConnectionError) {
                    Log.d("error ocurred", "No Connection");
                    mResponseAlert.showWarningAlert("No Connection", "Please check your internet connection");
                } else if (error instanceof AuthFailureError) {
                    Log.d("error ocurred", "AuthFailureError");
                    mResponseAlert.showWarningAlert("Auth Failure Error", "Unable to authenticate server");
                } else if (error instanceof ServerError) {
                    Log.d("error ocurred", "ServerError");
                    mResponseAlert.showWarningAlert("Server Error", "Unknown server error");
                } else if (error instanceof NetworkError) {
                    Log.d("error ocurred", "NetworkError");
                    mResponseAlert.showWarningAlert("Network Error", "Network error. Please try again ");
                } else if (error instanceof ParseError) {
                    Log.d("error ocurred", "ParseError");
                    mResponseAlert.showWarningAlert("Error", "Server error, please try again. Please contact administrator.");
                }


                mCircularProgress.hideProgressDialog();
//                VolleyTaskManager mVolleyTaskManager
//                = new VolleyTaskManager(getContext());
//        mVolleyTaskManager.doRegistration(sRegistrationUrl, sRegistrationUrlValueToBePassed, com.android.volley.Request.Method.POST, true, true, true);

            }
        })
        {
            @Override
            public byte[] getBody() throws AuthFailureError {

                return sRegistrationUrlValueToBePassed.getBytes();
            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();

                    params.put("Accept", "application/json");


                return params;
            }


            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            protected String getPostParamsEncoding() {

                return "application/json;charset=UTF-8";
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(180000,0,1));
        com.example.akash.shield.RequestQueue.getInstance().addToRequestQueue(mStringRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==ACTIVITY_SELECT_IMAGE  && resultCode == Activity.RESULT_OK)
        {

            Uri selectedImageUri = data.getData();
            String[] sFilePathArray = { MediaStore.Images.Media.DATA };
            Cursor imageCursor = getActivity().getContentResolver().query(selectedImageUri, sFilePathArray, null, null, null);
            imageCursor.moveToFirst();
            int columnIndex = imageCursor.getColumnIndex(sFilePathArray[0]);
            String sPicturePath = imageCursor.getString(columnIndex);
            imageCursor.close();
            Bitmap bmpThumbnail = (BitmapFactory.decodeFile(sPicturePath));

            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bmpThumbnail, 100, 100, false);
            Bitmap circledBitmap = getCircledBitmap(resizedBitmap, 100);

            doSaveInSharedPreference(circledBitmap);

        }
    }

    private boolean isValidEmail(String sEmail) {
        if (sEmail == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(sEmail).matches();
        }
    }

    private void permissionCheckCamera() {
        CameraPermission = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA);

        if (CameraPermission == PackageManager.PERMISSION_GRANTED) {

            Intent iCameraImageCapture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(iCameraImageCapture, CAPTURE_NEW_PICTURE);
            proImageOptionDialog.cancel();

        } else {
//			Log.e("Heading", "Permission to record denied");
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }
    }

    private void permissionCheckWriteExternalStorage() {
        WriteExternalStoragePermission = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (WriteExternalStoragePermission == PackageManager.PERMISSION_GRANTED) {

            Intent iMediaImagesPick = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(iMediaImagesPick, ACTIVITY_SELECT_IMAGE);
            proImageOptionDialog.cancel();
        } else {
//			Log.e("Heading", "Permission to record denied");
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    public Dialog onCreateDialog(int id) {
        if (id == 999) {
            return new DatePickerDialog(getActivity(), myDateListener, iYear, iMonth, iDay);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            arg2++;
            String ar2 = "", ar3 = ""; //arg0=view,arg1=year,arg2=month,arg3=day

            if (arg2 < 10)
                ar2 = "0" + arg2;
            else
                ar2 = "" + arg2;
            if (arg3 < 10)
                ar3 = "0" + arg3;
            else
                ar3 = "" + arg3;

            etDOB.setText(new StringBuilder().append(arg1).append("-").append(ar2).append("-").append(ar3));
        }
    };

    private Bitmap getCircledBitmap(Bitmap bitmap, int pixel) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), android.graphics.Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent iCameraImageCapture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(iCameraImageCapture, CAPTURE_NEW_PICTURE);
                    proImageOptionDialog.cancel();

                } else {

                    Toast.makeText(getActivity(), "CAMERA ACCESS Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent iMediaImagesPick = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(iMediaImagesPick, ACTIVITY_SELECT_IMAGE);
                    proImageOptionDialog.cancel();

                } else {

                    Toast.makeText(getActivity(), "READ EXTERNAL STORAGE ACCESS Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }



}
