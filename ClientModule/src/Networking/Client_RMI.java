package Networking;

import DataTypes.User;
import Util.PropertyChangeSubject;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Client_RMI implements ClientConnection_RMI, PropertyChangeSubject {

    private ServerConnection_RMI server;
    private PropertyChangeSupport propertyChangeSupport;

    public Client_RMI()
    {
        try {
            UnicastRemoteObject.exportObject(this, 0);
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            server = (ServerConnection_RMI) registry.lookup("Model");
            server.registerClient(this);
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }


    // Requests for Login
    @Override
    public void validateUser(String username, String password) {
        try {
            server.validateUser(username, password);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Client_RMI: user trying to validate");
    }

    @Override
    public void createUser(String username, String password) {
        try {
            server.createUser(username, password);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Client_RMI: user trying to create user");
    }

    @Override
    public void userCreatedSuccessfully() {
        System.out.println("Opdatering fra server: user is created succesfully");
        propertyChangeSupport.firePropertyChange("userCreatedSuccess", null, null);
    }

    @Override
    public void updateUser(User user) {
        System.out.println("Opdatering fra server: user is logged in succesfully");
        propertyChangeSupport.firePropertyChange("userLoginSuccess", null, user);
    }


    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
    @Override
    public void addPropertyChangeListener(String name, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(name, listener);
    }
    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
    @Override
    public void removePropertyChangeListener(String name, PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(name, listener);
    }

    @Override
    public void sendMessage(String message, User sender) {
        try {
            server.sendMessage(message, sender);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void receiveMessage(String message) {
        propertyChangeSupport.firePropertyChange("messageReceived", null, message);
    }


}
