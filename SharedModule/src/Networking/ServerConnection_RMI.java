package Networking;

import DataTypes.*;
import DataTypes.UserRoles.Role;
import DataTypes.UserRoles.UserRole;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ServerConnection_RMI extends Remote {
    void registerClient(ClientConnection_RMI client) throws RemoteException;
    void registerClientToGame(ClientConnection_RMI client, String gameId) throws RemoteException;
    void unRegisterClient(ClientConnection_RMI client) throws RemoteException;
    void unRegisterClientFromGame(ClientConnection_RMI client, String gameId) throws RemoteException;
    void sendMessage(Message message, User sender) throws RemoteException;
    void validateUser(String username, String password, ClientConnection_RMI client) throws RemoteException;
    void createUser(String username, String password, ClientConnection_RMI client) throws RemoteException;
    void logoutUser(String username, String password) throws RemoteException;
    void registerClientListener(ClientConnection_RMI client) throws RemoteException;
    void unRegisterClientListener(ClientConnection_RMI client) throws RemoteException;
    ArrayList<Task> getTaskList(String gameId) throws RemoteException;
    void addTask(Task task, String gameId) throws RemoteException;
    boolean removeTask(Task task, String gameId) throws RemoteException;
    boolean editTask(Task oldTask, Task newTask, String gameId) throws RemoteException;
    void broadcastSkipTasks(ArrayList<Task> skippedTasksList, String gameId) throws RemoteException;
    boolean validatePlanningPokerID(String planningPokerID) throws RemoteException;
    PlanningPoker createPlanningPoker(ClientConnection_RMI client) throws RemoteException;
    PlanningPoker loadPlanningPokerGame(String planningPokerId, ClientConnection_RMI client) throws RemoteException;
    void addConnectedUserToSession(User user) throws RemoteException;
    ArrayList<Effort> getEffortList() throws RemoteException;
    void removeUserFromSession(User user) throws RemoteException;
    void placeCard(UserCardData userCardData) throws RemoteException;
    void requestClearPlacedCards() throws RemoteException;
    User setRoleInPlanningPokerGame(UserRole roleToApply, User userToReceiveRole, String gameId) throws RemoteException;
}
