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


    /** Lets the server send a message to the clients chat*/
    void receiveMessage(Message message)  throws RemoteException;


    /** Lets the server get the User object assigned to the client*/
    User getCurrentUser() throws RemoteException;


    /** Lets the server send a list of users to the client it's called on
     * Used for the list of connected users in a game session*/
    void receiveUser(ArrayList<User> users) throws RemoteException;
}
