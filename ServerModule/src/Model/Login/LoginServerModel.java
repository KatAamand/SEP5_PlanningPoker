package Model.Login;

import DataTypes.User;
import Networking.ClientConnection_RMI;
import Networking.Server_RMI;
import Util.PropertyChangeSubject;

import java.rmi.RemoteException;
import java.util.ArrayList;

public interface LoginServerModel extends PropertyChangeSubject
{
    /**
     * Validates a user's credentials.
     * @param username the username of the user to validate.
     * @param password the password of the user to validate.
     * @return the User object if the credentials are valid, null otherwise.
     * @throws RemoteException if there is a communication error during the remote method call.
     */
    User validateUser(String username, String password) throws RemoteException;

    /**
     * Creates a new user with the specified username and password.
     * @param username the desired username for the new user.
     * @param password the desired password for the new user.
     * @return true if the user was successfully created, false otherwise.
     * @throws RemoteException if there is a communication error during the remote method call.
     */
    Boolean createUser(String username, String password) throws RemoteException;
    
    boolean logoutUser(String username, String password);

    /**
     * Requests the list of all users.
     * @return an ArrayList<User> containing all users.
     */
    ArrayList<User> requestUserList();
}
