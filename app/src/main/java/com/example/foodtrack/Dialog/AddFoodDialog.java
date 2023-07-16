package com.example.foodtrack.Dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.foodtrack.Model.FireBaseAction;
import com.example.foodtrack.Model.Food;
import com.example.foodtrack.Model.User;
import com.example.foodtrack.databinding.DialogAddFoodBinding;

import java.util.ArrayList;
import java.util.Calendar;

public class AddFoodDialog extends Dialog {

    private Calendar calendar;
    private String currentDate;
    private Context context;
    private Food food;
    private User user;
    private DialogAddFoodBinding binding;

    public AddFoodDialog(@NonNull Context context, Food food, User user) {
        super(context);
        this.context = context;
        this.food = food;
        this.calendar = Calendar.getInstance();
        this.user = user;
        init();
    }

    private void init() {
        // Inflate the dialog layout using the binding class
        binding = DialogAddFoodBinding.inflate(LayoutInflater.from(context), null, false);
        setContentView(binding.getRoot());

        // Initialize the date and food data
        updateDate();
        updateFoodData();

        // Handle click on the current date text to show the date picker dialog
        binding.currentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Handle changes in the grams seek bar
        binding.gramsSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Adjust the progress value to be in multiples of 10
                progress = progress / 10;
                progress = progress * 10;

                if (progress > 0) {
                    // Update the food gram value and UI
                    food.setGram(progress);
                    binding.gramsLBL.setText(String.valueOf(progress));
                    updateFoodData();
                }
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

        // Handle click on the add button
        binding.addBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save the food entry and dismiss the dialog
                save();
                dismiss();
            }
        });
    }

    private void updateFoodData() {
        // Update the views with the food data
        binding.foodName.setText(food.getName());
        binding.gramsLBL.setText(String.valueOf((int) food.getGram()));
        binding.gramsSeekBar.setProgress((int) food.getGram());
        binding.energyLBL.setText(String.format("%.1f", food.getEnergy()));
        binding.proteinLBL.setText(String.format("%.1f", food.getProtein()));
        binding.carbsLBL.setText(String.format("%.1f", food.getCarb()));
        binding.fatLBL.setText(String.format("%.1f", food.getFat()));
    }

    private void updateDate() {
        currentDate = formatDate(calendar);
        binding.currentDate.setText(currentDate);
    }

    private String formatDate(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return day + "-" + (month + 1) + "-" + year;
    }

    private void showDatePickerDialog() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // Update the calendar with the selected date
                calendar.set(year, month, dayOfMonth);
                updateDate();
            }
        }, year, month, day);

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void save() {
        // Check if the food entry list for the current date exists, create if necessary
        if (user.getUserFoodHistory().get(currentDate) == null)
            user.getUserFoodHistory().put(currentDate, new ArrayList<Food>());

        // Add the food entry to the user's food history
        user.getUserFoodHistory().get(currentDate).add(food);
        // Save the updated user data to the database
        FireBaseAction.save(user);
    }

    @Override
    public void setOnCancelListener(@Nullable OnCancelListener listener) {
        super.setOnCancelListener(listener);
    }
}
