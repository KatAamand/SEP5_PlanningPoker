package Model.Login;

import Util.PropertyChangeSubject;

import java.rmi.RemoteException;

public interface LoginServerModel extends PropertyChangeSubject
{
    boolean validateUser(String username, String password) throws RemoteException;
    void createUser(String username, String password) throws RemoteException;
}
