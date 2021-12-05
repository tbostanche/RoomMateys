package com.example.roommateys;

import com.google.android.gms.maps.model.LatLng;

import java.util.Map;

public class UserLocation {

    private String displayName;
    private LatLng location;


    public UserLocation(String displayName, Map<String, Double>location) {
        this.displayName = displayName;
        this.location = new LatLng(location.get("latitude"),location.get("longitude"));
    }

    public  UserLocation(String displayName, LatLng location) {
        this.displayName = displayName;
        this.location = location;
    }

    public UserLocation() {
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public LatLng getLocation() {
        return location;
    }
}
