package com.example.akash.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.akash.blueprints.CommunityListModel;
import com.example.akash.blueprints.Notification;
import com.example.akash.shield.R;

import java.util.ArrayList;


public class NotificationListAdapter extends BaseAdapter {
    public Context context;
    public ArrayList<Notification> rowItems;
    private LayoutInflater inflater;

    public NotificationListAdapter(Context context, ArrayList<Notification> rowItems) {
        this.context = context;
        this.rowItems = rowItems;
    }

    @Override
    public int getCount() {
        return rowItems.size();
    }

    @Override
    public Object getItem(int position) {
        return rowItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return rowItems.indexOf(getItem(position));
    }
    class ViewHolder{
        TextView id,notification,date,time;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        final Notification mNotificationDetails = rowItems.get(position);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.notification_list_layout, null);

            mViewHolder = new ViewHolder();

            // Initializing each Viewholder(list item view components holder) items
            mViewHolder.id = (TextView) convertView.findViewById(R.id.tvNotificationId);
            mViewHolder.notification = (TextView) convertView.findViewById(R.id.tvNotificationMsg);
            mViewHolder.date = (TextView) convertView.findViewById(R.id.tvNotificationDate);
            mViewHolder.time = (TextView) convertView.findViewById(R.id.tvNotificationTime);

            convertView.setTag(mViewHolder);
        }
        else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.id.setText(mNotificationDetails.getId());
        mViewHolder.notification.setText(mNotificationDetails.getMessage());
        mViewHolder.date.setText(mNotificationDetails.getSystemDate());
        mViewHolder.time.setText(mNotificationDetails.getSystemDate());

        return convertView;
    }
}
