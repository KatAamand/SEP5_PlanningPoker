package Networking;

import Application.Session;
import DataTypes.Message;
import DataTypes.Task;
import DataTypes.PlanningPoker;
import DataTypes.User;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.rmi.NoSuchObjectException;
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
      server.validateUser(username, password, this);
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
      server.createUser(username, password, this);
    }
    catch (RemoteException e)
    {
      throw new RuntimeException(e);
    }
    System.out.println("Client_RMI: user trying to create user");
  }

  @Override public void logoutUser(String username, String password)
  {
    //Check that the requested user is the local user before logging out (prevent local user from logging out other remote users):
    if(Session.getCurrentUser() != null
        && Session.getCurrentUser().getUsername().equals(username)
        && Session.getCurrentUser().getUsername().equals(password)) {
      try {
        this.logoutUserFromServer(username, password);
      } catch (RemoteException e) {
        throw new RuntimeException();
      }
    }

    //Disconnect the client:
    this.disconnectLocalClient();
  }


  @Override public void disconnectLocalClient() {
    try {
      // Unregister the client from the server:
      server.unRegisterClient(this);

      // Unexport the client from the RMI:
      UnicastRemoteObject.unexportObject(this, true);

      System.out.println("Client_RMI: Local Client is disconnected");
    } catch (RemoteException e) {
      throw new RuntimeException();
    }

    // Shut down the client thread:
    System.exit(0);
  }


  @Override public void logoutUserFromServer(String username, String password) throws RemoteException {
      server.logoutUser(username, password);
  }

  @Override
  public void userValidationFailed(String errorMessage) {
      propertyChangeSupport.firePropertyChange("userValidationFailed", null, errorMessage);
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

  @Override public boolean validatePlanningPokerID(String planningPokerID)
  {
    try
    {
      System.out.println("Client_RMI: planningPokerID trying to validate");
      boolean serverAnswer = server.validatePlanningPokerID(planningPokerID);
      if(serverAnswer) {
        System.out.println("Opdatering fra server: planningPokerID is validated");
        propertyChangeSupport.firePropertyChange("planningPokerIDValidatedSuccess", null, planningPokerID);
      } else {
        System.out.println("Client_RMI: planningPokerID validation failed");
      }
      return serverAnswer;
    }
    catch (RemoteException e)
    {
      throw new RuntimeException(e);
    }
  }

  @Override public PlanningPoker createPlanningPoker()
  {
    try
    {
      System.out.println("Client_RMI: user trying to create planningPoker");
      PlanningPoker serverAnswer = server.createPlanningPoker(this);

      if(serverAnswer != null) {
        //PlanningPoker created successfully
        System.out.println("Opdatering fra server: planningPokerID is created succesfully");
        propertyChangeSupport.firePropertyChange("planningPokerCreatedSuccess", null, serverAnswer);
        return serverAnswer;
      }
    }
    catch (RemoteException e)
    {
      throw new RuntimeException(e);
    }
    return null;
  }

  @Override public PlanningPoker loadPlanningPoker(String planningPokerId) {
      try {
        System.out.println("Client_RMI: trying to load PlanningPoker Game with ID " + planningPokerId);
        PlanningPoker serverAnswer = server.loadPlanningPokerGame(planningPokerId, this);

        if(serverAnswer != null) {
          //PlanningPoker loaded successfully
          System.out.println("Opdatering fra server: PlanningPoker game has been loaded successfully");
          return serverAnswer;
        }
      } catch (RemoteException e) {
        throw new RuntimeException();
      }
      return null;
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

  @Override public void loadTaskList(String gameId) {
    try {
      this.loadTaskListFromServer(gameId);
    }
    catch (RemoteException e) {
      //TODO: Add proper exception handling
      e.printStackTrace();
    }
  }

  @Override public void loadTaskListFromServer(String gameId) throws RemoteException
  {
    ArrayList<Task> taskList = server.getTaskList(gameId);
    if(taskList != null) {
      System.out.println("Loaded taskList from server.");
      propertyChangeSupport.firePropertyChange("receivedUpdatedTaskList", null, taskList);
    }
  }

  @Override public void addTask(Task task, String gameId) {
    try {
      this.addTaskToServer(task, gameId);
    } catch (RemoteException e) {
      throw new RuntimeException();
    }
  }

  @Override public boolean removeTask(Task task, String gameId)
  {
    try {
      return this.removeTaskFromServer(task, gameId);
    } catch (RemoteException e) {
      throw new RuntimeException();
    }
  }

  @Override
  public void sendUser() {
      try {
          server.addConnectedUserToSession(Session.getCurrentUser());
      } catch (RemoteException e) {
          throw new RuntimeException(e);
      }
  }

  @Override
  public void removeUserFromSession() {
      try {
          server.removeUserFromSession(Session.getCurrentUser());
      } catch (RemoteException e) {
          throw new RuntimeException(e);
      }
  }


  @Override public void addTaskToServer(Task task, String gameId) throws RemoteException {
      server.addTask(task, gameId);
    }

  @Override public boolean removeTaskFromServer(Task task, String gameId) throws RemoteException {
    return server.removeTask(task, gameId);
  }

  @Override
  public void sendMessage(Message message, User sender) {
      try {
          server.sendMessage(message, sender);
      } catch (RemoteException e) {
          throw new RuntimeException(e);
      }
  }

  @Override public void receiveMessage(Message message)
  {
    propertyChangeSupport.firePropertyChange("messageReceived", null, message);
  }

    @Override
    public User getCurrentUser() throws RemoteException {
        return Session.getCurrentUser();
    }

  @Override
  public void receiveUser(ArrayList<User> users) throws RemoteException {
    System.out.println("Client receiving user");
    propertyChangeSupport.firePropertyChange("userReceived", null, users);
  }


}
