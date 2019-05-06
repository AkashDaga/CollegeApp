package com.example.akash.shield;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Akash on 22-05-2016.
 */
public class ConvertIntoPhysicalAddress extends AsyncTask<Void,Void,Void> {
    double LATITUDE, LONGITUDE;
    SharedPreferenceInventory myInventory;
    Context context;
    StringBuilder strReturnedAddress;
    String Message1,Message;

    public ConvertIntoPhysicalAddress(double LATITUDE, double LONGITUDE, Context context) {
        this.LATITUDE = LATITUDE;
        this.LONGITUDE = LONGITUDE;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        myInventory=new SharedPreferenceInventory(context);
        strReturnedAddress = new StringBuilder();

        Message1=myInventory.getMessage()+"My Location is  LATITUDE: "+LATITUDE+" ,LONGITUDE: "+LONGITUDE+". ";

        Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);

            if(addresses != null) {
                Address returnedAddress = addresses.get(0);
                for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }

                Message= Message1+"And the physical address of the location is : "+strReturnedAddress.toString();
                myInventory.setUpdatedMessage(Message);
            }
            else{
                Message=Message1+". And the physical address of the location is: (Sorry no address found)";
                myInventory.setUpdatedMessage(Message);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        return null;
    }
}
