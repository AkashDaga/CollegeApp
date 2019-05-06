package com.example.akash.shield;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.akash.adapters.DataBaseAdapter;
import com.example.akash.adapters.NotificationDBHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SetMessageActivity extends Activity {
    DataBaseAdapter myDB;
    EditText msg;
    Button AddData,Reset;
    circularProgress mCircularProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_message);
        mCircularProgress=new circularProgress(getApplicationContext());

        myDB=new DataBaseAdapter(getApplicationContext());

        msg=(EditText)findViewById(R.id.etMessage);
        AddData=(Button)findViewById(R.id.btWriteMessage);
        Reset=(Button)findViewById(R.id.btResetMessage);

        AddData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mCircularProgress.showProgressDialog();
                String sMsg=msg.getText().toString();
                boolean isInserted=myDB.InsertData(sMsg);

                if(isInserted==true){
                    SharedPreferenceInventory myInventory=new SharedPreferenceInventory(getApplicationContext());
                    myInventory.setMessage(sMsg);
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

                    String msg="successfully set your emergency message";
                    dbHelper.addNewNotification(Date, Time, msg);
//                    mCircularProgress.hideProgressDialog();
                    Toast.makeText(getApplicationContext(),"DATA Successfully stored",Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(getApplicationContext(),ContactListActivity.class);
                    startActivity(intent);

                }
                else{
//                    mCircularProgress.hideProgressDialog();
                    Toast.makeText(getApplicationContext(),"Error occured", Toast.LENGTH_LONG).show();
                }
            }
        });

        Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msg.setText("");
            }
        });
    }

    }
