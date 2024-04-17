package Model.Login;

import DataTypes.User;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LoginServerModelImpl implements LoginServerModel, Runnable {

  private PropertyChangeSupport support;
  private static LoginServerModel instance;
  private static final Lock lock = new ReentrantLock();
  private ArrayList<User> users;

  private LoginServerModelImpl()
  {
    support = new PropertyChangeSupport(this);

    users = new ArrayList<>();
    addTestUsers();
  }

  public static LoginServerModel getInstance()
  {
    //Here we use the "Double-checked lock" principle to ensure proper synchronization.
    if(instance == null)
    {
      synchronized (lock)
      {
        if(instance == null)
        {
          instance = new LoginServerModelImpl();
        }
      }
    }
    return instance;
  }

  @Override
  public void validateUser(String username, String password) {
    System.out.println("Validating user: " + username + " " + password);
    for (User user : users) {
      if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
        support.firePropertyChange("userLoginSuccess", null, user);
        return; // if user found on user list, return true
      }
    }
  }

  @Override
  public void createUser(String username, String password) {
    System.out.println("Creating user: " + username + " " + password);
    users.add(new User(username, password));
    support.firePropertyChange("userCreatedSuccess", null, null);
  }

  private void addTestUsers() {
    this.users.add(new User("test", "123"));
  }

  @Override public void addPropertyChangeListener(PropertyChangeListener listener) {
    support.addPropertyChangeListener(listener);
  }
  @Override public void addPropertyChangeListener(String name, PropertyChangeListener listener) {
    support.addPropertyChangeListener(name, listener);
  }
  @Override public void removePropertyChangeListener(PropertyChangeListener listener) {
    support.removePropertyChangeListener(listener);
  }
  @Override public void removePropertyChangeListener(String name, PropertyChangeListener listener) {
    support.removePropertyChangeListener(name, listener);
  }

  @Override public void run()
  {
    //TODO
  }


}
