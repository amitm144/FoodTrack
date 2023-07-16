package com.example.foodtrack.Model;

import java.text.Collator;
import java.util.Comparator;
import java.util.Objects;


public class Food {

    private String name, from;
    private double gram, energy, protein, carb, fat;

    public Food() {
        this.gram = 100;
    }

    // Getters and setters for the food properties

    public String getName() {
        return name;
    }

    public Food setName(String name) {
        this.name = name;
        return this;
    }

    public double getGram() {
        return gram;
    }

    public Food setGram(double newGram) {
        updateNutrition(gram, newGram);
        this.gram = newGram;
        return this;
    }

    private void updateNutrition(double oldGram, double newGram) {
        double g = (newGram / oldGram);
        setEnergy(getEnergy() * g);
        setProtein(getProtein() * g);
        setCarb(getCarb() * g);
        setFat(getFat() * g);
    }

    public double getEnergy() {
        return energy;
    }

    public Food setEnergy(double energy) {
        this.energy = energy;
        return this;
    }

    public double getProtein() {
        return protein;
    }

    public Food setProtein(double protein) {
        this.protein = protein;
        return this;
    }

    public double getCarb() {
        return carb;
    }

    public Food setCarb(double carb) {
        this.carb = carb;
        return this;
    }

    public double getFat() {
        return fat;
    }

    public Food setFat(double fat) {
        this.fat = fat;
        return this;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    // Overridden methods

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Food food = (Food) o;
        return Objects.equals(name, food.name) && Objects.equals(from, food.from);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, from);
    }

    @Override
    public String toString() {
        return "name='" + name + '\'' +
                ", gram=" + gram +
                ", calories=" + energy +
                ", protein=" + protein +
                ", carb=" + carb +
                ", fat=" + fat;
    }

    // Comparator for sorting food items by name with a target word filter

    public static Comparator<Food> nameComparator(String targetWord) {
        return new Comparator<Food>() {
            private Collator collator = Collator.getInstance();

            @Override
            public int compare(Food food1, Food food2) {
                String food1Name = food1.getName();
                String food2Name = food2.getName();

                boolean food1StartsWithFilter = food1Name.toLowerCase().startsWith(targetWord.toLowerCase());
                boolean food2StartsWithFilter = food2Name.toLowerCase().startsWith(targetWord.toLowerCase());

                if (food1StartsWithFilter && !food2StartsWithFilter) {
                    return -1;
                } else if (!food1StartsWithFilter && food2StartsWithFilter) {
                    return 1;
                } else {
                    int nameComparison = collator.compare(food1Name, food2Name);
                    if (nameComparison != 0) {
                        return nameComparison;
                    } else {
                        return food1Name.length() - food2Name.length();
                    }
                }
            }
        };
    }
}
