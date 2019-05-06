package com.example.akash.shield;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Akash on 08-04-2016.
 */
public class VolleyTaskManager {
    private Context mContext;
    private ProgressDialog mProgressDialog;
    private String TAG = "";
    private boolean isToShowDialog = true, isToHideDialog = true, isToRetry = false;
    private SharedPreferenceInventory mSharedPreferenceInventory;

    public VolleyTaskManager(Context mContext) {
        this.mContext = mContext;

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Loading...");

        TAG = mContext.getClass().getSimpleName();
        Log.d("tag", TAG);
    }

    public void showProgressDialog() {
        if (!mProgressDialog.isShowing())
            mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    private void doPostRequest(String sUrl, final String sValueToBePassed, int iMethod, final boolean isPostAuthRestRequest) {

        if (isToShowDialog) {
            showProgressDialog();
        }
        StringRequest mStringRequest = new StringRequest(iMethod, sUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String sResponse) {

                if(isToHideDialog) {
                    hideProgressDialog();
                }
                ((ServerResponseCallback) mContext).getResponse(sResponse);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                ResponseAlert mResponseAlert = new ResponseAlert(mContext);

                hideProgressDialog();
                VolleyLog.d(TAG, "Error: " + error.getMessage());

                if (error instanceof TimeoutError) {
                    Log.d("error ocurred","TimeoutError");
                    mResponseAlert.showWarningAlert("Timeout Error","Unable to connect server ");
                }
                else if (error instanceof NoConnectionError) {
                    Log.d("error ocurred","No Connection");
                    mResponseAlert.showWarningAlert("No Connection","Please check your internet connection");
                }
                else if (error instanceof AuthFailureError) {
                    Log.d("error ocurred","AuthFailureError");
                    mResponseAlert.showWarningAlert("Auth Failure Error","Unable to authenticate server");
                } else if (error instanceof ServerError) {
                    Log.d("error ocurred","ServerError");
                    mResponseAlert.showWarningAlert("Server Error","Unknown server error");
                } else if (error instanceof NetworkError) {
                    Log.d("error ocurred","NetworkError");
                    mResponseAlert.showWarningAlert("Network Error","Network error. Please try again ");
                } else if (error instanceof ParseError) {
                    Log.d("error ocurred","ParseError");
                    mResponseAlert.showWarningAlert("Error","Server error, please try again. Please contact administrator.");
                }

                ((ServerResponseCallback) mContext).onVolleyErrorOccurred(error);


            }
        }){

            @Override
            public byte[] getBody() throws AuthFailureError {

                return sValueToBePassed.getBytes();
            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                if(isPostAuthRestRequest)
                {
                    mSharedPreferenceInventory = new SharedPreferenceInventory(mContext);
                    String sIsExistingUser = mSharedPreferenceInventory.getKeyIsExistingUser();
                    params.put("setup check", "Bearer "+sIsExistingUser);
                }
                else
                {
                    params.put("Accept", "application/json");
                }

                return params;
            }


            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            protected String getPostParamsEncoding() {

                return "application/json;charset=UTF-8";
            }
        };
        if(isToRetry)
            mStringRequest.setRetryPolicy(new DefaultRetryPolicy(180000,	1, 1));//Time out, no. of retry, Back Off Multiplier
        else
            mStringRequest.setRetryPolicy(new DefaultRetryPolicy(180000, 0, 1));//Time out, no. of retry, Back Off Multiplier

        RequestQueue.getInstance().addToRequestQueue(mStringRequest);
    }

    private void doGetJSONArrayRequest(String sUrl)
    {

        if(isToShowDialog){
            showProgressDialog();
        }

        JsonArrayRequest mJSONArrayRequest = new JsonArrayRequest(sUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        if(isToHideDialog) {
                            hideProgressDialog();
                        }
                        ((ServerResponseCallback) mContext).getResponse(response);

                        //////
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());

                ResponseAlert mResponseAlert = new ResponseAlert(mContext);

                hideProgressDialog();
                VolleyLog.d(TAG, "Error: " + error.getMessage());

                if (error instanceof TimeoutError) {
                    Log.d("error ocurred","TimeoutError");
                    mResponseAlert.showWarningAlert("Timeout Error","Unable to connect server ");
                }
                else if (error instanceof NoConnectionError) {
                    Log.d("error ocurred","No Connection");
                    mResponseAlert.showWarningAlert("No Connection","Please check your internet connection");
                }
                else if (error instanceof AuthFailureError) {
                    Log.d("error ocurred","AuthFailureError");
                    mResponseAlert.showWarningAlert("Auth Failure Error","Unable to authenticate server");
                } else if (error instanceof ServerError) {
                    Log.d("error ocurred","ServerError");
                    mResponseAlert.showWarningAlert("Server Error","Unknown server error");
                } else if (error instanceof NetworkError) {
                    Log.d("error ocurred","NetworkError");
                    mResponseAlert.showWarningAlert("Network Error","Network error. Please try again ");
                } else if (error instanceof ParseError) {
                    Log.d("error ocurred","ParseError");
                    mResponseAlert.showWarningAlert("Error","Server error, please try again. Please contact administrator.");
                }

                ((ServerResponseCallback) mContext).onVolleyErrorOccurred(error);
            }
        });

        // Adding request to request queue
        mJSONArrayRequest.setRetryPolicy(new DefaultRetryPolicy(180000,0, 1));

        RequestQueue.getInstance().addToRequestQueue(mJSONArrayRequest);
    }
    private void doImageRequest(String sUrl){

        if(isToShowDialog){
            showProgressDialog();
        }
        ImageRequest mImageRequest = new ImageRequest(sUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {

                        if(isToHideDialog) {
                            hideProgressDialog();
                        }

                        ((ServerResponseCallback) mContext).getDownloadedImage(bitmap);

                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {

                        hideProgressDialog();

                        ResponseAlert mResponseAlert = new ResponseAlert(mContext);

                        if (error instanceof TimeoutError) {
                            Log.d("error ocurred","TimeoutError");
                            mResponseAlert.showWarningAlert("Timeout Error","Unable to connect server ");
                        }
                        else if (error instanceof NoConnectionError) {
                            Log.d("error ocurred","No Connection");
                            mResponseAlert.showWarningAlert("No Connection","Please check your internet connection");
                        }
                        else if (error instanceof AuthFailureError) {
                            Log.d("error ocurred","AuthFailureError");
                            mResponseAlert.showWarningAlert("Auth Failure Error","Unable to authenticate server");
                        } else if (error instanceof ServerError) {
                            Log.d("error ocurred","ServerError");
                            mResponseAlert.showWarningAlert("Server Error","Unknown server error");
                        } else if (error instanceof NetworkError) {
                            Log.d("error ocurred","NetworkError");
                            mResponseAlert.showWarningAlert("Network Error","Network error. Please try again ");
                        } else if (error instanceof ParseError) {
                            Log.d("error ocurred","ParseError");
                            mResponseAlert.showWarningAlert("Error","Server error, please try again. Please contact adminstrator.");
                        }
                    }
                });
        mImageRequest.setRetryPolicy(new DefaultRetryPolicy(180000,0, 1));//Time out, no. of retry, Back Off Multiplier
        Log.e("hello", mImageRequest + "");
        RequestQueue.getInstance().addToRequestQueue(mImageRequest);

    }
    public void doRegistration(String sUrl, String sValueToBePassed, int iMethod, boolean isToShowDialog, boolean isToHideDialog, boolean isToRetry) {

        this.isToShowDialog = isToShowDialog;
        this.isToHideDialog = isToHideDialog;
        this.isToRetry=isToRetry;
        doPostRequest(sUrl, sValueToBePassed, iMethod, false);
    }

    public void doGetCategoryFilteredMarkerListRequest(String sUrl, boolean isToShowDialog, boolean isToHideDialog)
    {
        this.isToShowDialog = isToShowDialog;
        this.isToHideDialog = isToHideDialog;

        doGetJSONArrayRequest(sUrl);
    }

    public void getImageFromUrl(String sUrl, boolean isToShowDialog, boolean isToHideDialog) {

        this.isToShowDialog = isToShowDialog;
        this.isToHideDialog = isToHideDialog;

        doImageRequest(sUrl);
    }


}
