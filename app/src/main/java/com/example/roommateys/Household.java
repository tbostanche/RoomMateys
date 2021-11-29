package com.example.roommateys;

import java.util.Arrays;
import java.util.List;

public class Household {
    public String houseName;
    public String housePassword;
    public List<String> members;

    public Household() {

    }

    public Household(String houseName, String housePassword, String firstMember) {
        this.houseName = houseName;
        this.housePassword = housePassword;
        List<String> firstMemberArr = Arrays.asList(new String[]{firstMember});
        this.members = firstMemberArr;
    }
}
