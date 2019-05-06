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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.example.akash.adapters.ContactsDBHelper;
import com.example.akash.adapters.ContactsListAdapter;
import com.example.akash.blueprints.ContactDetails;
import com.example.akash.shield.R;

import java.util.ArrayList;

// Fragment class that displays all contacts those have been marked as favorites fetched from local database
public class FavouriteContactFragment extends Fragment {

    // Declaring global variables
private static ListView listView;
private static ContactsDBHelper mContactsDBHelper;
public static ContactsListAdapter mContactsListAdapter;
private static ArrayList<ContactDetails> favoriteContactsList = new ArrayList<ContactDetails>();
public static TextView tvDummyText;

    static Activity act =null;

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.fragment_favourite_contact, container, false);
    act = getActivity();
    // Instantiating and initializing variables declared earlier
    listView = (ListView) rootView.findViewById(R.id.listview);
    tvDummyText = (TextView) rootView.findViewById(R.id.tvDummyText);

    mContactsDBHelper = new ContactsDBHelper(getActivity());
    // Fetching all favorite contacts from local database and setting to the favorites' list
    doSetUpFavoriteContactsList();

    return rootView;
}
// Fetches user's all the favorite marked contacts from local database and sets up the favorite contacts' list content data
public static void doSetUpFavoriteContactsList()
{

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

    listView.setEmptyView(tvDummyText);
    listView.setAdapter(mContactsListAdapter);
    listView.setFastScrollEnabled(true);
}
}
