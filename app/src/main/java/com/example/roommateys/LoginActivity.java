package com.example.roommateys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EditText usernameField = (EditText) findViewById(R.id.editUsername);
        sharedPreferences = getSharedPreferences("com.example.roommateys", Context.MODE_PRIVATE);
        usernameField.setText(sharedPreferences.getString("username",""));
    }

    public void loginClickFunction(View view) {
        EditText usernameField = (EditText) findViewById(R.id.editUsername);
        EditText passwordField = (EditText) findViewById(R.id.editPassword);
        if (usernameField.length() == 0 || passwordField.length() == 0) return;
        String usernameString = usernameField.getText().toString();
        sharedPreferences.edit().putString("username",usernameString).apply();
        sharedPreferences.edit().putBoolean("isLoggedIn",true).apply();
        Intent intent = new Intent(this, MessageActivity.class);
        startActivity(intent);
    }
}