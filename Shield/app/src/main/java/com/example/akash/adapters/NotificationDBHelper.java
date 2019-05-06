package com.example.akash.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.akash.blueprints.Notification;

import java.util.ArrayList;

/**
 * Created by Akash on 12-05-2016.
 */
public class NotificationDBHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "ShieldNotificationDB";
    public NotificationDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String CREATE_TABLE_NOTIFICATION_LIST = "CREATE TABLE notification_list (notification_id integer primary key autoincrement, notification_date varchar(150), notification_time varchar(150), notification_msg varchar(200));";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_NOTIFICATION_LIST);
        System.out.println(CREATE_TABLE_NOTIFICATION_LIST);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addNewNotification(String date, String time, String msg) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues content = new ContentValues();
        content.put("notification_date", date);
        content.put("notification_time", time);
        content.put("notification_msg", msg);

        db.insert("notification_list", null, content);
        db.close(); // Closing the database Connection to avoid data leak
    }
    public ArrayList<Notification> getAllNotification() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Notification> rowItems = new ArrayList<Notification>();
        Cursor cursor=db.rawQuery("select * from notification_list",null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
        rowItems.add(new Notification(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3)));
            cursor.moveToNext();
        }
        cursor.close();

        return rowItems;
    }

}
