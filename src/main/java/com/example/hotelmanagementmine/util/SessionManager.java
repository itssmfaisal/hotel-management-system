package com.example.hotelmanagementmine.util;

import com.example.hotelmanagementmine.model.User;

public class SessionManager {
    private static User currentUser;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static void clearSession() {
        currentUser = null;
    }

    public static boolean isAdmin() {
        return currentUser != null && "ADMIN".equals(currentUser.getRole());
    }

    public static boolean isCustomer() {
        return currentUser != null && "CUSTOMER".equals(currentUser.getRole());
    }
}
