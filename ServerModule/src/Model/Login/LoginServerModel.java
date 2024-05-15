package Model.Login;

import DataTypes.User;
import Util.PropertyChangeSubject;

import java.rmi.RemoteException;

public interface LoginServerModel extends PropertyChangeSubject
{
    User validateUser(String username, String password) throws RemoteException;
    Boolean createUser(String username, String password) throws RemoteException;
    boolean logoutUser(String username, String password);
}
