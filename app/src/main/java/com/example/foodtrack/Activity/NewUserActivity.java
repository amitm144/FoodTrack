package com.example.foodtrack.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodtrack.Model.CurrentUserSingleton;
import com.example.foodtrack.Model.User;
import com.example.foodtrack.databinding.ActivityNewUserBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class NewUserActivity extends AppCompatActivity {

    private ActivityNewUserBinding binding;
    private User user;
    private String firstName;
    private String lastName;
    private int budget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewUserBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Retrieve current user and its data
        user = CurrentUserSingleton.getInstance().getCurrentUser();
        firstName = user.getFirstName() == null ? "" : user.getFirstName();
        lastName = user.getLastName() == null ? "" : user.getLastName();
        budget = user.getCalorieBudget();

        // Set initial values in the UI
        if (!firstName.isEmpty() && !lastName.isEmpty() && budget != 0)
            binding.join.setText("Update");

        binding.firstName.setText(firstName);
        binding.lastName.setText(lastName);
        binding.calorieBudget.setProgress(budget);
        binding.calorieBudgetLabel.setText(String.valueOf(budget));

        binding.calorieBudget.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update the budget value when the seek bar progress changes
                progress = progress / 100;
                progress = progress * 100;
                budget = progress;
                binding.calorieBudgetLabel.setText(String.valueOf(budget));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not needed for this implementation
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Not needed for this implementation
            }
        });

        binding.join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve the entered first name, last name, and budget
                firstName = binding.firstName.getText().toString();
                lastName = binding.lastName.getText().toString();

                // Validate the entered data
                if (firstName.isEmpty() || lastName.isEmpty() || budget == 0) {
                    Toast.makeText(NewUserActivity.this, "Fill all the data", Toast.LENGTH_SHORT).show();
                } else {
                    // Update the user object with the entered data
                    user
                            .setFirstName(firstName)
                            .setLastName(lastName)
                            .setCalorieBudget(budget)
                            .setUserFoodHistory(user.getUserFoodHistory() == null ? new HashMap<>() : user.getUserFoodHistory());

                    // Save the updated user object to the database
                    DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("USERS");
                    usersReference.child(user.getPhoneNumber()).setValue(user);

                    // Navigate to the main activity
                    goToMainActivity();
                }
            }
        });
    }

    private void goToMainActivity() {
        // Navigate to the main activity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
