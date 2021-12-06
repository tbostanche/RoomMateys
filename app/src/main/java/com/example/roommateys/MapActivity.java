package com.example.roommateys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MapActivity extends AppCompatActivity {

    private GoogleMap mMap;
    private DatabaseReference db;
    private SharedPreferences sharedPreferences;
    private HashMap<String, Marker> hashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        hashMap = new HashMap<>();
        db = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = getSharedPreferences("com.example.roommateys", Context.MODE_PRIVATE);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
        mapFragment.getMapAsync(googleMap -> {
            mMap = googleMap;
            Query houseMembers = db.child("Locations").child(sharedPreferences.getString("houseName",""));
            houseMembers.addChildEventListener(memberLocationChanged);
        });
    }


    ChildEventListener memberLocationChanged = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            Log.d("locq",snapshot.toString());
            Log.d("locq",snapshot.getValue().toString());
            String displayName = "displayName";
            CustomLatLng latLng = null;
            for (DataSnapshot child : snapshot.getChildren()) {
                if (child.getKey().equals(displayName)) {
                    displayName = child.getValue(String.class);
                }
                else {
                    latLng = child.getValue(CustomLatLng.class);
                }
            }
            Marker m = mMap.addMarker(new MarkerOptions().position(new LatLng(latLng.getLatitude(),latLng.getLongitude())).title(displayName));
            hashMap.put(snapshot.getKey(),m);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            UserLocation ul = snapshot.getValue(UserLocation.class);
            Marker m = hashMap.get(snapshot.getKey());
            m.setPosition(ul.getLocation());
            m.setTitle(ul.getDisplayName());
            hashMap.remove(snapshot.getKey());
            hashMap.put(snapshot.getKey(),m);
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            Log.d("locq",snapshot.toString());
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            Log.d("locq",snapshot.toString());
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
        }
    };

    private void displayAllRoommates() {

    }

    public void choreOnClick(View view) {
        Intent intent = new Intent(this, ChoreActivity.class);
        startActivity(intent);
    }
    public void shoppingOnClick(View view) {
        Intent intent = new Intent(this, ShoppingActivity.class);
        startActivity(intent);
    }
    public void settingsOnClick(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
    public void messageOnClick(View view) {
        Intent intent = new Intent(this, MessageActivity.class);
        startActivity(intent);
    }
}