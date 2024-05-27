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


    /**
     * Validates a user's credentials.
     * @param username the username of the user to validate.
     * @param password the password of the user to validate.
     * @throws RemoteException if there is a communication error during the remote method call.
     */
    void validateUser(String username, String password) throws RemoteException;


    /**
     * Creates a new user with the specified username and password.
     * @param username the desired username for the new user.
     * @param password the desired password for the new user.
     * @throws RemoteException if there is a communication error during the remote method call.
     */
    void createUser(String username, String password) throws RemoteException;


    /**
     * Notifies the client that user validation has failed.
     * @param errorMessage the error message describing the validation failure.
     * @throws RemoteException if there is a communication error during the remote method call.
     */
    void userValidationFailed(String errorMessage) throws RemoteException;


    /**
     * Notifies the client that a user has been successfully created.
     * @throws RemoteException if there is a communication error during the remote method call.
     */
    void userCreatedSuccessfully() throws RemoteException;


    /**
     * Updates the client's information with the specified user data.
     * @param user the user data to update.
     * @throws RemoteException if there is a communication error during the remote method call.
     */
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
