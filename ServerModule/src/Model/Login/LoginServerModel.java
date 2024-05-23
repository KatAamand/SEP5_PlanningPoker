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

    // TODO: Source code comment/document
    User validateUser(String username, String password) throws RemoteException;


    // TODO: Source code comment/document
    Boolean createUser(String username, String password) throws RemoteException;


    /** NOT IMPLEMENTED FULLY<br>
     * Method is intended to log out each user from the server, whenever the user closes their local application (disconnects).<br>
     * Currently, the implementation does nothing, except return False (logout failed).
     * @param username a String containing the name of the user to log out.
     * @param password a String containing the password for the user whom to log out.
     * @return True, if log out was successful. False, if log out failed.
     */
    boolean logoutUser(String username, String password);


    // TODO: Source code comment/document
    ArrayList<User> requestUserList();
}
