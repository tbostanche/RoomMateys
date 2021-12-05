package com.example.roommateys;


import com.google.android.gms.maps.model.LatLng;

public class User {
    private String uid;
    private String houseName;
    private LatLng location;
    private String displayName;

    public User() {

    }

    public User(String uid,String houseName,String displayName) {
        this.uid = uid;
        this.houseName = houseName;
        this.displayName = displayName;
        this.location = null;
    }

    public User(String uid,String houseName,String displayName, LatLng location) {
        this.uid = uid;
        this.houseName = houseName;
        this.displayName = displayName;
        this.location = location;
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

    public LatLng getLocation() {
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

    public void setLocation(LatLng location) {
        this.location = location;
    }

}
