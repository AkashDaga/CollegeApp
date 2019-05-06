package com.example.akash.shield;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.jar.Manifest;

public class SignUpActivity extends AppCompatActivity {
    private EditText etFirstName,etLastName,etEmail,etPassword,etRetypePassword,etPin,etPhone;
    private String sFirstName,sLastName,sEmail,sPassword,sRetypePassword,sPin,sPhone,sDOB;
    private TextView tvDOB;
    private RadioGroup radioGroup;
    private RadioButton rb1,rb2,rb3;
    private Calendar mCalendar;
    private int iDay,iMonth,iYear;
    private ImageView ivProfile;
    Button btnRegister,btnReset;
    Dialog proImageOptionDialog;
    private int CAPTURE_NEW_PICTURE=1111;
    private int ACTIVITY_SELECT_IMAGE = 101;
    final private int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    final private int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 2;
    final private int MY_PERMISSIONS_REQUEST_SMS = 3;

    int CameraPermission, WriteExternalStoragePermission,SMSReadPermission,SMSReceivedPermission;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(Build.VERSION.SDK_INT>9){
            StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sign_up);

        etFirstName=(EditText)findViewById(R.id.etFirstName);
        etLastName=(EditText)findViewById(R.id.etLastName);
        etEmail=(EditText)findViewById(R.id.etEmailId);
        etPassword=(EditText)findViewById(R.id.etPassword);
        etRetypePassword=(EditText)findViewById(R.id.etRetypePassword);
        etPin=(EditText)findViewById(R.id.etPin);
        etPhone=(EditText)findViewById(R.id.etPhoneNumber);
        tvDOB=(TextView)findViewById(R.id.tvBirthday);
        ivProfile=(ImageView)findViewById(R.id.ivProfilePic);
        btnRegister=(Button)findViewById(R.id.btSignUp);
        btnReset=(Button)findViewById(R.id.btReset);

        proImageOptionDialog=new Dialog(SignUpActivity.this);
        proImageOptionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        proImageOptionDialog.setContentView(R.layout.profile_image_option);

        etPhone.setRawInputType(Configuration.KEYBOARD_12KEY);

        mCalendar=Calendar.getInstance();
        iYear=mCalendar.get(Calendar.YEAR);
        iMonth=mCalendar.get(Calendar.MONTH);
        iDay=mCalendar.get(Calendar.DAY_OF_MONTH);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.profile);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bm, 200, 200, false);
        Bitmap circledBitmap = getCircledBitmap(resizedBitmap, 100);

        tvDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(999);
            }
        });

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proImageOptionDialog.show();

                Button btnCapture=(Button)proImageOptionDialog.findViewById(R.id.btnCapture);
                Button btnBrowse=(Button)proImageOptionDialog.findViewById(R.id.btnBrowse);

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
                        sFirstName=etFirstName.getText().toString().trim();
                        sLastName=etLastName.getText().toString().trim();
                        sEmail=etEmail.getText().toString().trim();
                        sPassword=etPassword.getText().toString().trim();
                        sRetypePassword=etRetypePassword.getText().toString().trim();
                        sPin=etPin.getText().toString().trim();
                        sPhone=etPhone.getText().toString().trim();
                        sDOB=tvDOB.getText().toString().trim();

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

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, iYear, iMonth, iDay);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int arg1, int arg2, int arg3) {
            arg2++;
            String ar2="",ar3="";

            if(arg2<10)
                ar2="0"+arg2;
            else
                ar2=""+arg2;
            if(arg3<10)
                ar3="0"+arg3;
            else
                ar3=""+arg3;

            tvDOB.setText(new StringBuilder().append(arg1).append("-").append(ar2).append("-").append(ar3));
        }
    };

    private Bitmap getCircledBitmap(Bitmap bitmap, int pixels) {
        Bitmap output=Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), android.graphics.Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(),
                bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2,
                bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    private void doRegisterCustomer() {


    }

    private boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private void permissionCheckCamera()
    {
        CameraPermission = ContextCompat.checkSelfPermission(SignUpActivity.this,android.Manifest.permission.CAMERA);

        if (CameraPermission == PackageManager.PERMISSION_GRANTED ) {

            Intent iCameraImageCapture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(iCameraImageCapture, CAPTURE_NEW_PICTURE);
            proImageOptionDialog.cancel();

        }
        else
        {
//			Log.e("Heading", "Permission to record denied");
            ActivityCompat.requestPermissions(SignUpActivity.this,new String[]{android.Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }
    }
    private void permissionCheckWriteExternalStorage()
    {
        WriteExternalStoragePermission = ContextCompat.checkSelfPermission(SignUpActivity.this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (WriteExternalStoragePermission == PackageManager.PERMISSION_GRANTED) {

            Intent iMediaImagesPick = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(iMediaImagesPick, ACTIVITY_SELECT_IMAGE);
            proImageOptionDialog.cancel();
        }
        else
        {
//			Log.e("Heading", "Permission to record denied");
            ActivityCompat.requestPermissions(SignUpActivity.this,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_CAMERA:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED )
                {
                    Intent iCameraImageCapture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(iCameraImageCapture, CAPTURE_NEW_PICTURE);
                    proImageOptionDialog.cancel();

                } else
                {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    Toast.makeText(SignUpActivity.this, "CAMERA ACCESS Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED )
                {
                    Intent iMediaImagesPick = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(iMediaImagesPick, ACTIVITY_SELECT_IMAGE);
                    proImageOptionDialog.cancel();

                } else
                {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    Toast.makeText(SignUpActivity.this, "READ EXTERNAL STORAGE ACCESS Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }


}
