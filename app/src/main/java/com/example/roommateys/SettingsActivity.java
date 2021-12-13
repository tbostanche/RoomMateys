package com.example.roommateys;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    DatabaseReference db;
    boolean darkmode;
    boolean shareLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sharedPreferences = getSharedPreferences("com.example.roommateys", Context.MODE_PRIVATE);
        db = FirebaseDatabase.getInstance().getReference();
        darkmode = sharedPreferences.getBoolean("darkMode", false);
        shareLocation = sharedPreferences.getBoolean("shareLocation", true);
        if (darkmode){
            Button darkModeButton = findViewById(R.id.darkModeButton);
            darkModeButton.setText("ON");
        }
        if (!shareLocation){
            Button shareLocationButton = findViewById(R.id.displayLocationButton);
            shareLocationButton.setText("OFF");
        }
    }

    public void choreOnClick(View view) {
        Intent intent = new Intent(this, ChoreActivity.class);
        startActivity(intent);
    }
    public void messageOnClick(View view) {
        Intent intent = new Intent(this, MessageActivity.class);
        startActivity(intent);
    }
    public void shoppingOnClick(View view) {
        Intent intent = new Intent(this, ShoppingActivity.class);
        startActivity(intent);
    }
    public void mapOnClick(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }
    public void logoutOnClick(View view) {
        boolean darkMode = sharedPreferences.getBoolean("darkMode",false);
        sharedPreferences.edit().clear().apply();
        if (darkMode) {
            sharedPreferences.edit().putBoolean("darkMode",true).apply();
        }
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> {
                    Intent intent = new Intent(SettingsActivity.this,FirebaseUIActivity.class);
                    startActivity(intent);
                    finish();
                });
    }
    public void leaveHouseOnClick(View view) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String houseName = sharedPreferences.getString("houseName",null);
        boolean darkMode = sharedPreferences.getBoolean("darkMode",false);
        db.child("Users").child(uid).removeValue();
        db.child("Locations").child(houseName).child(uid).removeValue();
        db.child("Houses").child(houseName).child("members").child(uid).removeValue();
        Query queryHouse = db
                .child("Houses")
                .orderByChild("houseName")
                .equalTo(houseName);
        queryHouse.addListenerForSingleValueEvent(houseEmptyListener);
        sharedPreferences.edit().clear().apply();
        if (darkMode) {
            sharedPreferences.edit().putBoolean("darkMode",true).apply();
        }
        Intent intent = new Intent(this,PostSignInActivity.class);
        startActivity(intent);
    }


    ValueEventListener houseEmptyListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            for (DataSnapshot child : snapshot.getChildren()) {
                Household currHouse = child.getValue(Household.class);
                String houseName = currHouse.getHouseName();
                if (currHouse.getMembers() == null || currHouse.getMembers().isEmpty()) {
                    db.child("Houses").child(houseName).removeValue();
                    db.child("ChoreLists").child(houseName).removeValue();
                    db.child("Locations").child(houseName).removeValue();
                    db.child("Messages").child(houseName).removeValue();
                    db.child("ShoppingLists").child(houseName).removeValue();
                }
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    public void enableDarkModeOnClick(View view) {
        Button darkModeButton = findViewById(R.id.darkModeButton);
        if (!darkmode){
            darkModeButton.setText("ON");
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            sharedPreferences.edit().putBoolean("darkMode", true).apply();
        } else {
            darkModeButton.setText("OFF");
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            sharedPreferences.edit().putBoolean("darkMode", false).apply();
        }
    }

    @SuppressLint("MissingPermission")
    public void locationSettingOnClick(View view) {
        if (shareLocation) {
            sharedPreferences.edit().putBoolean("shareLocation", false).apply();
            shareLocation = false;
            Button shareLocationButton = findViewById(R.id.displayLocationButton);
            db.child("Locations")
                    .child(sharedPreferences.getString("houseName",null))
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .removeValue();
            shareLocationButton.setText("OFF");
        } else {
            sharedPreferences.edit().putBoolean("shareLocation", true).apply();
            shareLocation = true;
            FusedLocationProviderClient mFusedLocationProviderClient =  LocationServices
                    .getFusedLocationProviderClient(this);
            mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(
                    this, task -> {
                        Location mLastKnownLocation = task.getResult();
                        if (task.isSuccessful()) {
                            LatLng currentPosition = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                            db.child("Locations")
                                    .child(sharedPreferences.getString("houseName",null))
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(new UserLocation(sharedPreferences
                                            .getString("displayName",null),currentPosition));
                        }
                    });
            Button shareLocationButton = findViewById(R.id.displayLocationButton);
            shareLocationButton.setText("ON");
        }
    }
}