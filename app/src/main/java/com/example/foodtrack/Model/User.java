package com.example.foodtrack.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class User {

    String firstName, lastName, phoneNumber;
    int calorieBudget;
    Map<String, ArrayList<Food>> userFoodHistory;

    public User() {
        userFoodHistory = new HashMap<>();
    }

    // Getters and setters for the user properties

    public String getFirstName() {
        return firstName;
    }

    public User setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public User setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public User setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public int getCalorieBudget() {
        return calorieBudget;
    }

    public User setCalorieBudget(int calorieBudget) {
        this.calorieBudget = calorieBudget;
        return this;
    }

    public Map<String, ArrayList<Food>> getUserFoodHistory() {
        return userFoodHistory;
    }

    public User setUserFoodHistory(Map<String, ArrayList<Food>> userFoodHistory) {
        this.userFoodHistory = userFoodHistory;
        return this;
    }

    // Overridden methods

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(phoneNumber, user.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, phoneNumber);
    }

    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}

