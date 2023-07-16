package com.example.foodtrack.Fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.foodtrack.Activity.NewUserActivity;
import com.example.foodtrack.Adapter.RecycleView_DailyFood_Adapter;
import com.example.foodtrack.Dialog.ConfirmationDialog;
import com.example.foodtrack.Dialog.EditFoodDialog;
import com.example.foodtrack.Model.CurrentUserSingleton;
import com.example.foodtrack.Model.FireBaseAction;
import com.example.foodtrack.Model.Food;
import com.example.foodtrack.Model.User;
import com.example.foodtrack.R;
import com.example.foodtrack.databinding.FragmentProfileBinding;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ProfileFragment extends Fragment implements EditFoodDialog.OnFoodEditedListener {

    private FragmentProfileBinding binding;
    private RecycleView_DailyFood_Adapter dailyFood_adapter;
    private Calendar calendar;
    private String currentDate;
    private double energy, protein, carbs, fat;
    private int calorieBudget;
    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        // Initialize the calendar and user
        calendar = Calendar.getInstance();
        user = CurrentUserSingleton.getInstance().getCurrentUser();

        // Initialize the labels and RecyclerView
        initLabels();
        initRecycleView();

        // Update the initial date and set up the buttons
        updateDate();
        setButtons();

        return binding.getRoot();
    }

    private void initLabels() {
        // Initialize the calorie budget from the user data
        calorieBudget = user.getCalorieBudget();
        binding.energyProgressCircularProfileFragment.setMax(calorieBudget);
        binding.energyMaxLBL.setText(String.valueOf(calorieBudget));
    }

    private void initRecycleView() {
        // Initialize the RecyclerView and its adapter
        dailyFood_adapter = new RecycleView_DailyFood_Adapter(new ArrayList<>());

        // Set the click listeners for the food items in the RecyclerView
        dailyFood_adapter.setOnFoodClickListener(new RecycleView_DailyFood_Adapter.OnFoodClickListener() {
            @Override
            public void onClick(View view, Food food, int position) {
                // Show a confirmation dialog for editing the food item
                ConfirmationDialog confirmationDialog = new ConfirmationDialog(getContext(), food, "Edit", "Are you sure you want to edit: " + food.getName());
                confirmationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                confirmationDialog.setOnConfirmationClickListener(new ConfirmationDialog.OnConfirmationClickListener() {
                    @Override
                    public void onClick(View view, Food food) {
                        edit(food);
                    }
                });
                confirmationDialog.show();
            }

            @Override
            public void onLongClick(View view, Food food, int position) {
                // Show a confirmation dialog for removing the food item
                ConfirmationDialog confirmationDialog = new ConfirmationDialog(getContext(), food, "Remove", "Are you sure you want to remove: " + food.getName());
                confirmationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                confirmationDialog.setOnConfirmationClickListener(new ConfirmationDialog.OnConfirmationClickListener() {
                    @Override
                    public void onClick(View view, Food food) {
                        remove(food);
                    }
                });
                confirmationDialog.show();
            }
        });

        // Set up the RecyclerView with a LinearLayoutManager and the adapter
        binding.profileRCVDailyFood.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.profileRCVDailyFood.setHasFixedSize(true);
        binding.profileRCVDailyFood.setAdapter(dailyFood_adapter);
    }

    private void updateDate() {
        // Update the current date and display it
        currentDate = formatDate(calendar);
        String displayDate = displayDateFormat(calendar);
        binding.currentDate.setText(displayDate);

        // Reset the summary values and retrieve the food items for the current date
        resetSummary();
        getFoodFromServer();
    }

    private void setButtons() {
        // Set click listeners for the date navigation buttons
        binding.datePrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to the previous date and update the view
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                updateDate();
            }
        });

        binding.dateNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar tomorrow = Calendar.getInstance();
                tomorrow.add(Calendar.DAY_OF_MONTH, 1);

                if (calendar.before(tomorrow) && !isSameDay(calendar, Calendar.getInstance())) {
                    // Go to the next date (limited to today's date or earlier) and update the view
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    updateDate();
                } else {
                    Toast.makeText(getContext(), "Can't see the future ;)", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.currentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the date picker dialog
                showDatePickerDialog();
            }
        });

        binding.setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the user settings activity
                Intent intent = new Intent(getContext(), NewUserActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getFoodFromServer() {
        // Reset the summary values and update the food items in the RecyclerView
        resetSummary();
        dailyFood_adapter.updateList(null);

        // Retrieve the food items for the current date from the user's data
        ArrayList<Food> foodArrayList = user.getUserFoodHistory().get(currentDate);
        if (foodArrayList != null) {
            dailyFood_adapter.updateList(foodArrayList);
            foodArrayList.forEach(f -> updateSummary(f));
            setTextNutrition();
        }
    }

    private void resetSummary() {
        // Reset the summary values for energy, protein, carbs, and fat
        energy = 0;
        protein = 0;
        carbs = 0;
        fat = 0;
        setTextNutrition();
    }

    @SuppressLint("DefaultLocale")
    private void setTextNutrition() {
        // Update the displayed nutrition values
        binding.energyCurrentLBL.setText(String.format("%.1f", energy));
        binding.energyCurrentLeftLBL.setText(String.format("%.1f", calorieBudget - energy));
        binding.proteinLBLProfileFragment.setText(String.format("%.1f", protein));
        binding.carbsLBLProfileFragment.setText(String.format("%.1f", carbs));
        binding.fatLBLProfileFragment.setText(String.format("%.1f", fat));
        binding.energyProgressCircularProfileFragment.setProgress((int) energy, true);


    }


        private void updateSummary(Food food) {
        // Update the summary values with the nutrition data from a food item
        energy += food.getEnergy();
        protein += food.getProtein();
        carbs += food.getCarb();
        fat += food.getFat();
    }

    private void edit(Food food) {
        // Show the edit food dialog
        showEditFoodDialog(food);
    }

    private void showEditFoodDialog(Food food) {
        // Show the dialog for editing a food item
        EditFoodDialog editFoodDialog = new EditFoodDialog(getContext(), food, user);
        editFoodDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        editFoodDialog.setOnFoodEditedListener(this);
        editFoodDialog.show();
    }

    @Override
    public void onFoodEdited(Food oldFood, Food updatedFood) {
        // Handle the event of a food item being edited
        FireBaseAction.editFood(user, currentDate, oldFood, updatedFood);
        getFoodFromServer();
    }

    private void remove(Food food) {
        // Remove the food item from the server
        FireBaseAction.deleteFood(user, currentDate, food);
        getFoodFromServer();
    }

    private void showDatePickerDialog() {
        // Show the date picker dialog for selecting a date
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // Update the calendar with the selected date and refresh the view
                calendar.set(year, month, dayOfMonth);
                updateDate();
            }
        }, year, month, day);

        // Set the maximum date to today's date
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private String displayDateFormat(Calendar calendar) {
        // Format the calendar date into a display-friendly format
        Calendar today = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_MONTH, -1);

        if (isSameDay(calendar, today))
            return "Today";
        else if (isSameDay(calendar, yesterday))
            return "Yesterday";
        else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM", Locale.getDefault());
            return dateFormat.format(calendar.getTime());
        }
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        // Check if two calendars represent the same day
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    private String formatDate(Calendar calendar) {
        // Format the calendar date into the required format for storing in the database
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return day + "-" + (month + 1) + "-" + year;
    }
}
