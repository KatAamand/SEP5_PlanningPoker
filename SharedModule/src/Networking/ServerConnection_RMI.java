package Networking;

import DataTypes.*;
import DataTypes.UserRoles.UserRole;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


/**
 * The ServerConnection_RMI interface defines the key methods relating to interfacing the server with the local clients.
 * It is further responsible to defining the key methods which delegate their executing to specialized server-side models and interfaces for the final execution.
 * These methods enable the server and client to communicate with each other, and can be called from either the server or the client.
 * It extends the Remote interface and is thus prepared for network use with RMI.
 */
public interface ServerConnection_RMI extends Remote {


    // TODO: Source code comment/document
    void registerClient(ClientConnection_RMI client) throws RemoteException;


    /** Adds the specified ClientConnection_RMI to the implementing class's List of clients connected to the Planning Poker game with the specified planningPokerId.<br>
     * It is mainly called from the client towards the server.
     * @param client The ClientConnection_RMI to add to the specified Planning Poker games' client list.
     * @param planningPokerId The unique Planning Poker game id for the Planning Poker game that the ClientConnection_RMI should be connected to.
     * @throws RemoteException If an error relating to the RMI connection is encountered.
     */
    void registerClientToGame(ClientConnection_RMI client, int planningPokerId) throws RemoteException;


    // TODO: Source code comment/document
    void unRegisterClient(ClientConnection_RMI client) throws RemoteException;


    /** Removes the specified ClientConnection_RMI from the implementing class's List of clients connected to the Planning Poker game with the specified planningPokerId.<br>
     * It is mainly called from the client towards the server.
     * @param client The ClientConnection_RMI to remove from the servers client list.
     * @param planningPokerId The unique Planning Poker game id for the Planning Poker game that the ClientConnection_RMI should already be connected to.
     * @throws RemoteException If an error relating to the RMI connection is encountered.
     */
    void unRegisterClientFromGame(ClientConnection_RMI client, int planningPokerId) throws RemoteException;


    /** Lets the client send a message via the chat system
     * @param message the message being sent
     * @param sender the user that sends the message*/
    void sendMessage(Message message, User sender) throws RemoteException;


    /**
     * Validates the credentials of a user.
     * @param username the username of the user.
     * @param password the password of the user.
     * @param client the client connection making the request.
     * @throws RemoteException if there is a communication error during the remote method call.
     */
    void validateUser(String username, String password, ClientConnection_RMI client) throws RemoteException;


    /**
     * Creates a new user with the given credentials.
     * @param username the username for the new user.
     * @param password the password for the new user.
     * @param client the client connection making the request.
     * @throws RemoteException if there is a communication error during the remote method call.
     */
    void createUser(String username, String password, ClientConnection_RMI client) throws RemoteException;


    /** NOT IMPLEMENTED FULLY<br>
     * Method is intended to log out each user from the server, whenever the user closes their local application (disconnects).<br>
     * Currently, this implementation receives the request from a connected client and then passes this on to a specialized interface responsible for the Login functionality.
     * The final part inside the specialized interface is not fully implemented yet.
     * @param username a String containing the name of the user to log out.
     * @param password a String containing the password for the user whom to log out.
     * @throws RemoteException If an error relating to the RMI connection is encountered.
     */
    void logoutUser(String username, String password) throws RemoteException;


    /**
     * Registers a listener on the client to receive updates from the server.
     * @param client the client connection to register the listener for.
     * @throws RemoteException if there is a communication error during the remote method call.
     */
    void registerClientListener(ClientConnection_RMI client) throws RemoteException;


    /**
     * Unregisters a listener on the client to stop receiving updates from the server.
     * @param client the client connection to unregister the listener for.
     * @throws RemoteException if there is a communication error during the remote method call.
     */
    void unRegisterClientListener(ClientConnection_RMI client) throws RemoteException;


    /**
     * Returns a List of all the Task objects in the Planning Poker game with the specified game id, if such a game exists.<br>
     * This implementation passes the request on to the TaskServerModel interface, for the final execution.
     * @param planningPokerId The Game ID for the Planning Poker game, from which to load all current Tasks.
     * @return An ArrayList containing all the current Tasks associated with the Planning Poker game with the specified game id.
     * @throws RemoteException If an error relating to the RMI connection is encountered.
     */
    ArrayList<Task> getTaskList(int planningPokerId) throws RemoteException;


    /**
     * Adds the specified Task to the Planning Poker game with the specified game id, if such a game exists.<br>
     * This implementation passes the request on to the TaskServerModel interface, for the final execution.
     * @param task The Task that should be added to the specified Planning Poker game.
     * @param planningPokerId The Game ID for the Planning Poker game, to which the specified Task should be added.
     * @throws RemoteException If an error relating to the RMI connection is encountered.
     */
    void addTask(Task task, int planningPokerId) throws RemoteException;


    /**
     * Removes the specified Task from the Planning Poker game with the specified game id, if such a game exists.<br>
     * This implementation passes the request on to the TaskServerModel interface, for the final execution.
     * @param task The Task that should be removed from the specified Planning Poker game.
     * @param planningPokerId The Game ID for the Planning Poker game, from which the specified Task should be removed.
     * @return True, if the Task was successfully removed. Otherwise, returns False.
     * @throws RemoteException If an error relating to the RMI connection is encountered.
     */
    boolean removeTask(Task task, int planningPokerId) throws RemoteException;


    /**
     * Edits the specified Task in the Planning Poker game with the specified game id, if such a game exists.<br>
     * This implementation passes the request on to the TaskServerModel interface, for the final execution.<br>
     * This method requires that both the un-modified Task and the modified Task are specified.
     * The method finds the un-modified task in the specified Planning Poker games list of tasks, and replaces this Task with the modified one.
     * This approach allows for the Task ordering / priority to remain unchanged, so modified tasks are not continuously appended to the end of the task list.
     * @param oldTask The un-modified task, which should be found and replaced.
     * @param newTask The modified task, which should replace the un-modified one.
     * @param planningPokerId The Game ID for the Planning Poker game, from which the specified Task should be edited.
     * @return True, if the specified Task was successfully edited. False, if it failed, or the specified game does not exist.
     * @throws RemoteException If an error relating to the RMI connection is encountered.
     */
    boolean editTask(Task oldTask, Task newTask, int planningPokerId) throws RemoteException;


    /** Broadcasts the list of skipped tasks to all the given clients, whom connection can still be established with.<br>
     * This implementation passes the request on to the GameServerModel interface, for the final execution.<br>
     * @param skippedTasksList An ArrayList containing a list of tasks, that each connected client should skip.
     * @param planningPokerId The Game ID for the Planning Poker game, inside which to find connected users and broadcast the skipped task list to.
     * @throws RemoteException If an error relating to the RMI connection is encountered.
     */
    void broadcastSkipTasks(ArrayList<Task> skippedTasksList, int planningPokerId) throws RemoteException;


    // Validates the provided Planning Poker ID by communicating with the server.
    // Verifies the integrity of all users connected to the game before validation.
    // @param planningPokerID the ID of the Planning Poker game to be validated.
    // @return true if the ID is validated, false otherwise.
    // @throws RemoteException if there is an issue with the remote method call.
    boolean validatePlanningPokerID(int planningPokerID) throws RemoteException;


    /** Lets a client create a game of planning poker via the server*/
    PlanningPoker createPlanningPoker(ClientConnection_RMI client) throws RemoteException;


    /**
     * Attempts to load an existing Planning Poker game by validating the specified planningPokerId in the MainServerModel interface.<br>
     * If validation passes, the method then checks if the specified ClientConnection_RMI is assigned to the validated Planning Poker game.
     * If not, this method ensures the Client is properly assigned to this game, if the specified boolean 'addClientToGame' is true.<br>
     * @param planningPokerId The unique Planning Poker game id for the Planning Poker game that should be loaded.
     * @param client A reference to the local Client from which execution of this method was initiated from.
     * @param addClientToGame Set to TRUE, if the specified ClientConnection_RMI should be assigned to this Planning Poker game, if not already assigned. Set to FALSE, if not.
     * @throws RemoteException Is thrown if RMI related connection errors occur.
     * */
    PlanningPoker loadPlanningPokerGame(int planningPokerId, ClientConnection_RMI client, boolean addClientToGame) throws RemoteException;


    /** Adds user to its planning poker session's list of connected user, then broadcasts userlist to all users in that session */
    void addConnectedUserToSession(User user) throws RemoteException;


    /**
     * Retrieves the list of efforts.
     * @return an ArrayList of Effort objects.
     * @throws RemoteException if there is a communication error during the remote method call.
     */
    ArrayList<Effort> getEffortList() throws RemoteException;


    /** Removes the user from the session, and broadcasts that update to the rest of the users in that session*/
    void removeUserFromSession(User user) throws RemoteException;


    /** Removes the specified ClientConnection_RMI from the implementing class's List of clients connected to the Planning Poker game with the specified planningPokerId.<br>
     * This implementation passes the request on to the GameServerModel interface, for the final execution.<br>
     * It is mainly called from the client towards the server.
     * @param localClient The ClientConnection_RMI to remove from the specified Planning Poker games' client list.
     * @param user The User to remove, if found in the games' client list.
     * @param planningPokerId The unique Planning Poker game id for the Planning Poker game that the ClientConnection_RMI should be connected to.
     * @throws RemoteException If an error relating to the RMI connection is encountered.
     */
    void removeUserFromGame(ClientConnection_RMI localClient, User user, int planningPokerId) throws RemoteException;


    /**
     * Places a card for the specified user in the specified Planning Poker game.
     * @param userCardData the data of the card being placed.
     * @param planningPokerId the ID of the Planning Poker game.
     * @throws RemoteException if there is a communication error during the remote method call.
     */
    void placeCard(UserCardData userCardData, int planningPokerId) throws RemoteException;


    /**
     * Requests to clear all placed cards in the specified Planning Poker game.
     * @param planningPokerId the ID of the Planning Poker game.
     * @throws RemoteException if there is a communication error during the remote method call.
     */
    void requestClearPlacedCards(int planningPokerId) throws RemoteException;


    /**
     * Attempts to apply the specified UserRole to the specified User, who is connected to the Planning Poker Game with the specified id.<br>
     * This implementation passes the request on to the MainServerModel interface, for the final execution.<br>
     * @param roleToApply The UserRole that should be applied to the specified user.
     * @param userToReceiveRole The User, for which the specified UserRole should be applied.
     * @param planningPokerId The Game ID for the Planning Poker game, to which the specified User should be connected.
     * @return A NON-NULL User object, if such a User was in the Planning Poker game as specified by the Planning Poker id. Otherwise returns <code>NULL</code>
     * @throws RemoteException If an error relating to the RMI connection is encountered.
     */
    User setRoleInPlanningPokerGame(UserRole roleToApply, User userToReceiveRole, int planningPokerId) throws RemoteException;


    /**
     * Requests to show all cards in the specified Planning Poker game.
     * @param planningPokerId the ID of the Planning Poker game.
     * @throws RemoteException if there is a communication error during the remote method call.
     */
    void requestShowCards(int planningPokerId) throws RemoteException;


    /** Broadcasts userlist of senders planningpoker session to all users in that session*/
    void broadcastUserInSession(User user) throws RemoteException;


    /** Sets the product owner for the users planning poker session
     * @param user the user that is intended to become product owner*/
    void setProductOwner(User user) throws RemoteException;


    /**
     * Requests the list of all users.
     * @return an ArrayList<User> containing all users.
     */
    ArrayList<User> requestUserList() throws RemoteException;


    /**
     * Requests to start the specified Planning Poker game.
     * @param planningPokerId the ID of the Planning Poker game.
     * @throws RemoteException if there is a communication error during the remote method call.
     */
    void requestStartGame(int planningPokerId) throws RemoteException;


    /**
     * Requests the recommended effort for the specified Planning Poker game.
     * @param planningPokerId the ID of the Planning Poker game.
     * @throws RemoteException if there is a communication error during the remote method call.
     */
    void requestRecommendedEffort(int planningPokerId) throws RemoteException;
}
