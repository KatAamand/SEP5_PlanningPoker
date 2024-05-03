package Networking.ClientInterfaces;

import DataTypes.User;
import java.beans.PropertyChangeListener;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LoginClientInterface extends Remote {
    void validateUser(String username, String password) throws RemoteException;
    void createUser(String username, String password) throws RemoteException;
    void userValidationFailed(String errorMessage) throws RemoteException;
    void userCreatedSuccessfully() throws RemoteException;
    void updateUser(User user) throws RemoteException;
    void addPropertyChangeListener(PropertyChangeListener listener) throws RemoteException;

    void addPropertyChangeListener(String name, PropertyChangeListener listener) throws RemoteException;

    void removePropertyChangeListener(PropertyChangeListener listener) throws RemoteException;

    void removePropertyChangeListener(String name, PropertyChangeListener listener) throws RemoteException;
}
