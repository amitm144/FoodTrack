package com.example.foodtrack.Activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.foodtrack.Model.CurrentUserSingleton;
import com.example.foodtrack.Model.User;
import com.example.foodtrack.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            result -> onSignInResult(result)
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).hide();

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = mAuth.getCurrentUser();

        // Check if user is already authenticated or not
        if (firebaseUser == null) {
            // User is not authenticated, proceed with the login process
            login();
        } else {
            // User is already authenticated, load user profile
            loadUserProfile();
        }
    }

    private void goToMainActivity() {
        // Navigate to the main activity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        // Handle sign-in result here
    }

    private void login() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.PhoneBuilder().build()
        );

        int authMethodPickerLayout = R.layout.activity_login;

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .setTheme(R.style.AppTheme_AuthUI)
                .setLogo(R.drawable.logofoodtracker)
                .build();

        signInLauncher.launch(signInIntent);
    }

    private void loadUserProfile() {
        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("USERS");

        // Retrieve the user data from the database based on the phone number
        usersReference.child(firebaseUser.getPhoneNumber()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // User data exists in the database
                    CurrentUserSingleton.getInstance().setCurrentUser(snapshot.getValue(User.class));

                    // Check user data for required fields
                    User user = CurrentUserSingleton.getInstance().getCurrentUser();
                    if (user.getUserFoodHistory() == null || user.getFirstName() == null || user.getCalorieBudget() == 0) {
                        // Required fields are missing, navigate to the new user activity
                        goToNewUserActivity();
                    } else {
                        // All required fields are present, navigate to the main activity
                        goToMainActivity();
                    }
                } else {
                    // User data doesn't exist in the database, create a new user
                    createUser();

                    // Navigate to the new user activity
                    goToNewUserActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error here
            }
        });
    }

    private void goToNewUserActivity() {
        // Navigate to the new user activity
        Intent intent = new Intent(this, NewUserActivity.class);
        startActivity(intent);
        finish();
    }

    private void createUser() {
        // Create a new user object
        User user = new User();
        user.setPhoneNumber(firebaseUser.getPhoneNumber());
        CurrentUserSingleton.getInstance().setCurrentUser(user);

        // Save the user object to the database
        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("USERS");
        usersReference.child(firebaseUser.getPhoneNumber()).setValue(user);
    }
}





