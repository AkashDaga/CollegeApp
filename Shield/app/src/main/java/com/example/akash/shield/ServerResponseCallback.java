package com.example.akash.shield;

import android.graphics.Bitmap;

import com.android.volley.VolleyError;

import org.json.JSONArray;

public interface ServerResponseCallback {
	public void onVolleyErrorOccurred(VolleyError response);
	public void getResponse(String response);
	public void getResponse(JSONArray jsonArray);
	public void getDownloadedImage(Bitmap bitmap);
}
