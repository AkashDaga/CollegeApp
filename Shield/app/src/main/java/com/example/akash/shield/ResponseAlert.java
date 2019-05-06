package com.example.akash.shield;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ResponseAlert {
	
	private Context mContext;
	private AlertDialog mAlertDialog;
	
	public ResponseAlert(Context mContext)
	{
		this.mContext = mContext;
	}
	
    public void showWarningAlert(String title, String message)
    {
    	mAlertDialog = new AlertDialog.Builder(mContext).create();
    	mAlertDialog.setTitle(title);  
    	mAlertDialog.setIcon(R.mipmap.ic_launcher);
    	mAlertDialog.setCanceledOnTouchOutside(false);
    	mAlertDialog.setMessage(message);  
    	mAlertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					
				}
			});
    	mAlertDialog.show();
    }
    
}
