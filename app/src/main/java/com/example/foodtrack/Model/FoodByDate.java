package com.example.foodtrack.Model;

import java.util.ArrayList;

public class FoodByDate {
    ArrayList<Food> foodByDate;
    String date ;

    public FoodByDate() {
    }

    public ArrayList<Food> getFoodByDate() {
        return foodByDate;
    }

    public void setFoodByDate(ArrayList<Food> foodByDate) {
        this.foodByDate = foodByDate;
    }

    public void addFood(Food food){
        this.foodByDate.add(food);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
