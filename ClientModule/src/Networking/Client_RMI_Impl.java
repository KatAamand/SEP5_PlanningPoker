package Networking;

import Application.Session;
import DataTypes.Task;
import DataTypes.PlanningPoker;
import DataTypes.User;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Client_RMI_Impl implements Client, ClientConnection_RMI, Serializable {

    private ServerConnection_RMI server;
    private PropertyChangeSupport propertyChangeSupport;

    public Client_RMI_Impl()
    {
        propertyChangeSupport = new PropertyChangeSupport(this);
        try {
            UnicastRemoteObject.exportObject(this, 0);
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            server = (ServerConnection_RMI) registry.lookup("Model");
            server.registerClient(this);
            server.registerClientListener(this);
            System.out.println("user is connected");

        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

  // Requests for Login
  @Override public void validateUser(String username, String password)
  {
    try
    {
      server.validateUser(username, password);
    }
    catch (RemoteException e)
    {
      throw new RuntimeException(e);
    }
    System.out.println("Client_RMI: user trying to validate");
  }

  @Override public void createUser(String username, String password)
  {
    try
    {
      server.createUser(username, password);
    }
    catch (RemoteException e)
    {
      throw new RuntimeException(e);
    }
    System.out.println("Client_RMI: user trying to create user");
  }

  @Override public void userCreatedSuccessfully()
  {
    System.out.println("Opdatering fra server: user is created succesfully");
    propertyChangeSupport.firePropertyChange("userCreatedSuccess", null, null);
  }

  @Override public void updateUser(User user)
  {
    System.out.println("Opdatering fra server: user is logged in succesfully");
    propertyChangeSupport.firePropertyChange("userLoginSuccess", null, user);
  }

  @Override public void validatePlanningPokerID(String planningPokerID)
  {
    try
    {
      server.validatePlanningPokerID(planningPokerID);
    }
    catch (RemoteException e)
    {
      throw new RuntimeException(e);
    }
    System.out.println("Client_RMI: planningPokerID trying to validate");
  }

  @Override public void createPlanningPoker()
  {
    try
    {
      server.createPlanningPoker();
    }
    catch (RemoteException e)
    {
      throw new RuntimeException(e);
    }
    System.out.println("Client_RMI: user trying to create planningPoker");
  }

  @Override public void PlanningPokerCreatedSuccessfully()
  {
    System.out.println("Opdatering fra server: planningPokerID is created succesfully");
    propertyChangeSupport.firePropertyChange("planningPokerCreatedSuccess", null, null);
  }

  @Override public void updatePlanningPoker(String planningPokerID)
  {
    System.out.println("Opdatering fra server: planningPokerID is found in succesfully");
    propertyChangeSupport.firePropertyChange("PlanningPokerIDValidatedSuccess", null, planningPokerID);
  }

  @Override public void updatePlanningPoker(PlanningPoker planningPoker)
      throws RemoteException
  {
    // Needs logic.
  }

  @Override public void addPropertyChangeListener(
      PropertyChangeListener listener)
  {
    propertyChangeSupport.addPropertyChangeListener(listener);
  }

  @Override public void addPropertyChangeListener(String name,
      PropertyChangeListener listener)
  {
    propertyChangeSupport.addPropertyChangeListener(name, listener);
  }

  @Override public void removePropertyChangeListener(
      PropertyChangeListener listener)
  {
    propertyChangeSupport.removePropertyChangeListener(listener);
  }

  @Override public void removePropertyChangeListener(String name,
      PropertyChangeListener listener)
  {
    propertyChangeSupport.removePropertyChangeListener(name, listener);
  }

  @Override public void loadTaskList() {
    try {
      this.loadTaskListFromServer();
    }
    catch (RemoteException e) {
      //TODO: Add proper exception handling
      e.printStackTrace();
    }
  }

  @Override public void loadTaskListFromServer() throws RemoteException
  {
    ArrayList<Task> taskList = server.getTaskList();
    if(taskList != null)
    {
      System.out.println("Loaded taskList from server.");
      propertyChangeSupport.firePropertyChange("receivedUpdatedTaskList", null, taskList);
    }
  }

    @Override public void addTask(Task task)
    {
      try {
        this.addTaskToServer(task);
      } catch (RemoteException e) {
        //TODO: Add proper exception handling
        e.printStackTrace();
      }
    }

    @Override public void addTaskToServer(Task task) throws RemoteException
    {
      server.addTask(task);
    }

    @Override
    public void sendMessage(String message, User sender) {
        try {
            server.sendMessage(message, sender);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

  @Override public void receiveMessage(String message)
  {
    propertyChangeSupport.firePropertyChange("messageReceived", null, message);
  }

    @Override
    public User getCurrentUser() throws RemoteException {
        return Session.getCurrentUser();
    }

}
