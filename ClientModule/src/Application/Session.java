package Application;

import DataTypes.User;

public class Session {
    private static User currentUser;

    public Session() {
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        Session.currentUser = currentUser;
    }
}
