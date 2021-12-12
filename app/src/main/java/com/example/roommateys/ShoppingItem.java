package com.example.roommateys;

public class ShoppingItem {
    String item;
    String assignedHousemate;

    public ShoppingItem(){}

    public ShoppingItem(String item, String assignedHousemate) {
        this.assignedHousemate = assignedHousemate;
        this.item = item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public void setAssignedHousemate(String assignedHousemate) {
        this.assignedHousemate = assignedHousemate;
    }

    public String getItem() {
        return item;
    }

    public String getAssignedHousemate() {
        return assignedHousemate;
    }
}
