package com.example.roommateys;

import java.util.List;

public class ShoppingList {
    public List<ShoppingItem> shoppingList;

    public ShoppingList() {}

    public ShoppingList(List<ShoppingItem> shoppingList) {
        this.shoppingList = shoppingList;
    }

    public void addItem(ShoppingItem item) {
        shoppingList.add(item);
    }

    public void removeItem(ShoppingItem item) {
        shoppingList.remove(item);
    }

    public List<ShoppingItem> getList() {
        return shoppingList;
    }

    public void setShoppingList(List<ShoppingItem> shoppingList) {
        this.shoppingList = shoppingList;
    }

}
