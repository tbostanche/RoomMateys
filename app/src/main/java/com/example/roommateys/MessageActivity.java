package com.example.roommateys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MessageActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 12; //could've been any number!
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LatLng currentPosition = new LatLng(0.0, 0.0);
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("com.example.roommateys", Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("isLoggedIn",true).apply();
        setContentView(R.layout.activity_message);
        db = FirebaseDatabase.getInstance().getReference();
        displayMessages();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        updateMyLocation();
    }

    public void displayMessages() {
        Query query = db.child("Messages")
                .child(sharedPreferences.getString("houseName",""))
                .limitToLast(50);
        FirebaseRecyclerOptions<Message> options = new FirebaseRecyclerOptions.Builder<Message>()
                .setQuery(query,Message.class)
                .build();
        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Message, MessageHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MessageHolder holder, int position, @NonNull Message model) {
                holder.messageText.setText(model.getMessageText());
                if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(model.getUid())) {
                    holder.constraintLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    holder.displayName.setText(model.getDisplayName());
                    holder.displayName.setGravity(Gravity.LEFT);
                    holder.messageText.setGravity(Gravity.LEFT);
                    holder.linearLayout.setGravity(Gravity.LEFT);
                }
                else {
                    holder.displayName.setText("Me");
                }
            }

            @NonNull
            @Override
            public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater
                        .from(parent.getContext()).inflate(R.layout.message,parent,false);
                return new MessageHolder(view);
            }
        };
        RecyclerView recyclerView = findViewById(R.id.MessageRecycler);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.startListening();
    }

    public void sendMessageOnClick (View view) {
        EditText editMessage = (EditText) findViewById(R.id.editMessage);
        String message = editMessage.getText().toString();
        if (message.length() == 0) {
            return;
        } else if (message.length() > 256) {
            Toast toast = Toast.makeText(getApplicationContext(),"Max message length: 256",Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        DatabaseReference ref = db.child("Messages").child(sharedPreferences.getString("houseName","")).push();
        ref.setValue(new Message(sharedPreferences.getString("displayName",""),message,FirebaseAuth.getInstance().getCurrentUser().getUid()));
        editMessage.setText("");
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
                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            db.child("Locations")
                                    .child(sharedPreferences.getString("houseName",null))
                                    .child(uid)
                                    .setValue(new UserLocation(sharedPreferences
                                            .getString("displayName",null),currentPosition));
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
    public void mapOnClick(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }
}