package com.example.roommateys;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class HouseLocationDialog extends AppCompatDialogFragment {
    private Marker marker;
    private String houseName;
    private String housePassword;
    private String displayName;

    public HouseLocationDialog(Marker marker, String houseName, String housePassword, String displayName) {
        super();
        this.marker = marker;
        this.houseName = houseName;
        this.housePassword = housePassword;
        this.displayName = displayName;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Place House Here?")
                .setMessage("Would you like to place your house here?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("locq","yes");
                        createHouse();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                marker.remove();
            }
        });
        return builder.create();
    }

    private void createHouse(){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        ShoppingList shoppingList = new ShoppingList();
        List<ShoppingItem> rawShoppingList = new ArrayList<>();
        rawShoppingList.add(new ShoppingItem("Milk", "Milk Man"));
        shoppingList.setShoppingList(rawShoppingList);

        ArrayList<String> choreList = new ArrayList<>();
        choreList.add("Clean the kitchen");
        //else house does not yet exist; create new house
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); //get authenticated user
        String uid = user.getUid();
        db.child("Users").child(uid).setValue(new User(uid,houseName,displayName));
        db.child("Houses").child(houseName).setValue(new Household(houseName,housePassword,uid,displayName,marker.getPosition())); //add a new house to the Houses/houseName path, set the value to a new java object with houseName, housePassword, and the first member as this user
        db.child("Locations").child(houseName).child(uid).setValue( new UserLocation(displayName,new LatLng(90,135)));
        db.child("ShoppingLists").child(houseName).setValue(shoppingList);
        db.child("ShoppingLists").child(houseName).child("list").removeValue();
        db.child("ChoreLists").child(houseName).setValue(new ChoreList(choreList));
        db.child("ChoreLists").child(houseName).child("list").removeValue();
        Intent intent = new Intent(getContext(), MessageActivity.class);
        startActivity(intent);
    }
}
