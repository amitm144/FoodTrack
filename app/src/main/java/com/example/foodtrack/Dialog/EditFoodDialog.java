package com.example.foodtrack.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.foodtrack.Model.Food;
import com.example.foodtrack.Model.User;
import com.example.foodtrack.databinding.DialogAddFoodBinding;


public class EditFoodDialog extends Dialog {

    private Context context;
    private Food updatedFood, oldFood;
    private double oldGram;

    private DialogAddFoodBinding binding;

    private OnFoodEditedListener listener;
    private boolean flag = false;

    public interface OnFoodEditedListener {
        void onFoodEdited(Food oldFood, Food updatedFood);
    }

    public void setOnFoodEditedListener(OnFoodEditedListener listener) {
        this.listener = listener;
    }

    public EditFoodDialog(@NonNull Context context, Food food, User user) {
        super(context);
        this.context = context;
        this.updatedFood = food;
        this.oldFood = food;
        this.oldGram = food.getGram();

        init();
    }

    private void init() {
        // Inflate the dialog layout using the binding class
        binding = DialogAddFoodBinding.inflate(LayoutInflater.from(context), null, false);
        setContentView(binding.getRoot());
        binding.currentDate.setVisibility(View.INVISIBLE);
        binding.addBTN.setText("Update");
        updateFoodData();

        // Handle changes in the seek bar for adjusting grams
        binding.gramsSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = progress / 10;
                progress = progress * 10;

                if (progress > 0) {
                    updatedFood.setGram(progress);
                    binding.gramsLBL.setText(String.valueOf(progress));
                    updateFoodData();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Handle click on the update button
        binding.addBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = true;
                listener.onFoodEdited(oldFood, updatedFood);
                dismiss();
            }
        });
    }

    private void updateFoodData() {
        // Update the displayed food data
        binding.foodName.setText(updatedFood.getName());
        binding.gramsLBL.setText(String.valueOf((int) updatedFood.getGram()));
        binding.gramsSeekBar.setProgress((int) updatedFood.getGram());
        binding.energyLBL.setText(String.format("%.1f", updatedFood.getEnergy()));
        binding.proteinLBL.setText(String.format("%.1f", updatedFood.getProtein()));
        binding.carbsLBL.setText(String.format("%.1f", updatedFood.getCarb()));
        binding.fatLBL.setText(String.format("%.1f", updatedFood.getFat()));
    }

    @Override
    public void setOnCancelListener(@Nullable OnCancelListener listener) {
        super.setOnCancelListener(listener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // If the dialog is dismissed without updating the food, revert the gram value to the old value
        if (!flag) {
            updatedFood.setGram(oldGram);
        }
    }
}
