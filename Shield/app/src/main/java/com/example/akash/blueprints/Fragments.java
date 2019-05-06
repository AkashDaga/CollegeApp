package com.example.akash.blueprints;

/**
 * Created by Akash on 05-04-2016.
 */
public class Fragments {
    private String info;
    private int iconID;

    public Fragments(String info) {
        this.info = info;
        //this.iconID = iconID;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getIconID() {
        return iconID;
    }

    public void setIconID(int iconID) {
        this.iconID = iconID;
    }
}
