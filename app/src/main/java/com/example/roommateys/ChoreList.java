package com.example.roommateys;

import java.util.List;

public class ChoreList {
    public List<ChoreItem> choreList;

    public ChoreList() {}

    public ChoreList(List<ChoreItem> choreList) {
        this.choreList = choreList;
    }

    public void addItem(ChoreItem item) {
        choreList.add(item);
    }

    public void removeItem(ChoreItem item) {
        choreList.remove(item);
    }

    public List<ChoreItem> getList() {
        return choreList;
    }

    public void setChoreList(List<ChoreItem> choreList) {
        this.choreList = choreList;
    }
}
