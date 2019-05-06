package com.example.akash.shield;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;

/**
 * Created by Akash on 09-04-2016.
 */
public class RequestQueue extends Application {
    public static final String TAG = RequestQueue.class.getSimpleName();

    private com.android.volley.RequestQueue mRequestQueue;

    private static RequestQueue mAppInstance;

    private static boolean isActivityVisible;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppInstance = this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static synchronized RequestQueue getInstance() {
        return mAppInstance;
    }

    public com.android.volley.RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public static boolean isActivityVisible() {
        return isActivityVisible;
    }

    public static void activityResumed() {
        isActivityVisible = true;
    }

    public static void activityPaused() {
        isActivityVisible = false;
    }


}
