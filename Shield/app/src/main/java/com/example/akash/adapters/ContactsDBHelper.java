package com.example.akash.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// SQLiteOpenHelper class that manages the local database for this application
public class ContactsDBHelper extends SQLiteOpenHelper {

    // Declaring global variables

// Database Version
private static final int DATABASE_VERSION = 1;
// Database Name
private static final String DATABASE_NAME = "PaySKPDB";

// Constructor that initializes the SQLiteOpenHelper object
public ContactsDBHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
}
// Table creation statements

private static final String CREATE_TABLE_FAVOURITE_LIST = "CREATE TABLE favourite_list (favourite_id varchar(20), contact_name varchar(150),contact_no varchar(150) primary key );";



@Override
public void onCreate(SQLiteDatabase db) {

    // While the database is being created the associated tables are also created along

    db.execSQL(CREATE_TABLE_FAVOURITE_LIST);
    System.out.println(CREATE_TABLE_FAVOURITE_LIST);



}
// onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) callback; [To be used only if the database version needs to be upgraded
// and the schema definition updates should duly be executed here]
@Override
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // TODO Auto-generated method stub
}
// Adds new row to the "favourite_list" table with the specified data
public void addNewFavourite(String contact_name, String contact_no) {

    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues content = new ContentValues();
    content.put("contact_name", contact_name);
    content.put("contact_no", contact_no);

    db.insert("favourite_list", null, content);
    db.close(); // Closing the database Connection to avoid data leak
}
// Fetches all existing rows from the "favourite_list" table as cursor
public Cursor getAllFavourite() {
    SQLiteDatabase db = this.getReadableDatabase();

    Cursor cursor = db.query("favourite_list", new String[] {
            "contact_name", "contact_no" },null, null, null,null, null, null);

    return cursor;
}
    public Integer deleteSelectedFavourite(String id){
        SQLiteDatabase db=this.getWritableDatabase();

        return db.delete("favourite_list","contact_no = ?",new String[] {id});

    }

    public Cursor getContacts(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=db.rawQuery("select * from favourite_list",null);
        return cursor;
    }




}