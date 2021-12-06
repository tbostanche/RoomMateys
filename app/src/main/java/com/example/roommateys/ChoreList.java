package com.example.roommateys;

import java.util.List;

public class ChoreList {
    public List<String> choreList;

    public ChoreList() {}

    public ChoreList(List<String> choreList) {
        this.choreList = choreList;
    }

    public void addItem(String item) {
        choreList.add(item);
    }

    public void removeItem(String item) {
        choreList.remove(item);
    }

    public List<String> getList() {
        return choreList;
    }

    public void setChoreList(List<String> choreList) {
        this.choreList = choreList;
    }
}
