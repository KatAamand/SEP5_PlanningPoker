package Networking;

import DataTypes.Task;
import DataTypes.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ServerConnection_RMI extends Remote {
    void registerClient(ClientConnection_RMI client) throws RemoteException;
    void unRegisterClient(ClientConnection_RMI client) throws RemoteException;

    void sendMessage(String message, User sender) throws RemoteException;

    void validateUser(String username, String password, ClientConnection_RMI client) throws RemoteException;

    void createUser(String username, String password, ClientConnection_RMI client) throws RemoteException;

    void registerClientListener(ClientConnection_RMI client) throws RemoteException;
    void unRegisterClientListener(ClientConnection_RMI client) throws RemoteException;
    ArrayList<Task> getTaskList() throws RemoteException;

    void addTask(Task task) throws RemoteException;

    void validatePlanningPokerID(String planningPokerID) throws RemoteException;
    void createPlanningPoker() throws RemoteException;
}
