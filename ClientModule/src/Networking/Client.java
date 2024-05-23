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
     * @throws RuntimeException Is thrown if any error occurs during the user creation process.
     */
    void createUser(String username, String password) throws RuntimeException;


    /**
     * Logs the user out of the server.
     * @param username A String containing the username who should be logged out.
     * @param password A String containing the password of the user whom should be logged out.
     * @throws RuntimeException Is thrown if any error occurs during the logout process.
     * */
    void logoutUser(String username, String password) throws RuntimeException;


    // TODO: Source code comment/document
    void sendMessage(Message message, User sender);


    void receiveMessage(Message message); //TODO Method is never used. Should be removed.


    /**
     * Validates the provided Planning Poker game ID with the server.
     * @param planningPokerID the ID of the Planning Poker game to validate.
     * @return true if the ID is valid, false otherwise.
     */
    boolean validatePlanningPokerID(int planningPokerID);


    /** Attempts to create a new planning poker game by calling the server and evaluating the server's response.
     * This method also broadcasts a propertyChangeEvent 'planningPokerCreatedSuccess',
     * so connected listeners can act on the newly created game.
     * @return <code>NULL</code> if the server failed to create a Planning Poker game. Otherwise, returns the Planning Poker object that was created.
     * @throws RuntimeException Is thrown if any error occurs during creation of the planning poker game.
     * */
    PlanningPoker createPlanningPoker() throws RuntimeException;


    /** Attempts to load an existing planning poker game by calling the server and evaluating the server's response.
     * This method also attaches the loaded NON-NULL planning poker game to the local User inside the local Session Object,
     * for an immediate update of the locally active Planning Poker Game object.
     * @param planningPokerId The Game ID for the Planning Poker game, that the Local User wishes to load.
     * @return <code>NULL</code> if the server failed to create a Planning Poker game. Otherwise, returns the Planning Poker object that was created.
     * @throws RuntimeException Is thrown if any error occurs during loading of the planning poker game.
     * */
    PlanningPoker loadPlanningPoker(int planningPokerId) throws RuntimeException;


    /** Attempts to load the list of tasks associated with the given planning poker game id.
     * @param planningPokerId The Game ID for the Planning Poker game, that the Local User wishes to load the list of tasks from.
     * @throws RuntimeException Is thrown if any error occurs during loading of the task list.
     * */
    void loadTaskList(int planningPokerId) throws RuntimeException;


    /** Attempts to add a task to the Planning Poker game on the server, with the given planningPokerId.
     * @param task The task, that should be added.
     * @param planningPokerId The unique Planning Poker game id the task should be assigned to.
     * @throws RuntimeException Is thrown if any error occurs while trying to add the task to this game.
     * */
    void addTask(Task task, int planningPokerId) throws RuntimeException;


    /** Attempts to remove a task from the Planning Poker game on the server, with the given planningPokerId.
     * @param task The task, that should be removed.
     * @param planningPokerId The unique Planning Poker game id the task should be removed from.
     * @return True, if the task was successfully removed from the servers task list, otherwise false if removal was not successful.
     * @throws RuntimeException Is thrown if any error occurs while trying to remove from the task to this game.
     * */
    boolean removeTask(Task task, int planningPokerId) throws RuntimeException;


    /** Attempts to edit a task in the Planning Poker game on the server, with the given planningPokerId.
     * This method requires the unmodified Task object in order to locate the correct task in the servers
     * Planning Poker objects list of tasks, before replacing with the modified Task object. This procedure
     * ensures that the priority and ordering og each task in the task list remains consistent
     * (i.e. modified tasks aren't moved to the bottom of the list).
     * @param oldTask The task, as it was before any modifications were applied.
     * @param newTask The task, that should replace the old task.
     * @param planningPokerId The unique Planning Poker game id the task should be edited in.
     * @return True, if the task was successfully edited in the servers task list, otherwise false if edit was not successful.
     * @throws RuntimeException Is thrown if any error occurs while trying to edit the task in this game.
     * */
    boolean editTask(Task oldTask, Task newTask, int planningPokerId) throws RuntimeException;


    /** Causes the server to broadcast the attached ArrayList<Task> to all clients who are currently connected to the given Planning Poker game id.
     * This method ensures that tasks, that are skipped locally, are also communicated to all connected clients inside the same game.
     * @param skippedTasksList An ArrayList containing all the tasks in the currently active game, that have been skipped.
     * @param planningPokerId The unique Planning Poker game id the task should be edited in.
     * @throws RuntimeException Is thrown if any error occurs while trying to skip the task in this game.
     * */
    void skipTasks(ArrayList<Task> skippedTasksList, int planningPokerId) throws RuntimeException;


    // TODO: Source code comment/document
    void sendUser();


    void removeUserFromSession(); //TODO Method is never used. Should be removed.


    /** Causes the server to remove the Local user from any currently active games, on the server.
     * Method is primarily used when Local Users leave currently active games, or close their application.
     * @param planningPokerId The unique Planning Poker game id the task should be edited in.
     * @throws RuntimeException Is thrown if any error occurs while trying to remove the Local user from this game.
     * */
    void removeUserFromGame(int planningPokerId) throws RuntimeException;


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
    void clearPlacedCards(); //TODO: Method is never used. Should be removed.


    /**
     * Requests the server to show all placed cards in the current Planning Poker game.
     */
    void requestShowCards();


    void setProductOwner(User user); //TODO: Method is never used. Should be removed.


    /** Attempts to apply the given UserRole to the given User in the given Planning Poker, by forwarding the request to the server.
     * Before forwarding to the server, this method will check if the given User already has the given UserRole. If so, this method will do nothing.
     * This method receives the servers answer, and updates the local User in the local Session object, if the role was applied successfully.
     * @param userRole The UserRole that should be applied to the given User.
     * @param planningPokerId The unique Planning Poker game id for the Planning Poker game inside which the given User should be found and given the specified UserRole.
     * @param user The User that should have the given UserRole applied.
     * @throws RuntimeException Is thrown if any error occurs while trying to apply the UserRole to the User in the given game.
     * */
    void setRoleInGame(UserRole userRole, int planningPokerId, User user);


    // TODO: Source code comment/document
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
    void startTimer(); //TODO: Method is never used. Should be removed.
}
