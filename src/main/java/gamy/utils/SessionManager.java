package gamy.utils;

import gamy.models.User;

public class SessionManager {
    private static User currentUser;

    public static void setCurrentUser(User _user) {
        currentUser = _user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void logout() {
        currentUser = null;
    }
}