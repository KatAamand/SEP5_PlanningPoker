package Model.Login;

import Application.ClientFactory;

import DataTypes.User;
import Networking.Client;
import javafx.application.Platform;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.rmi.RemoteException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Implementation of the client side model. This class handles data related logical operations on the client side,
 * and queries server related data through the Client_RMI network class.
 */
public class LoginModelImpl implements LoginModel {
    private final PropertyChangeSupport support;
    private Client clientConnection;
    private ArrayList<User> userList;

    public LoginModelImpl() {
        support = new PropertyChangeSupport(this);

        //Assign the network connection:
        try {
            clientConnection = (Client) ClientFactory.getInstance().getClient();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        assignListeners();
        clientConnection.requestUserList();
    }

    @Override
    public void setUserList(ArrayList<User> userList) {
        this.userList = userList;
    }

    @Override
    public ArrayList<User> getUserList() {
        return userList;
    }


    @Override
    public void requestLogin(String username, String password) {
        System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", LoginModelImpl: Validating user");
        clientConnection.validateUser(username, password);
    }

    @Override
    public void requestCreateUser(String username, String password) {
        if (usernameAlreadyExists(username)) {
            System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", LoginModelImpl: User already exists");
            support.firePropertyChange("userAlreadyExists", null, true);
        } else {
            System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", LoginModelImpl: Creating user");
            clientConnection.createUser(username, password);
        }
    }

    @Override
    public void requestLogout(String username, String password) {
        clientConnection.logoutUser(username, password);
    }

    @Override
    public boolean usernameAlreadyExists(String username) {
        if (userList != null) {
            for (User user : userList) {
                if (user.getUsername().equals(username)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    @Override
    public void addPropertyChangeListener(String name, PropertyChangeListener listener) {
        support.addPropertyChangeListener(name, listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(String name, PropertyChangeListener listener) {
        support.removePropertyChangeListener(name, listener);
    }


    /**
     * Assigns all the required listeners to the clientConnection allowing for Observable behavior between classes.
     */
    private void assignListeners() {
        clientConnection.addPropertyChangeListener("userListRecieved", evt -> {
            Platform.runLater(() -> {
                setUserList((ArrayList<User>) evt.getNewValue());
            });
        });

        clientConnection.addPropertyChangeListener("userLoginSuccess", evt -> {
            Platform.runLater(() -> {
                System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", LoginModelImpl: User logged ind successfully");
                support.firePropertyChange("userLoginSuccess", null, evt.getNewValue());
            });
        });

        clientConnection.addPropertyChangeListener("userCreatedSuccess", evt -> {
            Platform.runLater(() -> {
                System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", LoginModelImpl: User created successfully");
                support.firePropertyChange("userCreatedSuccess", null, null);
            });
        });

        clientConnection.addPropertyChangeListener("userValidationFailed", evt -> {
            Platform.runLater(() -> {
                support.firePropertyChange("userValidationFailed", null, evt.getNewValue());
            });
        });
    }

}