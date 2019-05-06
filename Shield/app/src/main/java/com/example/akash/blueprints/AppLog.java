package com.example.akash.blueprints;

import android.util.Log;

/**
 * Created by Akash on 12-04-2016.
 */
public class AppLog {
    private static final String APP_TAG="AudioRecorder";

    public static int logString(String message){
        return Log.i(APP_TAG,message);
    }
}
