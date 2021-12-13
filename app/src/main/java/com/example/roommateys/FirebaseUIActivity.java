package com.example.roommateys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class FirebaseUIActivity extends AppCompatActivity {

    FirebaseAuth auth;
    SharedPreferences sharedPreferences;

    // See: https://developer.android.com/training/basics/intents/result
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            this::onSignInResult
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_ui);
        sharedPreferences = getSharedPreferences("com.example.roommateys", Context.MODE_PRIVATE);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(this, PostSignInActivity.class);
            startActivity(intent);
        } else {
            createSignInIntent();
        }
    }

    public void createSignInIntent() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.PhoneBuilder().build());

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.mipmap.ic_launcher_ship_foreground)
                .build();
        signInLauncher.launch(signInIntent);
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
            Query queryUserExists = db
                    .child("Users")
                    .orderByChild("uid")
                    .equalTo(user.getUid());
            queryUserExists.addListenerForSingleValueEvent(userExistsListener);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),response.getError().getErrorCode(),Toast.LENGTH_SHORT);
            toast.show();
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
        }
    }

    ValueEventListener userExistsListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()) {
                if (snapshot.getChildrenCount() == 1) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        //If user already exists, they must be in a house, so skip PostSignInActivity
                        User user = child.getValue(User.class);
                        sharedPreferences.edit().putBoolean("isLoggedIn", true)
                        .putString("houseName", user.getHouseName())
                        .putString("displayName",user.getDisplayName()).apply();
                        Intent intent = new Intent(FirebaseUIActivity.this, MessageActivity.class);
                        startActivity(intent);
                        return;
                    }
                }
            }
            //else go to post sign in activity
            Intent intent = new Intent(FirebaseUIActivity.this, PostSignInActivity.class);
            startActivity(intent);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

}
