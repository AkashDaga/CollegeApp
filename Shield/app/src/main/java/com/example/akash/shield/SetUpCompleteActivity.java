package com.example.akash.shield;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.akash.adapters.NotificationDBHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SetUpCompleteActivity extends AppCompatActivity {
    Button GoToAppDrawer,Exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_complete);
        GoToAppDrawer=(Button)findViewById(R.id.btHome);
        Exit=(Button)findViewById(R.id.btExit);

        GoToAppDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                String msg="you complete the application set up successfully";
                dbHelper.addNewNotification(Date, Time, msg);
                Intent intent=new Intent(getApplicationContext(),AppDrawerActivity.class);
                startActivity(intent);

                SharedPreferenceInventory myInventory=new SharedPreferenceInventory(getApplicationContext());
                myInventory.setKeyIsExistingUser("true");
            }
        });
    }
}
