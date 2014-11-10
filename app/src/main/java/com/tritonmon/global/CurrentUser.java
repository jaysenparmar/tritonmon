package com.tritonmon.global;

import com.tritonmon.model.User;

public class CurrentUser {
    private static User user = null;

    public CurrentUser() {

    }

    public static void setUser(User u) {
        user = u;
    }

    public static User getUser() {
        return user;
    }

    public static boolean exists() {
        return user != null;
    }

    public static void logout() {
        user = null;
    }
}
