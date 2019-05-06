package com.example.akash.blueprints;

import android.graphics.Bitmap;
import android.widget.TextView;

/**
 * Created by Akash on 22-04-2016.
 */
public class CommunityListModel {
    private String userName;
    private String systemDate;
    private String systeTime;
    private String Post;

    public CommunityListModel(String Post, String userName, String systemDate, String systeTime) {
        this.Post = Post;
        this.userName = userName;
        this.systemDate = systemDate;
        this.systeTime = systeTime;
    }

    public String getPost() {
        return Post;
    }

    public void setPost(String post) {
        Post = post;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSystemDate() {
        return systemDate;
    }

    public void setSystemDate(String systemDate) {
        this.systemDate = systemDate;
    }

    public String getSysteTime() {
        return systeTime;
    }

    public void setSysteTime(String systeTime) {
        this.systeTime = systeTime;
    }
}
