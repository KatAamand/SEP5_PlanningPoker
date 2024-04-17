package Networking.ClientInterfaces;

import DataTypes.User;
import Util.PropertyChangeSubject;

import java.beans.PropertyChangeListener;
import java.rmi.RemoteException;

public interface ChatClientInterface {
    void sendMessage(String message, User sender);
    void receiveMessage(String message);
    void addPropertyChangeListener(PropertyChangeListener listener) throws RemoteException;

    void addPropertyChangeListener(String name, PropertyChangeListener listener) throws RemoteException;

    void removePropertyChangeListener(PropertyChangeListener listener) throws RemoteException;

    void removePropertyChangeListener(String name, PropertyChangeListener listener) throws RemoteException;
}
