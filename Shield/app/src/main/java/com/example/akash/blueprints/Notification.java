package com.example.akash.blueprints;

import android.graphics.Bitmap;

public class Notification {
    private String id;
    private String message;
    private String systemDate;
    private String systemTime;

    public Notification(String id,String systemDate, String systemTime, String message) {
        this.id = id;
        this.message = message;
        this.systemDate = systemDate;
        this.systemTime = systemTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSystemDate() {
        return systemDate;
    }

    public void setSystemDate(String systemDate) {
        this.systemDate = systemDate;
    }

    public String getSystemTime() {
        return systemTime;
    }

    public void setSystemTime(String systemTime) {
        this.systemTime = systemTime;
    }
}
