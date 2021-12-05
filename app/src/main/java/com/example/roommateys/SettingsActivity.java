package com.example.roommateys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sharedPreferences = getSharedPreferences("com.example.roommateys", Context.MODE_PRIVATE);
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
        sharedPreferences.edit().remove("isLoggedIn").remove("houseName").apply();
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(SettingsActivity.this,FirebaseUIActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }
    public void leaveHouseOnClick(View view) {
        //TODO remove user from database entirely
        sharedPreferences.edit().remove("isLoggedIn").remove("houseName").apply();
        Intent intent = new Intent(this,PostSignInActivity.class);
        startActivity(intent);
    }

    public void changeDisplayNameOnClick(View view) {
        //TODO change display name of user in db
    }

    public void deleteAccountOnClick(View view) {
        //TODO remove user from database entirely
        sharedPreferences.edit().remove("isLoggedIn").remove("houseName").apply();
        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(SettingsActivity.this,FirebaseUIActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }
}