package Networking.ClientInterfaces;

import DataTypes.User;
import Util.PropertyChangeSubject;

import java.beans.PropertyChangeListener;
import java.rmi.RemoteException;

public interface LoginClientInterface {
    void validateUser(String username, String password);
    void createUser(String username, String password);
    void userCreatedSuccessfully();
    void updateUser(User user);
    void addPropertyChangeListener(PropertyChangeListener listener) throws RemoteException;

    void addPropertyChangeListener(String name, PropertyChangeListener listener) throws RemoteException;

    void removePropertyChangeListener(PropertyChangeListener listener) throws RemoteException;

    void removePropertyChangeListener(String name, PropertyChangeListener listener) throws RemoteException;
}
