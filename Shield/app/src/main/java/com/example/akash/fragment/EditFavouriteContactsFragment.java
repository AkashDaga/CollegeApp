package com.example.akash.fragment;


import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.akash.adapters.ContactsDBHelper;
import com.example.akash.adapters.ContactsListAdapter;
import com.example.akash.adapters.NotificationDBHelper;
import com.example.akash.blueprints.ContactDetails;
import com.example.akash.shield.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditFavouriteContactsFragment extends Fragment {
    private static ListView listView;
    private static ContactsDBHelper mContactsDBHelper;
    public static ContactsListAdapter mContactsListAdapter;
    private static ArrayList<ContactDetails> favoriteContactsList = new ArrayList<ContactDetails>();
    private View rootView;

    static Activity act =null;

    public EditFavouriteContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView= inflater.inflate(R.layout.fragment_edit_favourite_contacts, container, false);
        act = getActivity();
        // Instantiating and initializing variables declared earlier
        listView = (ListView) rootView.findViewById(R.id.editContacts);

        mContactsDBHelper = new ContactsDBHelper(getActivity());
        // Fetching all favorite contacts from local database and setting to the favorites' list
        registerForContextMenu(listView);
        doSetUpFavoriteContactsList();

        return rootView;
    }

    private void doSetUpFavoriteContactsList() {
        favoriteContactsList.clear();

        Cursor cursor = mContactsDBHelper.getAllFavourite();
        if (cursor != null && cursor.moveToFirst()) {
            do {

                String sConName = cursor.getString(0);
                String sConNo = cursor.getString(1);

                Log.e("contact_details", sConName + " " + sConNo);

                ContactDetails mContactDetails = new ContactDetails();
                mContactDetails.setsContactName(sConName);
                mContactDetails.setsContactNumber(sConNo);
                mContactDetails.setContactPhotoId(0);
                mContactDetails.setIsChecked(false);

                if(!(sConNo.length() <10 || sConNo.substring(0,1).equals("0")))
                {
                    favoriteContactsList.add(mContactDetails);
                }

            }
            while (cursor.moveToNext());
        }
        cursor.close();

        // Initializing and setting up the list adapter to the favorites contact list
        mContactsListAdapter = new ContactsListAdapter(act, favoriteContactsList, true);


        listView.setAdapter(mContactsListAdapter);

        listView.setFastScrollEnabled(true);
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater=getActivity().getMenuInflater();
        inflater.inflate(R.menu.list_delete, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info= (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()){
            case R.id.delete:{
                ContactDetails mContactDetails = (ContactDetails) listView.getItemAtPosition(info.position);
                Integer deletedRows=mContactsDBHelper.deleteSelectedFavourite(mContactDetails.getsContactNumber());
                if(deletedRows>0){
                    //mContactsListAdapter.notifyDataSetChanged();
                    doSetUpFavoriteContactsList();
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

                    String msg="Your favourite contact list is updated";
                    dbHelper.addNewNotification(Date,Time,msg);
                    Toast.makeText(getActivity(),"Contact deleted Successfully",Toast.LENGTH_LONG).show();
                    return true;
                }else{
                    Toast.makeText(getActivity(),"There is some problem in deleting contacts",Toast.LENGTH_LONG).show();
                }
            }

        }
        return super.onContextItemSelected(item);
    }
}
