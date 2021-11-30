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
        db = FirebaseDatabase.getInstance().getReference();
    }

    public void joinHouseOnClick(View view) {
        EditText editHouseName = (EditText) findViewById(R.id.editHouseName);
        EditText editHousePassword = (EditText) findViewById(R.id.editHousePassword);
        houseName = editHouseName.getText().toString();
        housePassword = editHousePassword.getText().toString();

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
                        for (DataSnapshot nestedChild : child.getChildren()) {
                            if (nestedChild.getKey().equals("housePassword") && nestedChild.getValue().toString().equals(housePassword)) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                db.child("Houses").child(houseName).setValue(new Household(houseName,housePassword,user.getUid()));//TODO don't overwrite existing users
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

    public void joinHouseOnClick() {

    }
}