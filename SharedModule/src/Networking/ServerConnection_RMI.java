package Networking;

import DataTypes.User;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerConnection_RMI extends Remote {
    void registerClient(ClientConnection_RMI client) throws RemoteException;
    void unRegisterClient(ClientConnection_RMI client) throws RemoteException;

    void sendMessage(String message, User sender) throws RemoteException;

    void validateUser(String username, String password) throws RemoteException;

    void createUser(String username, String password) throws RemoteException;

    void registerClientListener(ClientConnection_RMI client) throws RemoteException;

    void validatePlanningPokerID(String planningPokerID) throws RemoteException;
    void createPlanningPoker() throws RemoteException;
}
