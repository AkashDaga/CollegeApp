package com.example.akash.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.akash.blueprints.CommunityListModel;
import com.example.akash.shield.R;

import java.util.ArrayList;

/**
 * Created by Akash on 22-04-2016.
 */
public class CommunityListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<CommunityListModel> rowitems;


    public CommunityListAdapter(Context context, ArrayList<CommunityListModel> rowitems) {
        this.context = context;
        this.rowitems = rowitems;
    }

    @Override
    public int getCount() {
        return rowitems.size();
    }

    @Override
    public Object getItem(int position) {
        return rowitems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return rowitems.indexOf(getItem(position));
    }

    class ViewHolder{
        TextView userName;
        TextView SystemDate;
        TextView SystemTime;
        TextView post;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        holder=new ViewHolder();
        if(convertView == null){
            convertView=inflater.inflate(R.layout.community_list_row_layout,null);
            holder.post=(TextView)convertView.findViewById(R.id.CommunityPost);
            holder.userName=(TextView)convertView.findViewById(R.id.CommnityUserName);
            holder.SystemDate=(TextView)convertView.findViewById(R.id.CommunityDate);
            holder.SystemTime=(TextView)convertView.findViewById(R.id.CommunityTime);
            convertView.setTag(holder);
        }

        else{
            holder=(ViewHolder) convertView.getTag();
        }

        final CommunityListModel row_pos=rowitems.get(position);

        holder.userName.setText(row_pos.getUserName());
        holder.SystemDate.setText(row_pos.getSystemDate());
        holder.SystemTime.setText(row_pos.getSysteTime());
        holder.post.setText(row_pos.getPost());

        return convertView;
    }
}
