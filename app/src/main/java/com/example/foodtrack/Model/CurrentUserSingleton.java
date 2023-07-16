package com.example.foodtrack.Model;

public class CurrentUserSingleton {
    private static CurrentUserSingleton instance;
    private User currentUser;

    // Private constructor to prevent direct instantiation
    private CurrentUserSingleton() {
    }

    // Method to retrieve the singleton instance
    public static CurrentUserSingleton getInstance() {
        if (instance == null) {
            synchronized (CurrentUserSingleton.class) {
                if (instance == null) {
                    instance = new CurrentUserSingleton();
                }
            }
        }
        return instance;
    }

    // Getter method to retrieve the current user object
    public User getCurrentUser() {
        return currentUser;
    }

    // Setter method to set the current user object
    public void setCurrentUser(User user) {
        currentUser = user;
    }
}
