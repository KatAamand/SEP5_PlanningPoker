package Networking;

import DataTypes.*;
import DataTypes.UserRoles.UserRole;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ServerConnection_RMI extends Remote {
    void registerClient(ClientConnection_RMI client) throws RemoteException;
    void registerClientToGame(ClientConnection_RMI client, int planningPokerId) throws RemoteException;
    void unRegisterClient(ClientConnection_RMI client) throws RemoteException;
    void unRegisterClientFromGame(ClientConnection_RMI client, int planningPokerId) throws RemoteException;
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
     * Retrieves the list of tasks for a specified Planning Poker game.
     * @param planningPokerId the ID of the Planning Poker game.
     * @return an ArrayList of tasks associated with the specified Planning Poker game.
     * @throws RemoteException if there is a communication error during the remote method call.
     */
    ArrayList<Task> getTaskList(int planningPokerId) throws RemoteException;

    void addTask(Task task, int planningPokerId) throws RemoteException;
    boolean removeTask(Task task, int planningPokerId) throws RemoteException;
    boolean editTask(Task oldTask, Task newTask, int planningPokerId) throws RemoteException;
    void broadcastSkipTasks(ArrayList<Task> skippedTasksList, int planningPokerId) throws RemoteException;
    boolean validatePlanningPokerID(int planningPokerID) throws RemoteException;
    PlanningPoker createPlanningPoker(ClientConnection_RMI client) throws RemoteException;
    PlanningPoker loadPlanningPokerGame(int planningPokerId, ClientConnection_RMI client, boolean addClientToGame) throws RemoteException;
    void addConnectedUserToSession(User user) throws RemoteException;

    /**
     * Retrieves the list of efforts.
     * @return an ArrayList of Effort objects.
     * @throws RemoteException if there is a communication error during the remote method call.
     */
    ArrayList<Effort> getEffortList() throws RemoteException;

    void removeUserFromSession(User user) throws RemoteException;

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


    User setRoleInPlanningPokerGame(UserRole roleToApply, User userToReceiveRole, int planningPokerId) throws RemoteException;

    /**
     * Requests to show all cards in the specified Planning Poker game.
     * @param planningPokerId the ID of the Planning Poker game.
     * @throws RemoteException if there is a communication error during the remote method call.
     */
    void requestShowCards(int planningPokerId) throws RemoteException;
    void broadcastUserInSession(User user) throws RemoteException;

    void setProductOwner(User user) throws RemoteException;

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
