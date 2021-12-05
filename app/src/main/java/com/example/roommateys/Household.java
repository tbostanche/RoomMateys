package com.example.roommateys;

import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Household {
    private String houseName;
    private String housePassword;
    private Map<String, UserLocation> members = new HashMap<>();

    public Household() {
        //required by firebase
    }

    public Household(String houseName, String housePassword, String uid, UserLocation firstMember) {
        this.houseName = houseName;
        this.housePassword = housePassword;
        this.members.put(uid, firstMember);
    }

    public String getHouseName() {
        return houseName;
    }

    public String getHousePassword() {
        return housePassword;
    }

    public Map<String, UserLocation> getMembers() {
        return members;
    }

    public void pushMember(String uid, String displayName) {
        this.members.put(uid, new UserLocation(displayName,new LatLng(90,135)));
    }//add new member to list
}
