package Model.Login;

import DataTypes.User;
import Database.User.UserDAO;
import Database.User.UserDAOImpl;
import Networking.ClientConnection_RMI;
import Networking.Server_RMI;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LoginServerModelImpl implements LoginServerModel, Runnable {

    private PropertyChangeSupport support;
    private volatile static LoginServerModel instance;
    private static final Lock lock = new ReentrantLock();
    private ArrayList<User> users;

    private LoginServerModelImpl() {
        support = new PropertyChangeSupport(this);

        users = new ArrayList<>();
        users = getUsersFromDb();
    }

    private ArrayList<User> getUsersFromDb() {
        try {
            UserDAO userDAO = UserDAOImpl.getInstance();
            return userDAO.getAllUsers();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static LoginServerModel getInstance() {
        //Here we use the "Double-checked lock" principle to ensure proper synchronization.
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new LoginServerModelImpl();
                }
            }
        }
        return instance;
    }

    @Override
    public User validateUser(String username, String password) {
        System.out.println("Validating user: " + username + " " + password);
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                System.out.println("user found, now logging user in...");
                return user;
            }
        }
        return null;
    }

    @Override
    public Boolean createUser(String username, String password) {
        System.out.println("Creating user: " + username + " " + password);
        User newUser = new User(username, password);
        users.add(newUser);

        try {
            UserDAO userDAO = UserDAOImpl.getInstance();
            userDAO.create(username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return users.contains(newUser);
    }

    @Override
    public boolean logoutUser(String username, String password) {
        return false;
    }

    @Override
    public ArrayList<User> requestUserList() {
        return users;
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

    @Override
    public void run() {
    }


}
