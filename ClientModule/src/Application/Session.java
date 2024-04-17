package Application;

import DataTypes.User;

public class Session {
    private User currentUser;

    public Session() {
        this.currentUser = new User("test", "123");
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
