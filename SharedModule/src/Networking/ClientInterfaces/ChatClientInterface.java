package Networking.ClientInterfaces;

import DataTypes.User;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatClientInterface extends Remote {
    void sendMessage(String message, User sender) throws RemoteException;
    void receiveMessage(String message)  throws RemoteException;
    User getCurrentUser() throws RemoteException;
}
