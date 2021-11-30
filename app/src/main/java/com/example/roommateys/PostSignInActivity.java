package com.example.roommateys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

public class PostSignInActivity extends AppCompatActivity {

    private DatabaseReference db;
    private String houseName;
    private String housePassword;
    SharedPreferences sharedPreferences;

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
        houseName = editHouseName.getText().toString();
        housePassword = editHousePassword.getText().toString();
        if (!isLoginValid())
            return;
        Query queryHouseExists = FirebaseDatabase.getInstance().getReference()
                .child("Houses")
                .orderByChild("houseName")
                .equalTo(houseName);

        queryHouseExists.addListenerForSingleValueEvent(houseDoesntExistListener);
    }

    public void createHouseOnClick(View view) {
        EditText editHouseName = (EditText) findViewById(R.id.editHouseName);
        EditText editHousePassword = (EditText) findViewById(R.id.editHousePassword);
        houseName = editHouseName.getText().toString();
        housePassword = editHousePassword.getText().toString();
        if (!isLoginValid())
            return;
        Query queryHouseExists = FirebaseDatabase.getInstance().getReference()
                .child("Houses")
                .orderByChild("houseName")
                .equalTo(houseName);

        queryHouseExists.addListenerForSingleValueEvent(houseExistsListener);
    }

    ValueEventListener houseDoesntExistListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()) {
                if (snapshot.getChildrenCount() == 1){
                    for (DataSnapshot child : snapshot.getChildren()){
                        Household household = child.getValue(Household.class);
                        if (household.getHousePassword().equals(housePassword)) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (household.getMembers().contains(user.getUid())) { //Shouldn't reach
                                sharedPreferences.edit().putBoolean("isLoggedIn",true).apply();
                                Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                                startActivity(intent);
                                return;
                            }
                            else {
                                household.pushMember(user.getUid());
                                db.child("Houses").child(houseName).setValue(household);
                                sharedPreferences.edit().putBoolean("isLoggedIn",true).apply();
                                Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                                startActivity(intent);
                                return;
                            }
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

    ValueEventListener houseExistsListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()) {
                if (snapshot.getChildrenCount() == 1){
                    Toast toast = Toast.makeText(getApplicationContext(),"House already exists",Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
            }
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            db.child("Houses").child(houseName).setValue(new Household(houseName,housePassword,user.getUid()));
            sharedPreferences.edit().putBoolean("isLoggedIn",true).apply();
            Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
            startActivity(intent);
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

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
}