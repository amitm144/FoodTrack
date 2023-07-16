package com.example.foodtrack.Model;

import static com.example.foodtrack.Model.Food.nameComparator;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FireBaseAction {

    public static void editFood(User user, String date, Food currentFood, Food updatedFood) {
        // Edit a food item in the user's food history
        ArrayList<Food> foodArrayList = user.getUserFoodHistory().get(date);
        foodArrayList.set(foodArrayList.indexOf(currentFood), updatedFood);
        save(user);
    }

    public static void deleteFood(User user, String date, Food food) {
        // Delete a food item from the user's food history
        ArrayList<Food> foodArrayList = user.getUserFoodHistory().get(date);
        foodArrayList.remove(foodArrayList.indexOf(food));
        save(user);
    }

    public static void save(User user) {
        // Save the user object to the Firebase Realtime Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("USERS");
        usersRef.child(user.getPhoneNumber()).setValue(user);
        CurrentUserSingleton.getInstance().setCurrentUser(user);
    }

    public static void getFoodByText(String newText, String phoneNumber, FoodSearchCallback callback) {
        // Retrieve food items from the Firebase Realtime Database based on the search text
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("FoodDB");
        ArrayList<Food> foodsFromServer = new ArrayList<>();

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    Food food = child.getValue(Food.class);
                    if (food.getName().toLowerCase().contains(newText.toLowerCase()) && (food.getFrom().equals("API") || food.getFrom().equals(phoneNumber))) {
                        foodsFromServer.add(food);
                    }
                }
                Comparator<Food> comparator = nameComparator(newText);
                Collections.sort(foodsFromServer, comparator);

                callback.onFoodSearchComplete(foodsFromServer);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFoodSearchComplete(foodsFromServer);
            }
        });
    }

    public static void saveFoodToServer(Food food) {
        // Save a food item to the Firebase Realtime Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("FoodDB");
        reference.child(food.hashCode() + "").setValue(food);
    }

    public interface FoodSearchCallback {
        void onFoodSearchComplete(ArrayList<Food> foods);
    }

}
