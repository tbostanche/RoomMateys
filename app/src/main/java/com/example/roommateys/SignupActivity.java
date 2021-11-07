package com.example.roommateys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SignupActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        sharedPreferences = getSharedPreferences("com.example.roommateys", Context.MODE_PRIVATE);
    }

    public void signupClickFunction(View view) {
        EditText usernameField = (EditText) findViewById(R.id.signupUsername);
        EditText passwordField = (EditText) findViewById(R.id.signupPassword);
        EditText confirmPasswordField = (EditText) findViewById(R.id.signupConfirmPassword);
        EditText emailField = (EditText) findViewById(R.id.signupEmail);
        if (usernameField.length() == 0 || passwordField.length() == 0 ||
                confirmPasswordField.length() == 0 || emailField.length() == 0) return;
        String usernameString = usernameField.getText().toString();
        sharedPreferences.edit().putString("username", usernameString).apply();
        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply();
        Intent intent = new Intent(this, MessageActivity.class);
        startActivity(intent);
    }
}