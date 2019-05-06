package com.example.akash.adapters;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.example.akash.shield.OTPActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;

public class IncomingSms extends BroadcastReceiver {
	

	 String finalString="";
	 String dateFormatted="";
	 long datetime=0;
	 
	 String message ;
	
	// Get the object of SmsManager
	final SmsManager sms = SmsManager.getDefault();
	
	@SuppressLint("SimpleDateFormat") @SuppressWarnings("deprecation")
	public void onReceive(Context context, Intent intent) {
	
		// Retrieves a map of extended data from the intent.
		final Bundle bundle = intent.getExtras();
		
		try {
			
			if (bundle != null) {
				
				final Object[] pdusObj = (Object[]) bundle.get("pdus");
				
				for (int i = 0; i < pdusObj.length; i++) {
					
					SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
					String phoneNumber = currentMessage.getDisplayOriginatingAddress();
					
			        String senderNum = phoneNumber;
			        message = currentMessage.getDisplayMessageBody();
			        datetime= currentMessage.getTimestampMillis();
			        
			        Date date = new Date(datetime);
			        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			        dateFormatted = formatter.format(date);

			        finalString="Sms Sender Number: \n"+senderNum+" \nDate Time: \n"+dateFormatted+" \nMessage Body: \n"+message;
			        Log.e("SmsReceiver", finalString);
			        
			        if(senderNum.contains("PAYSKP"))
			        {
			        	message = message.replaceAll("[^0-9.]", "");
			        	Log.e("msg",message);
			        	if(OTPActivity.EnterOTP !=null)
			        	{
			        	OTPActivity.EnterOTP.setText(message.split("\\.")[0]);
			        	//OTPValidationActivity.progressBar.setVisibility(View.INVISIBLE);
			        	}

			        }
					 
			
				} // end for loop
              } // bundle is null

		} catch (Exception e) {
			Log.e("SmsReceiver", "Exception smsReceiver" +e);
			
		}
	}
	
	
	

	
}