package Networking;

import DataTypes.Message;
import DataTypes.PlanningPoker;
import DataTypes.Task;
import DataTypes.User;

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
    boolean validatePlanningPokerID(String planningPokerID) throws RemoteException;
    PlanningPoker createPlanningPoker(ClientConnection_RMI client) throws RemoteException;
    PlanningPoker loadPlanningPokerGame(String planningPokerId, ClientConnection_RMI client) throws RemoteException;
}
