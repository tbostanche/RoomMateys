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
        houseName = editHouseName.getText().toString();
        housePassword = editHousePassword.getText().toString();
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
                            //a user should not be in a house and on this screen, TODO remove
                            if (household.getMembers().contains(user.getUid())) { //Shouldn't reach
                                sharedPreferences.edit().putBoolean("isLoggedIn",true).apply();
                                Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                                startActivity(intent);
                            }
                            else {
                                household.pushMember(user.getUid()); //add member to the java object
                                db.child("Houses").child(houseName).setValue(household); //update the house reference in the db with the java object
                                sharedPreferences.edit().putBoolean("isLoggedIn",true).apply(); //add logged in preference
                                sharedPreferences.edit().putString("houseName",houseName); //add house name preference
                                Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                                startActivity(intent); //proceed past this screen
                            }
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
            //else house does not yet exist; create new house
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); //get authenticated user
            db.child("Houses").child(houseName).setValue(new Household(houseName,housePassword,user.getUid())); //add a new house to the Houses/houseName path, set the value to a new java object with houseName, housePassword, and the first member as this user
            sharedPreferences.edit().putBoolean("isLoggedIn",true).apply(); //add preference
            Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
            startActivity(intent);// proceed past this screen
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