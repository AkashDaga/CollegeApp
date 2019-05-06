package com.example.akash.shield;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import com.android.volley.VolleyError;
import com.example.akash.blueprints.Constant;
import com.example.akash.blueprints.LocationDetails;
import com.example.akash.blueprints.LocationProviderService;
import com.example.akash.shield.R;
import com.example.akash.shield.ServerResponseCallback;
import com.example.akash.shield.VolleyTaskManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GoogleMapsActivity extends Activity implements
        OnInfoWindowClickListener, ServerResponseCallback {

    // Google Map
    private GoogleMap googleMap;

    private ImageView ivNavigate, ivCall;

    private VolleyTaskManager mVolleyTaskManager;
    private ArrayList<LocationDetails> locationDetailsList = new ArrayList<LocationDetails>();

//    private String sGetAllLocationsUrl = Constant.sLocationServerBaseUrl +"getNearMe";
//    private String sGetLocationDetailsUrl = Constant.sLocationServerBaseUrl+"getNearMeMaster";

    private LocationProviderService mLocationProviderService;
    private double dCurLatitude = 0.0, dCurLongitude = 0.0;

    private HashMap<Marker, Integer> mMarkerHash = new HashMap<Marker, Integer>();

    private boolean isFetchingMarkerDetails = false;

    private RelativeLayout rlMarkerDetails;
    private TextView tvDescription;

    private String sLatitude, sLongitude, sContactNo;
    private String  distance = "";

    final private int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    final private int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;
    int CallPhonePermission,AccessFineLocationPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_google_maps);

        ivNavigate = (ImageView) findViewById(R.id.ivNavigate);
        ivCall = (ImageView) findViewById(R.id.ivCall);

        mVolleyTaskManager = new VolleyTaskManager(GoogleMapsActivity.this);

        rlMarkerDetails = (RelativeLayout) findViewById(R.id.rlMarkerDetails);

        tvDescription = (TextView)findViewById(R.id.tvDescription);


        final Dialog sellerIndroductionCodeVerificationDialog = new Dialog(GoogleMapsActivity.this);
        sellerIndroductionCodeVerificationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        sellerIndroductionCodeVerificationDialog.setContentView(R.layout.distance_dialog);
        sellerIndroductionCodeVerificationDialog.show();

        final EditText etSellerIntroductionCode = (EditText) sellerIndroductionCodeVerificationDialog.findViewById(R.id.etSellerIntroductionCode);
        Button btnVerify = (Button) sellerIndroductionCodeVerificationDialog.findViewById(R.id.btnVerify);
        btnVerify.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                sellerIndroductionCodeVerificationDialog.cancel();
                distance = etSellerIntroductionCode.getText().toString();

                permissionCheckAccessFineLocation();

            }
        });


        ivNavigate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Uri gmmIntentUri = Uri.parse("google.navigation:q="+sLatitude+","+sLongitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        ivCall.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                permissionCheckCallPhone();


            }
        });





    }


    private void permissionCheckAccessFineLocation()
    {
        AccessFineLocationPermission = ContextCompat.checkSelfPermission(GoogleMapsActivity.this,Manifest.permission.ACCESS_FINE_LOCATION);

        if (AccessFineLocationPermission == PackageManager.PERMISSION_GRANTED ) {

            doFetchLocation();

        }
        else
        {
//			Log.e("Heading", "Permission to record denied");
            ActivityCompat.requestPermissions(GoogleMapsActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void permissionCheckCallPhone()
    {
        CallPhonePermission = ContextCompat.checkSelfPermission(GoogleMapsActivity.this,Manifest.permission.CALL_PHONE);

        if (CallPhonePermission == PackageManager.PERMISSION_GRANTED) {

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:"+sContactNo));
            startActivity(callIntent);
        }
        else
        {
//			Log.e("Heading", "Permission to record denied");
            ActivityCompat.requestPermissions(GoogleMapsActivity.this,new String[]{Manifest.permission.CALL_PHONE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED )
                {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:"+sContactNo));
                    startActivity(callIntent);

                } else
                {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    Toast.makeText(GoogleMapsActivity.this, "CALL PHONE Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED )
                {
                    doFetchLocation();

                } else
                {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    dCurLatitude = 0.0;
                    dCurLongitude = 0.0;
                    Toast.makeText(GoogleMapsActivity.this, "GPS Access Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }



    private void doPlotMarkersAtMap(/*ArrayList<LocationDetails> mLocationDetailsList*/)
    {


            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.nearMeMap)).getMap();

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }

            googleMap.clear();

            Double dLat, dLong;

            dLat = dCurLatitude;
            dLong = dCurLongitude;

            for(int k = 0; k < locationDetailsList.size(); k++)
            {

                dLat = Double.parseDouble(locationDetailsList.get(k).getsLat());
                dLong = Double.parseDouble(locationDetailsList.get(k).getsLong());
                String sTitle = locationDetailsList.get(k).getsName();

                Marker mMarker = googleMap
                        .addMarker(new MarkerOptions()
                                .position(new LatLng(dLat, dLong))
                                .title(sTitle)
                                .snippet("Click here")
                                .icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));

                mMarkerHash.put(mMarker, locationDetailsList.get(k).getId());
            }

            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(dCurLatitude, dCurLongitude))
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_RED)));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(dCurLatitude, dCurLongitude)).zoom(10).build();

            googleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));

            googleMap.getUiSettings().setZoomControlsEnabled(true);

            googleMap.getUiSettings().setZoomGesturesEnabled(true);

            googleMap.getUiSettings().setCompassEnabled(true);

            googleMap.getUiSettings().setRotateGesturesEnabled(true);

            googleMap.setOnInfoWindowClickListener(GoogleMapsActivity.this);

//			googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);



    }

    private void doFetchAllMarkers()
    {
        String sUrl = "";
        sUrl = "http://192.168.0.8:8084/MCETSuraksha/ServletDisplayAllPoliceStation?lat="+dCurLatitude+"&lng="+dCurLongitude+"&distance="+distance;
        Log.d("requesting url1::", ""+sUrl);

        mVolleyTaskManager.doGetCategoryFilteredMarkerListRequest(sUrl, true, true);
    }


    private void doFetchMarkerDetailsById(String sMarkerId)
    {
        String sUrl = "";

        sUrl = "http://192.168.0.8:8084/MCETSuraksha/ServletDisplayPoliceStationDetails?id="+sMarkerId;

        isFetchingMarkerDetails = true;

        mVolleyTaskManager.doGetCategoryFilteredMarkerListRequest(sUrl, true, true);
    }

    private void doFetchLocation()
    {
        mLocationProviderService = new LocationProviderService(GoogleMapsActivity.this);
        // check if GPS enabled     
        if(mLocationProviderService.canGetLocation()){

            dCurLatitude = mLocationProviderService.getLatitude();
            dCurLongitude = mLocationProviderService.getLongitude();

            mLocationProviderService.stopUsingGPS();

//            Log.d("dCurLatitude", ""+dCurLatitude);

            if(dCurLatitude == 0.0)
            {
                showFetchLocationAgainDialog();
            }
            else
            {
                    doFetchAllMarkers();

            }

        }else{

             // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            mLocationProviderService.showSettingsAlert();
        }
    }

    @SuppressWarnings("deprecation")
    private void showFetchLocationAgainDialog()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(
                GoogleMapsActivity.this).create();

        // Setting Dialog Title
        alertDialog.setTitle("Failed");

        // Setting Dialog Message
        alertDialog.setMessage("Can't fetch your location. Do you want to try again?");

        // Setting Icon to Dialog
        alertDialog.setIcon(R.mipmap.ic_launcher);

        // Setting OK Button
        alertDialog.setButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        // Write your code here to execute after dialog
                        // closed

                        permissionCheckAccessFineLocation();
                    }
                });

        alertDialog.setCancelable(false);

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {


        Log.v("marker.getId()", marker.getId());

        int id = mMarkerHash.get(marker);

//        Toast.makeText(GoogleMapsActivity.this, "" + id,Toast.LENGTH_SHORT).show();

        sLatitude = String.valueOf(marker.getPosition().latitude);
        sLongitude = String.valueOf(marker.getPosition().longitude);

        doFetchMarkerDetailsById(String.valueOf(id));

    }

    @Override
    public void onVolleyErrorOccurred(VolleyError response) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getResponse(String response) {
        // TODO Auto-generated method stub

    }


    @Override
    public void getResponse(JSONArray jsonArray) {

        Log.e("Response",jsonArray.toString());

        if(isFetchingMarkerDetails)
        {
            isFetchingMarkerDetails = false;

            JSONObject mJsonObject = jsonArray.optJSONObject(0);

            String sLat = mJsonObject.optString("lat");
            String sLng = mJsonObject.optString("lng");
            String sName = mJsonObject.optString("name");
            String sDescription;
            sContactNo = mJsonObject.optString("ph");
            String sAddress = mJsonObject.optString("add");
            String sId = mJsonObject.optString("id");



            rlMarkerDetails.setVisibility(View.VISIBLE);

            sDescription = sName+"\n"+sAddress+"\n"+sContactNo+"\nLat : "+sLat+"\n Lng : "+sLng;

            tvDescription.setText(sDescription);


        }
        else
        {
            locationDetailsList.clear();

            int i;
            for ( i = 0; i < jsonArray.length(); i++) {
                JSONObject mJsonObject = jsonArray.optJSONObject(i);

                LocationDetails mLocationDetails = new LocationDetails();
                mLocationDetails.setsName(mJsonObject.optString("name"));
                mLocationDetails.setId(Integer.parseInt(mJsonObject.optString("id")));
                mLocationDetails.setsLat(mJsonObject.optString("lat"));
                mLocationDetails.setsLong(mJsonObject.optString("lng"));

//                Log.e("Json Object",""+mJsonObject.toString());
                locationDetailsList.add(mLocationDetails);

            }
//            Log.e("Json Object count",""+locationDetailsList.size());

            doPlotMarkersAtMap();
        }

    }

    @Override
    public void getDownloadedImage(Bitmap bitmap) {

    }

}
