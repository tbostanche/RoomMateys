package com.example.roommateys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.roommateys", Context.MODE_PRIVATE);
        boolean darkmode = sharedPreferences.getBoolean("darkMode", false);
        if (darkmode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        setContentView(R.layout.splash_screen);

        Thread background = new Thread() {
            public void run() {
                try {
                    // Thread will sleep for .5 seconds
                    sleep(500);

                    // After .5 seconds redirect to another intent
                    Intent i=new Intent(getBaseContext(),FirebaseUIActivity.class);
                    startActivity(i);

                    //Remove activity
                    finish();
                } catch (Exception e) {
                }
            }
        };
        // start thread
        background.start();
    }
}