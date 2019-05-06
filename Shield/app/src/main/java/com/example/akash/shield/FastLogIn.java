package com.example.akash.shield;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class FastLogIn extends AppCompatActivity {
    Button submit,reset;
    ImageView profile;
    EditText pin;
    String Spin;
    SharedPreferenceInventory myInventory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fast_log_in);

        profile=(ImageView)findViewById(R.id.FastLogInProfile);

        myInventory=new SharedPreferenceInventory(getApplicationContext());
        byte[] decodedString= Base64.decode(myInventory.getProfilePicConvertedBase64(), Base64.DEFAULT);
        Bitmap userImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        profile.setImageBitmap(userImage);

        pin=(EditText)findViewById(R.id.etPINFastLogIn);

        Spin=myInventory.getUserPin();

        submit=(Button)findViewById(R.id.FastLogInSubmit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input=pin.getText().toString();
                if(Spin.equals(input)){
                    Intent intent=new Intent(getApplicationContext(),AppDrawerActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Please Enter a valid PIN",Toast.LENGTH_LONG).show();
                }
            }
        });

        reset=(Button)findViewById(R.id.FastLogInReset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pin.setText("");
            }
        });

    }
}
