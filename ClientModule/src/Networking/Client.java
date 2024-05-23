package Networking;

import DataTypes.*;
import DataTypes.UserRoles.UserRole;
import Util.PropertyChangeSubject;
import java.util.ArrayList;

/**
 * Client defines the interface which is responsible for all local client network related tasks. These methods provide the means of communicating with the server.
 * It extends the PropertyChangeSubject interface and is thus prepared for use with Observer Pattern principles.
 */
public interface Client extends PropertyChangeSubject {

    /**
     * Sends a request to the server to validate the user's credentials.
     * @param username the username of the user attempting to log in.
     * @param password the password of the user attempting to log in.
     */
    void validateUser(String username, String password);

    /**
     * Sends a request to the server to create a new user with the provided username and password.
     * @param username the desired username for the new user.
     * @param password the desired password for the new user.
     */
    void createUser(String username, String password);

    void logoutUser(String username, String password);
    void disconnectLocalClient();

    void sendMessage(Message message, User sender);
    void receiveMessage(Message message);

    /**
     * Validates the provided Planning Poker game ID with the server.
     * @param planningPokerID the ID of the Planning Poker game to validate.
     * @return true if the ID is valid, false otherwise.
     */
    boolean validatePlanningPokerID(int planningPokerID);
    PlanningPoker createPlanningPoker();
    PlanningPoker loadPlanningPoker(int planningPokerId);
    void loadTaskList(int planningPokerId);
    void addTask(Task task, int planningPokerId);
    boolean removeTask(Task task, int planningPokerId);
    boolean editTask(Task oldTask, Task newTask, int planningPokerId);
    void skipTasks(ArrayList<Task> skippedTasksList, int planningPokerId);
    void sendUser();
    void removeUserFromSession();
    void removeUserFromGame(int planningPokerId);

    /**
     * Retrieves the list of efforts from the server.
     * @return an ArrayList<Effort> containing the effort data.
     */
    ArrayList<Effort> getEffortList();

    /**
     * Sends a request to the server to place a card for the current user in the Planning Poker game.
     * @param userCardData the data of the card being placed by the user.
     */
    void placeCard(UserCardData userCardData);

    /**
     * Requests the server to clear all placed cards in the current Planning Poker game.
     */
    void requestClearPlacedCards();

    /**
     * Clears all placed cards locally.
     */
    void clearPlacedCards();

    /**
     * Requests the server to show all placed cards in the current Planning Poker game.
     */
    void requestShowCards();

    void setProductOwner(User user);
    void setRoleInGame(UserRole userRole, int gameId, User user);
    void requestUserList();

    /**
     * Requests the server to start the Planning Poker game for the specified game ID.
     * @param connectedGameId the ID of the Planning Poker game to start.
     */
    void requestStartGame(int connectedGameId);

    /**
     * Requests the recommended effort estimation from the server for the current Planning Poker game.
     */
    void requestRecommendedEffort();

    /**
     * Starts the timer for the Planning Poker game.
     */
    void startTimer();
}
