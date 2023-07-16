package com.example.foodtrack.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodtrack.Model.Food;
import com.example.foodtrack.R;
import com.example.foodtrack.databinding.RecycleViewDailyFoodBinding;

import java.util.ArrayList;

public class RecycleView_DailyFood_Adapter extends RecyclerView.Adapter<RecycleView_DailyFood_Adapter.DailyFoodHolder> {

    private ArrayList<Food> foodList;
    private OnFoodClickListener onClickListener;

    public RecycleView_DailyFood_Adapter(ArrayList<Food> foodList) {
        this.foodList = foodList;
    }

    public void updateList(ArrayList<Food> foods) {
        this.foodList = foods;
        notifyDataSetChanged();
    }

    public void setOnFoodClickListener(OnFoodClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public DailyFoodHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // Inflate the item layout for the RecyclerView
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_view_daily_food, viewGroup, false);
        return new DailyFoodHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyFoodHolder holder, int position) {
        // Bind the data to the views in each item of the RecyclerView
        Food food = getItem(position);
        holder.binding.itemName.setText(food.getName());
        holder.binding.gramsLBL.setText(String.valueOf(food.getGram()));
        holder.binding.energyLBL.setText(String.format("%.1f", food.getEnergy()));
        holder.binding.proteinLBL.setText(String.format("%.1f", food.getProtein()));
        holder.binding.carbsLBL.setText(String.format("%.1f", food.getCarb()));
        holder.binding.fatLBL.setText(String.format("%.1f", food.getFat()));
    }

    @Override
    public int getItemCount() {
        return foodList == null ? 0 : foodList.size();
    }

    private Food getItem(int position) {
        return foodList.get(position);
    }

    public ArrayList<Food> getFoodList() {
        return this.foodList;
    }

    public interface OnFoodClickListener {
        void onClick(View view, Food food, int position);

        void onLongClick(View view, Food food, int position);
    }

    public class DailyFoodHolder extends RecyclerView.ViewHolder {

        private RecycleViewDailyFoodBinding binding;

        public DailyFoodHolder(@NonNull View itemView) {
            super(itemView);
            binding = RecycleViewDailyFoodBinding.bind(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClick(v, getItem(getAdapterPosition()), getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onClickListener.onLongClick(v, getItem(getAdapterPosition()), getAdapterPosition());
                    return false;
                }
            });
        }
    }
}
