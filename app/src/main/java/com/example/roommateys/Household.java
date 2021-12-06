package com.example.roommateys;

import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Household {
    private String houseName;
    private String housePassword;
    private Map<String, String> members = new HashMap<>();

    public Household() {
        //required by firebase
    }

    public Household(String houseName, String housePassword, String uid, String firstMember) {
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

    public Map<String, String> getMembers() {
        return members;
    }

    public void pushMember(String uid, String displayName) {
        this.members.put(uid, displayName);
    }//add new member to list
}
