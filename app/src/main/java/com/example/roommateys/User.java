package com.example.roommateys;

import android.location.Location;

public class User {
    private String uid;
    private String houseName;
    private Location location;
    private String displayName;

    public User() {

    }

    public User(String uid,String houseName,String displayName) {
        this.uid = uid;
        this.houseName = houseName;
        this.displayName = displayName;
        this.location = null;
    }

    public String getHouseName() {
        return houseName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Location getLocation() {
        return location;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

}
