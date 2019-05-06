package com.example.akash.fragment;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.akash.adapters.DataBaseAdapter;
import com.example.akash.adapters.NotificationDBHelper;
import com.example.akash.shield.R;
import com.example.akash.shield.SharedPreferenceInventory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditMessageFragment extends Fragment {
    View rootView;
    DataBaseAdapter myDB;
    EditText msg;
    Button UpdateData,Reset;


    public EditMessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =inflater.inflate(R.layout.fragment_edit_message, container, false);

        myDB=new DataBaseAdapter(getActivity());

        msg=(EditText)rootView.findViewById(R.id.etSetMessage);
        UpdateData=(Button)rootView.findViewById(R.id.btnUpdatemessage);
        Reset=(Button)rootView.findViewById(R.id.ResetMessage);

        getAllData();

        UpdateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Write update code
               boolean isUpdated= myDB.updateData(msg.getText().toString());

                if(isUpdated == true){
                    Toast.makeText(getActivity(),"DATA UPDATED SUCCESSFULLY",Toast.LENGTH_LONG).show();
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

                    String msg="Your emergency message is updated";
                    dbHelper.addNewNotification(Date,Time,msg);
                }
                else{
                    Toast.makeText(getActivity(),"DATA IS NOT UPDATED SUCCESSFULLY",Toast.LENGTH_LONG).show();
                }
            }
        });

        Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msg.setText("");
            }
        });

        return rootView;
    }

    private void getAllData() {
        String result=myDB.getAllData();
        if(result == null){
            Toast.makeText(getActivity(),"There is no Data Available",Toast.LENGTH_LONG).show();
        }
        else{
            msg.setText(result);

        }
//        if(result.getCount() == 0){
//            Toast.makeText(getActivity(),"There is no Data Available",Toast.LENGTH_LONG).show();
//            return;
//        }
//        else{
//
//        StringBuffer buffer=new StringBuffer();
//            while(result.moveToNext()){
//                buffer.append(result.getString(1));
//            }
//            msg.setText(buffer.toString());
//        }
    }
}
