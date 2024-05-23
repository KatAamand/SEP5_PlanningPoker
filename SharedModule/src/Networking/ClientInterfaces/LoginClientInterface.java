package Networking.ClientInterfaces;

import DataTypes.User;
import java.beans.PropertyChangeListener;
import java.rmi.Remote;
import java.rmi.RemoteException;

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

    void addPropertyChangeListener(PropertyChangeListener listener) throws RemoteException;

    void addPropertyChangeListener(String name, PropertyChangeListener listener) throws RemoteException;

    void removePropertyChangeListener(PropertyChangeListener listener) throws RemoteException;

    void removePropertyChangeListener(String name, PropertyChangeListener listener) throws RemoteException;

    void logoutUserFromServer(String username, String password) throws RemoteException;
}
