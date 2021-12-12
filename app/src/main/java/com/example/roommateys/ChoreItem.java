package com.example.roommateys;

public class ChoreItem {
    String item;
    String assignedHousemate;

    public ChoreItem(){}

    public ChoreItem(String item, String assignedHousemate) {
        this.item = item;
        this.assignedHousemate = assignedHousemate;
    }

    public String getItem() {
        return item;
    }

    public String getAssignedHousemate() {
        return assignedHousemate;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public void setAssignedHousemate(String assignedHousemate) {
        this.assignedHousemate = assignedHousemate;
    }
}
