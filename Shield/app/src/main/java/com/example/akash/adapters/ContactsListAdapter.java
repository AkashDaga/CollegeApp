package com.example.akash.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.akash.blueprints.ContactDetails;
import com.example.akash.blueprints.ImageLoader;
import com.example.akash.shield.R;

import java.util.ArrayList;

// Adapter class that is responsible for populating items to the contact list which is setting this adapter class
public class ContactsListAdapter extends BaseAdapter implements Filterable{

    // Declaring global variables
private Context mContext;
private ArrayList<ContactDetails> contactDetailsList;
private LayoutInflater inflater;
private ImageLoader imageLoader;
private boolean isToHideCheckbox = false;
private ArrayList<ContactDetails> filteredContactDetailsList;

// Constructor for this Adapter class; assesses the list contents and other required values to comprise up the list view
public ContactsListAdapter(Context mContext, ArrayList<ContactDetails> contactDetailsList, boolean isToHideCheckbox) {
    this.mContext = mContext;
    this.contactDetailsList = contactDetailsList;
    this.isToHideCheckbox = isToHideCheckbox;
    this.filteredContactDetailsList = contactDetailsList;
    imageLoader = new ImageLoader(mContext);
}

// Returns list view's current number of items
@Override
public int getCount() {
    return filteredContactDetailsList.size();
}
// Returns the list item container object according to the currently focused position
@Override
public Object getItem(int position) {

    return filteredContactDetailsList.get(position);
}
// Returns list item's id as it's position in the list
@Override
public long getItemId(int position) {
    return position;
}

// Sets up list view individual item's view with respect it's position in the list view
@SuppressLint("InflateParams")
@Override
public View getView(int position, View convertView, ViewGroup parent) {

    ViewHolder mViewHolder;
    // Getting the list item container object from the list content with respect to its position
    final ContactDetails mContactDetails = filteredContactDetailsList.get(position);
    // Initializing(if not done before) the LayoutInflater and then the view for individual items in the list
    if (inflater == null)
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    if (convertView == null) {
        convertView = inflater.inflate(R.layout.contact_list_item, null);

        mViewHolder = new ViewHolder();

        // Initializing each Viewholder(list item view components holder) items
        mViewHolder.tvContactName = (TextView) convertView.findViewById(R.id.tvContactName);
        mViewHolder.tvContactNumber = (TextView) convertView.findViewById(R.id.tvContactNumber);
        mViewHolder.ivContact = (ImageView) convertView.findViewById(R.id.ivContact);
        mViewHolder.chkbxContact = (CheckBox) convertView.findViewById(R.id.chkbxContact);

        convertView.setTag(mViewHolder);

    } else {
        // Get the ViewHolder back to get fast access to the TextView
        // and the ImageView.
        mViewHolder = (ViewHolder) convertView.getTag();
    }

    // Setting the checkbox view as set in the item container object
    mViewHolder.chkbxContact.setChecked(mContactDetails.getIsChecked());

    mViewHolder.chkbxContact
            .setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
											 boolean isChecked) {

					mContactDetails.setIsChecked(isChecked);

				}
			});

    // Setting the item container fetched values into respective TextViews
    mViewHolder.tvContactName.setText(mContactDetails.getsContactName());
    mViewHolder.tvContactNumber
            .setText(mContactDetails.getsContactNumber());

    // Setting the image view image if the current contact item's photo id exists otherwise proceeding with the image set by default
    if (mContactDetails.getContactPhotoId() != 0) {

        imageLoader.DisplayImage("" + mContactDetails.getContactPhotoId(),
                mViewHolder.ivContact);
    } else {
        mViewHolder.ivContact.setImageDrawable(null);
    }

    Log.i("photo id::", "" + mContactDetails.getContactPhotoId());

    Log.i("pos id::", "" + position);

    // Checking the checkbox view visibility status and setting the checkbox view visibility accordingly
    if(isToHideCheckbox)
        mViewHolder.chkbxContact.setVisibility(View.GONE);

    // Finally returning the comprised view
    return convertView;

}

// Filters list comprised items(contact items) according to user's input(filter)text(contact name or number)
@Override
public Filter getFilter() {

    return new Filter() {

        // Updates the filtered result to the list by resetting list content data and notifying the adapter about the data set's
        // been changed
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            ArrayList<ContactDetails> filteredData = (ArrayList<ContactDetails>)results.values;
            ContactsListAdapter.this.filteredContactDetailsList = filteredData;
            ContactsListAdapter.this.notifyDataSetChanged();
        }

        // Performs the list items' filtering according to the user's input
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            FilterResults results = new FilterResults();

            // If there's nothing to filter on, returning the original data for
            // the list
            if (charSequence == null || charSequence.length() == 0) {
                results.values = contactDetailsList;
                results.count = contactDetailsList.size();
            }
            // List data content is being filtered matching the user's input with list data content
            else
            {
                ArrayList<ContactDetails> filterResultsData = new ArrayList<ContactDetails>();

                String filterString = charSequence.toString().toLowerCase();
                String filterableString;

                for (int i = 0; i < contactDetailsList.size(); i++) {
                    ContactDetails mContactDetails = contactDetailsList.get(i);
                    String sContactName = mContactDetails.getsContactName();
                    String sContactNumber = mContactDetails.getsContactNumber();
                    filterableString = sContactName + " " + sContactNumber;
                    if (filterableString.toLowerCase().contains(
                            filterString)) {
                        filterResultsData.add(contactDetailsList.get(i));

                    }

                }

                results.values = filterResultsData;
                results.count = filterResultsData.size();
            }

            return results;
        }
    };
}
// List item view contents holder class
static class ViewHolder
{
    TextView tvContactName, tvContactNumber;
    ImageView ivContact;
    CheckBox chkbxContact;
}


}
