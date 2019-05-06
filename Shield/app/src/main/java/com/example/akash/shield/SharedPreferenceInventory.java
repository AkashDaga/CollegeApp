package com.example.akash.shield;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Akash on 08-04-2016.
 */
public class SharedPreferenceInventory {

    private SharedPreferences mSharedPref;
    private SharedPreferences.Editor mSharedEditor;
    private Context mContext;
    int PRIVATE_MODE = 0;

    private static final String PREF_FILE_NAME = "ShieldSharedPrefInventory";
    private static final String KEY_PROFILE_PIC_CONVERTED_BASE64 = "PROFILE_PIC_CONVERTED_BASE64";
    private static final String KEY_PHONE_NUMBER = "PHONE_NUMBER";
    private static final String KEY_USER_PIN = "USER_PIN";
    private static final String KEY_USER_NAME = "USER_NAME";
    private static final String KEY_USER_EMAIL = "USER_EMAIL";
    private static final String KEY_IS_EXISTING_USER="EXISTING_USER";
    private static final String KEY_IS_SAVED_MESSAGE="SAVED_MESSAGE";
    private static final String KEY_IS_CURRENT_LONGITUDE="SAVED_CURRENT_LONGITUDE";
    private static final String KEY_IS_CURRENT_LATTITUDE="SAVED_CURRENT_LATTITUDE";
    private static final String KEY_IS_PREVIOUS_LONGITUDE="SAVED_PREVIOUS_LONGITUDE";
    private static final String KEY_IS_PREVIOUS_LATTITUDE="SAVED_PREVIOUS_LATTITUDE";
    private static final String KEY_IS_UPDATED_MESSAGE="SAVE_UPDATED_MESSAGE";



    public SharedPreferenceInventory(Context mContext) {

        this.mContext = mContext;
        mSharedPref = mContext.getSharedPreferences(PREF_FILE_NAME, PRIVATE_MODE);
        mSharedEditor = mSharedPref.edit();
    }

    public SharedPreferenceInventory() {
    }

    public void setUpdatedMessage(String sMessage){
        mSharedEditor.remove(KEY_IS_UPDATED_MESSAGE);
        mSharedEditor.putString(KEY_IS_UPDATED_MESSAGE, sMessage);
        mSharedEditor.commit();

    }
    public String getUpdatdMessage(){
        return mSharedPref.getString(KEY_IS_UPDATED_MESSAGE, "");
    }

    public void setPreviousLattitude(String pLattitude){
        mSharedEditor.remove(KEY_IS_PREVIOUS_LATTITUDE);
        mSharedEditor.putString(KEY_IS_PREVIOUS_LATTITUDE, pLattitude);
        mSharedEditor.commit();

    }
    public String getPreviousLattitude(){
        return mSharedPref.getString(KEY_IS_PREVIOUS_LATTITUDE, "");
    }

    public void setPreviousLongitude(String pLongitude){
        mSharedEditor.remove(KEY_IS_PREVIOUS_LONGITUDE);
        mSharedEditor.putString(KEY_IS_PREVIOUS_LONGITUDE, pLongitude);
        mSharedEditor.commit();

    }
    public String getPreviousLongitude(){
        return mSharedPref.getString(KEY_IS_PREVIOUS_LONGITUDE, "");
    }


    public void setCurrentLongitude(String cLongitude){
        mSharedEditor.remove(KEY_IS_CURRENT_LONGITUDE);
        mSharedEditor.putString(KEY_IS_CURRENT_LONGITUDE, cLongitude);
        mSharedEditor.commit();

    }
    public String getCurrentLongitude(){
        return mSharedPref.getString(KEY_IS_CURRENT_LONGITUDE, "");
    }

    public void setCurrentLattitude(String cLattitude){
        mSharedEditor.remove(KEY_IS_CURRENT_LATTITUDE);
        mSharedEditor.putString(KEY_IS_CURRENT_LATTITUDE, cLattitude);
        mSharedEditor.commit();

    }
    public String getCurrentLattitude(){
        return mSharedPref.getString(KEY_IS_CURRENT_LATTITUDE, "");
    }

    public void setMessage(String sMessage){
        mSharedEditor.remove(KEY_IS_SAVED_MESSAGE);
        mSharedEditor.putString(KEY_IS_SAVED_MESSAGE, sMessage);
        mSharedEditor.commit();
    }
    public String getMessage() {
        return mSharedPref.getString(KEY_IS_SAVED_MESSAGE, "");
    }


    public void setEmail(String sEmail){
        mSharedEditor.remove(KEY_USER_EMAIL);
        mSharedEditor.putString(KEY_USER_EMAIL, sEmail);
        mSharedEditor.commit();
    }
    public String getEmail() {
        return mSharedPref.getString(KEY_USER_EMAIL, "");
    }
    public void setProfileImageConvertedBase64(String sProfileImageConvertedBase64)
    {
        mSharedEditor.remove(KEY_PROFILE_PIC_CONVERTED_BASE64);
        mSharedEditor.putString(KEY_PROFILE_PIC_CONVERTED_BASE64, sProfileImageConvertedBase64);
        mSharedEditor.commit();
    }
    public String getProfilePicConvertedBase64() {
        return mSharedPref.getString(KEY_PROFILE_PIC_CONVERTED_BASE64, "");
    }

    public void setPhoneNumber(String sPhoneNumber)
    {
        mSharedEditor.remove(KEY_PHONE_NUMBER);
        mSharedEditor.putString(KEY_PHONE_NUMBER, sPhoneNumber);
        mSharedEditor.commit();
    }
    public String getPhoneNumber() {
        return mSharedPref.getString(KEY_PHONE_NUMBER, "");
    }

    public void setUserPIN(String sUserPIN)
    {
        mSharedEditor.remove(KEY_USER_PIN);
        mSharedEditor.putString(KEY_USER_PIN, sUserPIN);
        mSharedEditor.commit();
    }
    public String getUserPin() {
       return mSharedPref.getString(KEY_USER_PIN, "");
    }

    public void setUserName(String sUserName)
    {
        mSharedEditor.remove(KEY_USER_NAME);
        mSharedEditor.putString(KEY_USER_NAME, sUserName);
        mSharedEditor.commit();
    }
    public String getUserName() {
        return mSharedPref.getString(KEY_USER_NAME, "");
    }

    public void setKeyIsExistingUser(String sExistingUser)
    {
        mSharedEditor.remove(KEY_IS_EXISTING_USER);
        mSharedEditor.putString(KEY_IS_EXISTING_USER, sExistingUser);
        mSharedEditor.commit();
    }
    public String getKeyIsExistingUser() {
        return mSharedPref.getString(KEY_IS_EXISTING_USER, "");
    }



}

