package Application;

import DataTypes.User;

public class Session {
    private static User currentUser;
    private static int connectedGameId; //Holds the connectionId for the game the user is currently connected to. Is "invalid", if user has not joined a game.

    public Session() {
        connectedGameId = 0;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        Session.currentUser = currentUser;
    }

    public static void setConnectedGameId(int planningPokerId) {
        connectedGameId = planningPokerId;
    }

    public static int getConnectedGameId() {
        return connectedGameId;
    }
}
