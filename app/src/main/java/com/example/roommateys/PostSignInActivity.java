package com.example.roommateys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationRequest;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;


public class PostSignInActivity extends AppCompatActivity {

    private DatabaseReference db;
    private String houseName;
    private String housePassword;
    private String displayName;
    SharedPreferences sharedPreferences;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 12; //could've been any number!
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LatLng currentPosition = new LatLng(0.0, 0.0);
    private GoogleMap gMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_sign_in);
        sharedPreferences = getSharedPreferences("com.example.roommateys",Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn",false);
        if (isLoggedIn) {
            Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
            startActivity(intent);
            return;
        }
        db = FirebaseDatabase.getInstance().getReference();
    }

    public void joinHouseOnClick(View view) {
        EditText editHouseName = (EditText) findViewById(R.id.editHouseName);
        EditText editHousePassword = (EditText) findViewById(R.id.editHousePassword);
        EditText editDisplayName = (EditText) findViewById(R.id.editDisplayName);
        houseName = editHouseName.getText().toString();
        housePassword = editHousePassword.getText().toString();
        displayName = editDisplayName.getText().toString();
        if (!isLoginValid())
            return;
        Query queryHouseExists = db //query our db
                .child("Houses") //child database named "Houses"
                .orderByChild("houseName") //query on houseNames of "Houses"
                .equalTo(houseName); //any equaling the houseName in the field are returned

        queryHouseExists.addListenerForSingleValueEvent(houseDoesntExistListener); //run this query
        //once and return a snapshot to the listener
    }

    public void createHouseOnClick(View view) {
        EditText editHouseName = (EditText) findViewById(R.id.editHouseName);
        EditText editHousePassword = (EditText) findViewById(R.id.editHousePassword);
        EditText editDisplayName = (EditText) findViewById(R.id.editDisplayName);
        houseName = editHouseName.getText().toString();
        housePassword = editHousePassword.getText().toString();
        displayName = editDisplayName.getText().toString();
        if (!isLoginValid())
            return;
        Query queryHouseExists = db
                .child("Houses")
                .orderByChild("houseName")
                .equalTo(houseName);

        queryHouseExists.addListenerForSingleValueEvent(houseExistsListener);
    }

    // called when pressing "join house"
    ValueEventListener houseDoesntExistListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()) {
                if (snapshot.getChildrenCount() == 1){ //if there's 1 house with that name already
                    for (DataSnapshot child : snapshot.getChildren()){ //get that 1 house from the "list" of 1 returned child
                        Household household = child.getValue(Household.class); //convert it to a java object
                        if (household.getHousePassword().equals(housePassword)) { //check if the passwords match
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); //get current authenticated user
                            String uid = user.getUid();
                            household.pushMember(uid, displayName); //add member to the java object
                            db.child("Users").child(uid).setValue(new User(uid,houseName,displayName));
                            db.child("Locations").child(houseName).child(uid).setValue(new UserLocation(displayName,new LatLng(90,135)));
                            db.child("Houses").child(houseName).setValue(household); //update the house reference in the db with the java object
                            sharedPreferences.edit().putBoolean("isLoggedIn",true)
                                    .putString("houseName",houseName)
                                    .putString("displayName",displayName)
                                    .putFloat("houseLatitude", (float) household.getLocation().getLatitude())
                                    .putFloat("houseLongitude", (float) household.getLocation().getLongitude())
                                    .apply();

                            Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                            startActivity(intent); //proceed past this screen
                            return;
                        }
                    }
                }
                Toast toast = Toast.makeText(getApplicationContext(),"Incorrect password",Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            Toast toast = Toast.makeText(getApplicationContext(),"House doesn't exist",Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    //called by create house button
    ValueEventListener houseExistsListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()) {
                if (snapshot.getChildrenCount() >= 1){ //list of returned children has housenames
                    Toast toast = Toast.makeText(getApplicationContext(),"House already exists",Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
            }
            findViewById(R.id.createHouseButton).setVisibility(View.GONE);
            findViewById(R.id.joinHouseButton).setVisibility(View.GONE);
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
            updateMyLocation();
            PlaceHouseDialog place = new PlaceHouseDialog();
            place.show(getSupportFragmentManager(), "Placement");
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private void showMap() {
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        mapFragment.getMapAsync(googleMap -> {
            gMap = googleMap;
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition,18));
            gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(@NonNull LatLng latLng) {
                    Marker tmp = gMap.addMarker(new MarkerOptions().position(latLng));
                    HouseLocationDialog confirm = new HouseLocationDialog(tmp,houseName,housePassword,displayName);
                    confirm.show(supportFragmentManager,"Confirmation");
                    sharedPreferences.edit().putString("houseName",houseName)
                            .putString("displayName",displayName)
                            .putFloat("houseLatitude", (float) tmp.getPosition().latitude)
                            .putFloat("houseLongitude",(float)tmp.getPosition().longitude).apply();
                }
            });
        });
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.PostSignInContainer, mapFragment)
                .commit();
    }

    private boolean isLoginValid() {
        if (houseName.length()<2) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "House name must be at least 3 characters",Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        else if (housePassword.length()<8) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "House password must be at least 8 characters",Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        else return true;
    }

    private void updateMyLocation() {
        // check if permission is granted
        int permission = ActivityCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        // if not, ask for it
        if (permission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        // if permission is granted, display marker at current location
        else {
            mFusedLocationProviderClient.getCurrentLocation(LocationRequest.QUALITY_HIGH_ACCURACY, null)
                    .addOnCompleteListener(this, task -> {
                        Location mLastKnownLocation = task.getResult();
                        if (task.isSuccessful() && mLastKnownLocation != null) {
                            currentPosition = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                            showMap();
                        }
                    });
        }
    }
    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateMyLocation();
            }
        }
    }
}