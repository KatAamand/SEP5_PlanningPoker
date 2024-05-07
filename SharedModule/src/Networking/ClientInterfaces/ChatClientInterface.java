package Networking.ClientInterfaces;

import DataTypes.Message;
import DataTypes.User;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatClientInterface extends Remote {
    void sendMessage(Message message, User sender) throws RemoteException;
    void receiveMessage(Message message)  throws RemoteException;
    User getCurrentUser() throws RemoteException;
}
