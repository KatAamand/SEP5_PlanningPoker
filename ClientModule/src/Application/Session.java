package Application;

import DataTypes.User;

public class Session {
    private static User currentUser;
    private static String connectedGameId; //Holds the connectionId for the game the user is currently connected to. Is "invalid", if user has not joined a game.

    public Session() {
        connectedGameId = "invalid";
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        Session.currentUser = currentUser;
    }

    public static void setConnectedGameId(String gameId) {
        connectedGameId = gameId;
    }

    public static String getConnectedGameId() {
        return connectedGameId;
    }
}
