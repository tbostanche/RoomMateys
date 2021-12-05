package com.example.roommateys;

import java.util.Arrays;
import java.util.List;

public class Household {
    private String houseName;
    private String housePassword;
    private List<String> members;

    public Household() {
        //required by firebase
    }

    public Household(String houseName, String housePassword, String firstMember) {
        this.houseName = houseName;
        this.housePassword = housePassword;
        //convert string first member into list with 1 member
        List<String> firstMemberArr = Arrays.asList(new String[]{firstMember});
        this.members = firstMemberArr;
    }

    public String getHouseName() {
        return houseName;
    }

    public String getHousePassword() {
        return housePassword;
    }

    public List<String> getMembers() {
        return members;
    }

    public void pushMember(String member) {
        this.members.add(member);
    }//add new member to list
}
