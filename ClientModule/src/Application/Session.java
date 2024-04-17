package Application;

import DataTypes.User;

public class Session {
    private User currentUser;

    public Session() {
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
