package Model.Login;

import DataTypes.User;
import Networking.ClientConnection_RMI;
import Networking.Server_RMI;
import Util.PropertyChangeSubject;

import java.rmi.RemoteException;
import java.util.ArrayList;

public interface LoginServerModel extends PropertyChangeSubject
{
    User validateUser(String username, String password) throws RemoteException;
    Boolean createUser(String username, String password) throws RemoteException;
    boolean logoutUser(String username, String password);

    ArrayList<User> requestUserList();
}
