package Networking.ClientInterfaces;

import DataTypes.User;
import java.beans.PropertyChangeListener;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The LoginClientInterface interface defines all the key methods relating to interfacing the client-side application with the server,
 * as they relate to Log in, Log out, user validation, user creation and related interactions.
 * These methods enable the server and client to communicate with each other, and can be called from either the server or the client.
 * It extends the Remote interface and is thus prepared for network use with RMI.
 */
public interface LoginClientInterface extends Remote {


    // TODO: Source code comment/document
    void validateUser(String username, String password) throws RemoteException;


    // TODO: Source code comment/document
    void createUser(String username, String password) throws RemoteException;


    // TODO: Source code comment/document
    void userValidationFailed(String errorMessage) throws RemoteException;


    // TODO: Source code comment/document
    void userCreatedSuccessfully() throws RemoteException;


    // TODO: Source code comment/document
    void updateUser(User user) throws RemoteException;


    /**<p>Assigns a listener to this subject<br></p>
     * @param listener A PropertyChangeListener, i.e. some code or methods that should be executed when a PropertyChangeEvent is fired.
     * @throws RemoteException If an error relating to the RMI connection is encountered.
     */
    void addPropertyChangeListener(PropertyChangeListener listener) throws RemoteException;


    /**<p>Assigns a listener to this subject<br></p>
     * @param name A String reference to the event name a specific PropertyChangeEvent.
     * @param listener A PropertyChangeListener, i.e. some code or methods that should be executed when a named specific PropertyChangeEvent is fired.
     * @throws RemoteException If an error relating to the RMI connection is encountered.
     */
    void addPropertyChangeListener(String name, PropertyChangeListener listener) throws RemoteException;


    /**<p>Removes a listener from this subject<br></p>
     * @param listener A PropertyChangeListener, i.e. some code or methods that should be executed when a named specific PropertyChangeEvent is fired.
     * @throws RemoteException If an error relating to the RMI connection is encountered.
     */
    void removePropertyChangeListener(PropertyChangeListener listener) throws RemoteException;


    /**<p>Removes a listener from this subject<br></p>
     * @param name A String reference to the event name a specific PropertyChangeEvent.
     * @param listener A PropertyChangeListener, i.e. some code or methods that should be executed when a named specific PropertyChangeEvent is fired.
     * @throws RemoteException If an error relating to the RMI connection is encountered.
     */
    void removePropertyChangeListener(String name, PropertyChangeListener listener) throws RemoteException;


    /** NOT IMPLEMENTED FULLY<br>
     * Method is intended to log out each user from the server, whenever the user closes their local application (disconnects).<br>
     * Currently, the implementation passes the logout request on to the server for processing. Server-side has not fully implemented this method yet.
     * @param username a String containing the name of the user to log out.
     * @param password a String containing the password for the user whom to log out.
     * @throws RemoteException If an error relating to the RMI connection is encountered.
     */
    void logoutUserFromServer(String username, String password) throws RemoteException;
}
