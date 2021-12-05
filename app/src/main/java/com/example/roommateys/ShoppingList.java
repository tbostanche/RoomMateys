package com.example.roommateys;

import java.util.List;

public class ShoppingList {
    public List<String> shoppingList;

    public ShoppingList() {}

    public ShoppingList(List<String> shoppingList) {
        this.shoppingList = shoppingList;
    }

    public void addItem(String item) {
        shoppingList.add(item);
    }

    public void removeItem(String item) {
        shoppingList.remove(item);
    }

    public List<String> getList() {
        return shoppingList;
    }

    public void setShoppingList(List<String> shoppingList) {
        this.shoppingList = shoppingList;
    }

}
