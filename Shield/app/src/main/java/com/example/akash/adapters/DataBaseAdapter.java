package com.example.akash.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Akash on 15-04-2016.
 */
public class DataBaseAdapter extends SQLiteOpenHelper {
    public static final String DATABASE_NAME="Shield.db";
    public static final String TABLE_NAME="Message";
    public static final String COL_1="SET_MESSAGE";
    public DataBaseAdapter(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query="create table " + TABLE_NAME +" (SET_MESSAGE TEXT)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean InsertData(String message){
        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues contentValue=new ContentValues();
        contentValue.put(COL_1, message);
        long result=db.insert(TABLE_NAME,null,contentValue);
        if(result==-1)
            return false;
        else
            return true;
    }
       public String getAllData(){
        SQLiteDatabase db=this.getWritableDatabase();
        String msg="";
        Cursor c=db.rawQuery("select * from "+TABLE_NAME,null);
           c.moveToFirst();
           while(!c.isAfterLast()){
               msg=c.getString(0);
           }

        return msg;
    }

    public boolean updateData(String message){
        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues contentValues=new ContentValues();
        contentValues.put(COL_1, message);


        db.update(TABLE_NAME,contentValues,"SET_MESSAGE = ?", new String[] {message });
        return true;

    }

}
