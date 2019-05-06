package com.example.akash.fragment;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.akash.adapters.ContactsDBHelper;
import com.example.akash.adapters.ContactsListAdapter;
import com.example.akash.adapters.NotificationDBHelper;
import com.example.akash.blueprints.ContactDetails;
import com.example.akash.shield.R;
import com.example.akash.shield.SetUpCompleteActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

// Fragment class that displays user's all contacts from its contacts book along with the ones marked previously as favorites
public class AllContactFragment extends Fragment {

    // Declaring global variables
private ListView listView;

private ProgressDialog mProgressDialog;

private ArrayList<ContactDetails> contactDetailsList = new ArrayList<ContactDetails>();

public static ContactsListAdapter mContactsListAdapter;

    private ContactsDBHelper mContactsDBHelper;

public static TextView tvDummyText;
    private ArrayList<ContactDetails> favoriteContactsList = new ArrayList<ContactDetails>();

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.fragment_all_contact, container, false);
    // Instantiating and initializing variables declared earlier
    mProgressDialog = new ProgressDialog(getActivity());
    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    mProgressDialog.setCancelable(false);
    mProgressDialog.setMessage("Loading...");



    listView = (ListView) rootView.findViewById(R.id.listview);
    tvDummyText = (TextView) rootView.findViewById(R.id.tvDummyText);

    mContactsDBHelper = new ContactsDBHelper(getActivity());

    // Fetching all contacts from user's contacts book and setting each items along with the ones marked favorites to the contacts' list
    LoadContact loadContact = new LoadContact();
    loadContact.execute();

    ImageView icon=new ImageView(getActivity());
    icon.setImageResource(R.drawable.contactnext);

    com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton actionButton = new com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton.Builder(getActivity())
            .setContentView(icon)
            .build();

    actionButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(getActivity(), SetUpCompleteActivity.class);
            startActivity(intent);
        }
    });


    return rootView;
}


// Shows up the progress loader dialog if is not currently shown
private void showProgressDialog() {
    if (!mProgressDialog.isShowing())
        mProgressDialog.show();
}
// Hides the progress loader dialog if is currently shown
private void hideProgressDialog() {
    if (mProgressDialog.isShowing())
        mProgressDialog.dismiss();
}
// Async Task class that loads all contacts from user's contact book as then sets the items along with the favorite marked contacts
// to the all contacts' list
class LoadContact extends AsyncTask<Void, Void, Void> {

    // Before execution, starts showing up the loader dialog
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        showProgressDialog();
    }
    // Fetching all contacts from user's contact book
    @Override
    protected Void doInBackground(Void... voids) {
        // Get Contact list from Phone
        contactDetailsList = doFetchAllContacts();
        return null;
    }
    // After execution, hiding the loader dialog and setting up the final list content data comprising all contacts list data as well
    // as favorite contacts list data and so forth setting up the list view
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        hideProgressDialog();

        final ArrayList<ContactDetails> allContactsList = new ArrayList<ContactDetails>();

        for (int x = 0; x < contactDetailsList.size(); x++)
        {
            allContactsList.add(contactDetailsList.get(x));
        }
        // Setting up the adapter as well as the empty view for the list view
        mContactsListAdapter = new ContactsListAdapter(getActivity(), allContactsList, true);
        listView.setEmptyView(tvDummyText);
        listView.setAdapter(mContactsListAdapter);
        // All contacts' list item on click event; advances user to the
        // MoneyTransfer screen for P2P transaction having set the payee number and name as selected item's contact number and name
        // respectively
        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                ContactDetails mContactDetails = (ContactDetails)arg0.getItemAtPosition(arg2);
                mContactsDBHelper.addNewFavourite(mContactDetails.getsContactName(),mContactDetails.getsContactNumber());

                FavouriteContactFragment.doSetUpFavoriteContactsList();
                 }
        });*/

        registerForContextMenu(listView);

        listView.setFastScrollEnabled(true);

    }
}

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater=getActivity().getMenuInflater();
        inflater.inflate(R.menu.list_menu,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info= (AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()){
            case R.id.add:{
                ContactDetails mContactDetails = (ContactDetails) listView.getItemAtPosition(info.position);
                mContactsDBHelper.addNewFavourite(mContactDetails.getsContactName(),mContactDetails.getsContactNumber());
                FavouriteContactFragment.doSetUpFavoriteContactsList();
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
            }

        }
        return super.onContextItemSelected(item);
    }

    // Fetches all contacts unique list data from user's contacts book
private ArrayList<ContactDetails> doFetchAllContacts(){
    ContentResolver cr = getActivity().getContentResolver();

    String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

    Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, sortOrder);
    int i = 0;


    ArrayList<ContactDetails> contactDetailsList = new ArrayList<ContactDetails>();

    if (cursor.getCount() > 0) {
        String id_arr[] = new String[cursor.getCount()];
        while (cursor.moveToNext()) {
            id_arr[i] = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[]{id_arr[i]}, null);

                String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String contactNumber = "";
                int photoId = 0;
                while (pCur.moveToNext()) {
                    contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    contactNumber = contactNumber.replaceAll("[^0-9]", "");

                    if ( contactNumber.length() >10)
                    {
                        contactNumber = contactNumber.substring(contactNumber.length()-10);
                    }
                    photoId = pCur.getInt(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_ID));
                    ContactDetails mContactDetails = new ContactDetails();
                    mContactDetails.setsContactName(contactName);
                    mContactDetails.setsContactNumber(contactNumber.trim());
                    mContactDetails.setContactPhotoId(photoId);
                    mContactDetails.setIsChecked(false);

                    if(!contactDetailsList.contains(mContactDetails) && !(contactNumber.length() <10 || contactNumber.substring(0,1).equals("0")))
                        contactDetailsList.add(mContactDetails);
                }
                pCur.close();
                i++;
            }

        }
    }

    return contactDetailsList;

}

}
