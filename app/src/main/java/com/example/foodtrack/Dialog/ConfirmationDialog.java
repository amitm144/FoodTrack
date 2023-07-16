package com.example.foodtrack.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.foodtrack.Model.Food;
import com.example.foodtrack.databinding.DialogConfirmationBinding;

public class ConfirmationDialog extends Dialog {

    private String title, content;
    private Context context;
    private Food food;
    private DialogConfirmationBinding binding;
    private OnConfirmationClickListener onConfirmationClickListener;

    public ConfirmationDialog(@NonNull Context context, Food food, String title, String content) {
        super(context);
        this.context = context;
        this.food = food;
        this.title = title;
        this.content = content;
        init();
    }

    public void setOnConfirmationClickListener(OnConfirmationClickListener onConfirmationClickListener) {
        this.onConfirmationClickListener = onConfirmationClickListener;
    }

    private void init() {
        // Inflate the dialog layout using the binding class
        binding = DialogConfirmationBinding.inflate(LayoutInflater.from(context), null, false);
        setContentView(binding.getRoot());

        // Set the title and content of the dialog
        binding.title.setText(title);
        binding.contentLabel.setText(content);

        // Handle click on the confirm button
        binding.confirmBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the listener with the clicked food item and dismiss the dialog
                onConfirmationClickListener.onClick(v, food);
                dismiss();
            }
        });

        // Handle click on the dismiss button
        binding.dismissBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the dialog
                dismiss();
            }
        });
    }

    public interface OnConfirmationClickListener {
        void onClick(View view, Food food);
    }
}
