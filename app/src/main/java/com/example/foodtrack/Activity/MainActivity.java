package com.example.foodtrack.Activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.foodtrack.Api.FoodApi;
import com.example.foodtrack.Fragment.AddFragment;
import com.example.foodtrack.Fragment.ProfileFragment;
import com.example.foodtrack.Fragment.SearchFragment;
import com.example.foodtrack.Model.Food;
import com.example.foodtrack.R;
import com.example.foodtrack.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements FoodApi.FoodApiListener {

    private ActivityMainBinding binding;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Set the initial fragment to be displayed
        getSupportFragmentManager().beginTransaction().replace(R.id.body_container, new ProfileFragment()).commit();
        binding.bottomNavigation.setSelectedItemId(R.id.nav_profile);

        // Handle bottom navigation item selection
        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_profile:
                        currentFragment = new ProfileFragment();
                        break;
                    case R.id.nav_search:
                        currentFragment = new SearchFragment();
                        break;
                    case R.id.nav_add:
                        currentFragment = new AddFragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.body_container, currentFragment).commit();
                return true;
            }
        });

        // Create the database from API in the first time
//        createNewDB();
    }

    @Override
    public void onFoodDBReceived(ArrayList<Food> foodDB) {
        // Callback method when the food database is received from the API
        write(foodDB);
    }

    private void write(ArrayList<Food> foodDB) {
        // Write the food database to the Firebase Realtime Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference foodRef = database.getReference("FoodDB");
        for (Food food : foodDB) {
            foodRef.child(food.hashCode() + "").setValue(food);
        }
    }

    private void createNewDB() {
        // Create a new instance of the FoodApi and initialize it
        FoodApi foodApi = new FoodApi(this);
        foodApi.init(this);
    }

    private void removeDB() {
        // Remove the food database from the Firebase Realtime Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("FoodDB");
        reference.removeValue();
    }
}
