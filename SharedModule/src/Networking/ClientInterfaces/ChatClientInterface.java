package Networking.ClientInterfaces;

import DataTypes.Message;
import DataTypes.User;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


/**
 * The ChatClientInterface interface defines the key methods relating to interfacing the client-side application with the server,
 * as they relate to the chat related network interactions.
 * These methods enable the server and client to communicate with each other, and can be called from either the server or the client.
 * It extends the Remote interface and is thus prepared for network use with RMI.
 */
public interface ChatClientInterface extends Remote {

    // TODO: Source code comment/document
    void sendMessage(Message message, User sender) throws RemoteException; //TODO: Method is never used. Should be removed from this interface.


    // TODO: Source code comment/document
    void receiveMessage(Message message)  throws RemoteException;


    // TODO: Source code comment/document
    User getCurrentUser() throws RemoteException;


    // TODO: Source code comment/document
    void receiveUser(ArrayList<User> users) throws RemoteException;
}
