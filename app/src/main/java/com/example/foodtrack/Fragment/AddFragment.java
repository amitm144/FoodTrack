package com.example.foodtrack.Fragment;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.foodtrack.Dialog.AddFoodDialog;
import com.example.foodtrack.Dialog.ConfirmationDialog;
import com.example.foodtrack.Model.CurrentUserSingleton;
import com.example.foodtrack.Model.FireBaseAction;
import com.example.foodtrack.Model.Food;
import com.example.foodtrack.Model.User;
import com.example.foodtrack.databinding.FragmentAddBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class AddFragment extends Fragment {

    FragmentAddBinding binding;
    private boolean confirmation;
    User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddBinding.inflate(inflater, container, false);
        user = CurrentUserSingleton.getInstance().getCurrentUser();

        // Set click listener for the "Upload" button
        binding.newItemUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewFood();
            }
        });

        return binding.getRoot();
    }

    private void addNewFood() {
        confirmation = true;
        String name = binding.foodName.getText() == null ? "" : binding.foodName.getText().toString();
        double energy = parseEditText(binding.calorie.getText());
        double protein = parseEditText(binding.protein.getText());
        double carbs = parseEditText(binding.carbs.getText());
        double fat = parseEditText(binding.fat.getText());

        if (confirmation && !name.isEmpty()) {
            // Create a new Food object with the input values
            Food food = new Food();
            food.setName(name).setGram(100).setEnergy(energy).setProtein(protein).setCarb(carbs).setFat(fat).setFrom(user.getPhoneNumber());

            // Save the food to the server
            saveToServer(food);

            // Show the confirmation dialog before adding the food to the user's list
            showConfirmDialog(food);
        } else {
            Toast.makeText(getContext(), "Fill all the fields", Toast.LENGTH_SHORT).show();
        }
    }

    private double parseEditText(Editable editable) {
        if (editable != null && editable.length() > 0) {
            try {
                return Double.parseDouble(editable.toString());
            } catch (NumberFormatException e) {
                // Handle parsing error here
            }
        }
        confirmation = false;
        return 0.0;
    }

    private void saveToServer(Food food) {
        // Save the food to the server using Firebase
        FireBaseAction.saveFoodToServer(food);
    }

    private void showConfirmDialog(Food food) {
        ConfirmationDialog confirmationDialog = new ConfirmationDialog(getContext(), food, "Confirmation", "Add " + food.getName() + " to your today's list?");
        confirmationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        confirmationDialog.setOnConfirmationClickListener(new ConfirmationDialog.OnConfirmationClickListener() {
            @Override
            public void onClick(View view, Food food) {
                // Show the add food dialog after confirming
                showAddFoodDialog(food);
            }
        });
        confirmationDialog.show();
    }

    private void showAddFoodDialog(Food food) {
        AddFoodDialog addFoodDialog = new AddFoodDialog(getContext(), food, user);
        addFoodDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addFoodDialog.show();
    }
}
