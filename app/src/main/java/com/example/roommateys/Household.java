package com.example.roommateys;

import java.util.Arrays;
import java.util.List;

public class Household {
    private String houseName;
    private String housePassword;
    private List<String> members;

    public Household() {

    }

    public Household(String houseName, String housePassword, String firstMember) {
        this.houseName = houseName;
        this.housePassword = housePassword;
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
    }
}
