package Networking;

import DataTypes.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ServerConnection_RMI extends Remote {
    void registerClient(ClientConnection_RMI client) throws RemoteException;
    void registerClientToGame(ClientConnection_RMI client, int planningPokerId) throws RemoteException;
    void unRegisterClient(ClientConnection_RMI client) throws RemoteException;
    void unRegisterClientFromGame(ClientConnection_RMI client, int planningPokerId) throws RemoteException;
    void sendMessage(Message message, User sender) throws RemoteException;
    void validateUser(String username, String password, ClientConnection_RMI client) throws RemoteException;
    void createUser(String username, String password, ClientConnection_RMI client) throws RemoteException;
    void logoutUser(String username, String password) throws RemoteException;
    void registerClientListener(ClientConnection_RMI client) throws RemoteException;
    void unRegisterClientListener(ClientConnection_RMI client) throws RemoteException;
    ArrayList<Task> getTaskList(int planningPokerId) throws RemoteException;
    void addTask(Task task, int planningPokerId) throws RemoteException;
    boolean removeTask(Task task, int planningPokerId) throws RemoteException;
    boolean editTask(Task oldTask, Task newTask, int planningPokerId) throws RemoteException;
    void broadcastSkipTasks(ArrayList<Task> skippedTasksList, int planningPokerId) throws RemoteException;
    boolean validatePlanningPokerID(int planningPokerID) throws RemoteException;
    PlanningPoker createPlanningPoker(ClientConnection_RMI client) throws RemoteException;
    PlanningPoker loadPlanningPokerGame(int planningPokerId, ClientConnection_RMI client) throws RemoteException;
    void addConnectedUserToSession(User user) throws RemoteException;
    ArrayList<Effort> getEffortList() throws RemoteException;
    void removeUserFromSession(User user) throws RemoteException;
    void placeCard(UserCardData userCardData) throws RemoteException;
    void requestClearPlacedCards() throws RemoteException;
}
