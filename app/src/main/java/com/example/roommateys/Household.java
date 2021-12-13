package com.example.roommateys;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;

public class Household {
    private String houseName;
    private String housePassword;
    private Map<String, String> members = new HashMap<>();
    private CustomLatLng location;

    public Household() {
        //required by firebase
    }

    public Household(String houseName, String housePassword, String uid, String firstMember, LatLng location) {
        this.houseName = houseName;
        this.housePassword = housePassword;
        this.members.put(uid, firstMember);
        this.location = new CustomLatLng(location.latitude, location.longitude);
    }

    public CustomLatLng getLocation() {
        return location;
    }

    public String getHouseName() {
        return houseName;
    }

    public String getHousePassword() {
        return housePassword;
    }

    public Map<String, String> getMembers() {
        return members;
    }

    public void pushMember(String uid, String displayName) {
        this.members.put(uid, displayName);
    }//add new member to list
}
