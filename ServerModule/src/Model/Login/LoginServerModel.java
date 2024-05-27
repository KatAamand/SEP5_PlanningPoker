package Model.Login;

import DataTypes.User;
import Util.PropertyChangeSubject;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * The LoginServerModel interface defines all the key methods relating to handling the server-side logic of User validation, login and creation.
 * It extends the PropertyChangeSubject interface and is thus prepared for use with Observer Pattern principles.
 */
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


    /** NOT IMPLEMENTED FULLY<br>
     * Method is intended to log out each user from the server, whenever the user closes their local application (disconnects).<br>
     * Currently, the implementation does nothing, except return False (logout failed).
     * @param username a String containing the name of the user to log out.
     * @param password a String containing the password for the user whom to log out.
     * @return True, if log out was successful. False, if log out failed.
     */
    boolean logoutUser(String username, String password);

    /**
     * Requests the list of all users.
     * @return an ArrayList<User> containing all users.
     */
    ArrayList<User> requestUserList();
}
