package Model.Chat;

import DataTypes.Message;
import DataTypes.PlanningPoker;
import DataTypes.User;
import Networking.ClientConnection_RMI;
import Networking.ServerConnection_RMI;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Array;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ChatServerModelImpl implements ChatServerModel, Runnable
{
  private PropertyChangeSupport propertyChangeSupport;
  private static ChatServerModel instance;
  private static final Lock lock = new ReentrantLock();



  private ChatServerModelImpl()
  {
    //TODO

  }

  public static ChatServerModel getInstance()
  {
    //Here we use the "Double-checked lock" principle to ensure proper synchronization.
    if(instance == null)
    {
      synchronized (lock)
      {
        if(instance == null)
        {
          instance = new ChatServerModelImpl();
        }
      }
    }
    return instance;
  }

  @Override public void addPropertyChangeListener(PropertyChangeListener listener) {
    propertyChangeSupport.addPropertyChangeListener(listener);
  }
  @Override public void addPropertyChangeListener(String name, PropertyChangeListener listener) {
    propertyChangeSupport.addPropertyChangeListener(name, listener);
  }
  @Override public void removePropertyChangeListener(PropertyChangeListener listener) {
    propertyChangeSupport.removePropertyChangeListener(listener);
  }
  @Override public void removePropertyChangeListener(String name, PropertyChangeListener listener) {
    propertyChangeSupport.removePropertyChangeListener(name, listener);
  }

  @Override public void run()
  {
    //TODO
  }

  @Override
  public void receiveAndBroadcastMessage(Message message, User sender, ArrayList<ClientConnection_RMI> connectedClients, ServerConnection_RMI server) throws RemoteException {
    System.out.println(connectedClients);
    sender.getPlanningPoker().getChat().addMessage(message);

    for (ClientConnection_RMI client : connectedClients)
    {
      // Run this in a try brackets:
      Thread sendMessageThread = new Thread(() -> {
        try {
          if (client.getCurrentUser().getPlanningPoker().getPlanningPokerID() == sender.getPlanningPoker().getPlanningPokerID())
          {
            client.receiveMessage(message);
            System.out.println("Sending message to: " + client);
          }
        } catch (RemoteException e) {
          if(String.valueOf(e.getCause()).equals("java.net.ConnectException: Connection refused: connect")) {
            //Unregisters clients from the Server, who have lost connection in order to avoid further server errors.
            try {
              server.unRegisterClient(client);
            } catch (RemoteException ex) {
              throw new RuntimeException();
            }
          } else {
            //Error is something else:
            throw new RuntimeException();
          }
        }
      });
      sendMessageThread.setDaemon(true);
      sendMessageThread.start();
    }
  }

  @Override
  public void addUserToSession(User user, ArrayList<ClientConnection_RMI> connectedClients, ServerConnection_RMI server) throws RemoteException {

    for (ClientConnection_RMI client : connectedClients) {
      if (client.getCurrentUser().getPlanningPoker().getPlanningPokerID() == user.getPlanningPoker().getPlanningPokerID())
      {
        if (!user.getPlanningPoker().getConnectedUsers().contains(client.getCurrentUser())) {
          user.getPlanningPoker().getConnectedUsers().add(client.getCurrentUser());
        }
      }
    }
    broadcastUsers(user, connectedClients, server, user.getPlanningPoker());
  }

  @Override
  public void broadcastUsers(User user, ArrayList<ClientConnection_RMI> connectedClients, ServerConnection_RMI server, PlanningPoker planningPoker) throws RemoteException {
    ArrayList<User> userList = new ArrayList<>();
    //planningPoker.getConnectedUsers().clear();
    for (ClientConnection_RMI client : connectedClients)
    {
        if (client.getCurrentUser().getPlanningPoker().getPlanningPokerID() == planningPoker.getPlanningPokerID()) {
          userList.add(client.getCurrentUser());
          //planningPoker.getConnectedUsers().add(client.getCurrentUser());
        }
    }
    for (ClientConnection_RMI client : connectedClients) {
      Thread sendUserThread = new Thread(() -> {
        try {
            if (client.getCurrentUser().getPlanningPoker().getPlanningPokerID() == planningPoker.getPlanningPokerID()) {
              System.out.println("Sending user to client");
              client.receiveUser(userList);
              //client.receiveUser(planningPoker.getConnectedUsers());
            }
        } catch (RemoteException e) {
          if(String.valueOf(e.getCause()).equals("java.net.ConnectException: Connection refused: connect")) {
            //Unregisters clients from the Server, who have lost connection in order to avoid further server errors.
            try {
              server.unRegisterClient(client);
            } catch (RemoteException ex) {
              throw new RuntimeException();
            }
          } else {
            //Error is something else:
            throw new RuntimeException();
          }
        }
      });
      sendUserThread.setDaemon(true);
      sendUserThread.start();
    }
  }

  @Override
  public void broadcastUsersWithException(User removedUser, ArrayList<ClientConnection_RMI> connectedClients, ServerConnection_RMI server, PlanningPoker planningPoker) throws RemoteException {
    ArrayList<User> userList = new ArrayList<>();
    //planningPoker.getConnectedUsers().clear();
    for (ClientConnection_RMI client : connectedClients)
    {
      if (!client.getCurrentUser().equals(removedUser)) {
        if (client.getCurrentUser().getPlanningPoker().getPlanningPokerID() == planningPoker.getPlanningPokerID()) {
          userList.add(client.getCurrentUser());
          //planningPoker.getConnectedUsers().add(client.getCurrentUser());
        }
      }
    }
    for (ClientConnection_RMI client : connectedClients) {
      Thread sendUserThread = new Thread(() -> {
        try {
          if (client.getCurrentUser().getPlanningPoker().getPlanningPokerID() == planningPoker.getPlanningPokerID()) {
            System.out.println("Sending user to client");
            client.receiveUser(userList);
            //client.receiveUser(planningPoker.getConnectedUsers());
          }
        } catch (RemoteException e) {
          if(String.valueOf(e.getCause()).equals("java.net.ConnectException: Connection refused: connect")) {
            //Unregisters clients from the Server, who have lost connection in order to avoid further server errors.
            try {
              server.unRegisterClient(client);
            } catch (RemoteException ex) {
              throw new RuntimeException();
            }
          } else {
            //Error is something else:
            throw new RuntimeException();
          }
        }
      });
      sendUserThread.setDaemon(true);
      sendUserThread.start();
    }
  }

  @Override
  public void removeUserFromSession(User user, ArrayList<ClientConnection_RMI> connectedClients, ServerConnection_RMI server) throws RemoteException {
    if (user.getPlanningPoker().getConnectedUsers().contains(user))
    {
      System.out.println("Removing user from session");
      PlanningPoker temp = user.getPlanningPoker();
      temp.getConnectedUsers().remove(user);
      for (ClientConnection_RMI client : connectedClients)
      {
        if (client.getCurrentUser().equals(user))
        {
          client.getCurrentUser().setPlanningPoker(null);
          System.out.println(client.getCurrentUser().getPlanningPoker());
        }
      }
      broadcastUsersWithException(user, connectedClients, server, temp);
    }

  }


}
