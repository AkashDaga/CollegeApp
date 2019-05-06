package com.example.akash.shield;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Akash on 15-05-2016.
 */
public class circularProgress {
    private Context mContext;
    private ProgressDialog mProgressDialog;
//    private boolean isToShowDialog = true, isToHideDialog = true, isToRetry = false;

    public circularProgress(Context mContext) {
        this.mContext = mContext;
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Loading...");
    }

    public void showProgressDialog() {
        if (!mProgressDialog.isShowing())
            mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }
}
