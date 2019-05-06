package com.example.akash.fragment;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.akash.blueprints.Constant;
import com.example.akash.blueprints.LocationDetails;
import com.example.akash.blueprints.LocationProviderService;
import com.example.akash.shield.R;
import com.example.akash.shield.ServerResponseCallback;
import com.example.akash.shield.VolleyTaskManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class GpsFragment extends Fragment implements GoogleMap.OnInfoWindowClickListener, ServerResponseCallback {
    private GoogleMap googleMap;

    private ImageView ivNavigate, ivCall;

    private View rootView;

    private VolleyTaskManager mVolleyTaskManager;
    private ArrayList<LocationDetails> locationDetailsList = new ArrayList<LocationDetails>();

    private String sGetAllLocationsUrl = Constant.sLocationServerBaseUrl + "getNearMe";
    private String sGetLocationDetailsUrl = Constant.sLocationServerBaseUrl + "getNearMeMaster";

    private LocationProviderService mLocationProviderService;
    private double dCurLatitude = 0.0, dCurLongitude = 0.0;

    private HashMap<Marker, Integer> mMarkerHash = new HashMap<Marker, Integer>();

    private boolean isFetchingMarkerDetails = false;

    private RelativeLayout rlMarkerDetails;
    private TextView tvDescription;

    private String sLatitude, sLongitude, sContactNo;

    final private int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    final private int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;
    int CallPhonePermission, AccessFineLocationPermission;


    public GpsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView= inflater.inflate(R.layout.fragment_gps, container, false);

        ivNavigate = (ImageView) rootView.findViewById(R.id.ivNavigate);
        ivCall = (ImageView) rootView.findViewById(R.id.ivCall);

        mVolleyTaskManager = new VolleyTaskManager(getActivity());

        rlMarkerDetails = (RelativeLayout) rootView.findViewById(R.id.rlMarkerDetails);

        tvDescription = (TextView) rootView.findViewById(R.id.tvDescription);

        ivNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + sLatitude + "," + sLongitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionCheckCallPhone();
            }
        });

        permissionCheckAccessFineLocation();


        return rootView;
    }

    private void permissionCheckAccessFineLocation() {
        AccessFineLocationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);

        if (AccessFineLocationPermission == PackageManager.PERMISSION_GRANTED) {

            doFetchLocation();

        } else {
//			Log.e("Heading", "Permission to record denied");
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void permissionCheckCallPhone() {
        CallPhonePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);

        if (CallPhonePermission == PackageManager.PERMISSION_GRANTED) {

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + sContactNo));
            startActivity(callIntent);
        } else {
//			Log.e("Heading", "Permission to record denied");
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + sContactNo));
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(callIntent);

                } else
                {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    Toast.makeText(getContext(), "CALL PHONE Denied", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getContext(), "GPS Access Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }



    private void doPlotMarkersAtMap(/*ArrayList<LocationDetails> mLocationDetailsList*/)
    {


        SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager()
                .findFragmentById(R.id.nearMeMap);
        mapFragment.getMap();

        // check if map is created successfully or not
        if (googleMap == null) {
            Toast.makeText(getActivity(),
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
                .target(new LatLng(dCurLatitude, dCurLongitude)).zoom(17).build();

        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        googleMap.getUiSettings().setZoomControlsEnabled(true);

        googleMap.getUiSettings().setZoomGesturesEnabled(true);

        googleMap.getUiSettings().setCompassEnabled(true);

        googleMap.getUiSettings().setRotateGesturesEnabled(true);

        googleMap.setOnInfoWindowClickListener((GoogleMap.OnInfoWindowClickListener) getActivity());

//			googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);



    }

    private void doFetchAllMarkers()
    {
        String sUrl = sGetAllLocationsUrl +"/"+dCurLongitude+"/"+dCurLatitude+"/5000";

        Log.d("requesting url1::", ""+sUrl);

        mVolleyTaskManager.doGetCategoryFilteredMarkerListRequest(sUrl, true, true);
    }


    private void doFetchMarkerDetailsById(String sMarkerId)
    {
        String sUrl = sGetLocationDetailsUrl +"/"+sMarkerId;

        isFetchingMarkerDetails = true;

        mVolleyTaskManager.doGetCategoryFilteredMarkerListRequest(sUrl, true, true);
    }

    private void doFetchLocation()
    {
        mLocationProviderService = new LocationProviderService(getActivity());
        // check if GPS enabled
        if(mLocationProviderService.canGetLocation()){

            dCurLatitude = mLocationProviderService.getLatitude();
            dCurLongitude = mLocationProviderService.getLongitude();

            mLocationProviderService.stopUsingGPS();

            Log.d("dCurLatitude", "" + dCurLatitude);

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
                getActivity()).create();

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

        if(isFetchingMarkerDetails)
        {
            isFetchingMarkerDetails = false;

            JSONObject mJsonObject = jsonArray.optJSONObject(0);

            String sCategory = mJsonObject.optString("category");
            String sName = mJsonObject.optString("name");
            String sDescription = mJsonObject.optString("desc");
            sContactNo = mJsonObject.optString("contactno");
            String sEmailId = mJsonObject.optString("mailid");
            String sAddress = mJsonObject.optString("address");
            String sId = mJsonObject.optString("id");

            Log.d("sCategory", ""+sCategory);
            Log.d("sName", ""+sName);
            Log.d("sDescription", ""+sDescription);
            Log.d("sContactNo", ""+sContactNo);
            Log.d("sEmailId", ""+sEmailId);
            Log.d("sAddress", ""+sAddress);
            Log.d("sId", ""+sId);

            rlMarkerDetails.setVisibility(View.VISIBLE);

            sDescription = sName+"\n"+sAddress+"\n"+sContactNo+"\n"+sEmailId+"\n"+sDescription;

            tvDescription.setText(sDescription);


        }
        else
        {
            locationDetailsList.clear();

            int i;
            for ( i = 0; i < jsonArray.length(); i++) {
                JSONObject mJsonObject = jsonArray.optJSONObject(i);

                LocationDetails mLocationDetails = new LocationDetails();
                mLocationDetails.setsCategory(mJsonObject.optString("category"));
                mLocationDetails.setsName(mJsonObject.optString("name"));
                mLocationDetails.setsDescription(mJsonObject.optString("desc"));
                mLocationDetails.setsContactNo(mJsonObject.optString("contactno"));
                mLocationDetails.setsEmailId(mJsonObject.optString("mailid"));
                mLocationDetails.setsAddress(mJsonObject.optString("address"));
                mLocationDetails.setId(Integer.parseInt(mJsonObject.optString("id")));
                mLocationDetails.setsLat(mJsonObject.optString("lat"));
                mLocationDetails.setsLong(mJsonObject.optString("lng"));

                Log.e("Json Object",""+mJsonObject.toString());
                locationDetailsList.add(mLocationDetails);

            }
            Log.e("Json Object count",""+locationDetailsList.size());

            doPlotMarkersAtMap();
        }

    }

    @Override
    public void getDownloadedImage(Bitmap bitmap) {

    }

}
