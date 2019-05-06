package com.example.akash.fragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.akash.adapters.NotificationDBHelper;
import com.example.akash.adapters.NotificationListAdapter;
import com.example.akash.blueprints.Notification;
import com.example.akash.shield.AppDrawerActivity;
import com.example.akash.shield.R;
import com.example.akash.shield.SharedPreferenceInventory;
import com.example.akash.shield.circularProgress;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {
    public View rootView;
    public ArrayList<Notification> notify;
    public ImageView image;
    public TextView name,email;
    public ListView myList;
    public SharedPreferenceInventory myInventory;
    public circularProgress mCircularProgress;


    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.fragment_notification, container, false);
        myList=(ListView)rootView.findViewById(R.id.NotificationList);

        mCircularProgress=new circularProgress(getActivity());
        myInventory=new SharedPreferenceInventory(getActivity());

        image=(ImageView)rootView.findViewById(R.id.NotificationProfile);
        byte[] decodedString= Base64.decode(myInventory.getProfilePicConvertedBase64(), Base64.DEFAULT);
        Bitmap userImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        image.setImageBitmap(userImage);

        name=(TextView)rootView.findViewById(R.id.Notification_UserName);
        name.setText(myInventory.getUserName());

        email=(TextView)rootView.findViewById(R.id.Notification_email);
        email.setText(myInventory.getEmail());


        getNotification();
        return rootView;
    }

    private void getNotification() {
        mCircularProgress.showProgressDialog();
        NotificationDBHelper dbHelper=new NotificationDBHelper(getActivity());
        notify=dbHelper.getAllNotification();
        NotificationListAdapter adapter=new NotificationListAdapter(getActivity(),notify);
        myList.setAdapter(adapter);
        mCircularProgress.hideProgressDialog();
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getActivity(), AppDrawerActivity.class);
                startActivity(intent);
            }
        });
    }

}
