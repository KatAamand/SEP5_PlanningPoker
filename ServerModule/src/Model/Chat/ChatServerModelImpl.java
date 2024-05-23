package Model.Chat;

import DataTypes.Message;
import DataTypes.PlanningPoker;
import DataTypes.User;
import Networking.ClientConnection_RMI;
import Networking.ServerConnection_RMI;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.rmi.RemoteException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ChatServerModelImpl implements ChatServerModel
{
  private PropertyChangeSupport propertyChangeSupport;
  private static volatile ChatServerModel instance;
  private static final Lock lock = new ReentrantLock();



  private ChatServerModelImpl() {
    //Empty Constructor
  }

  public static ChatServerModel getInstance() {
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

  @Override
  public void receiveAndBroadcastMessage(Message message, User sender, ArrayList<ClientConnection_RMI> connectedClients, ServerConnection_RMI server) throws RemoteException {
    //System.out.println(connectedClients);
    sender.getPlanningPoker().getChat().addMessage(message);

    for (ClientConnection_RMI client : connectedClients)
    {
      // Run this in a try brackets:
      Thread sendMessageThread = new Thread(() -> {
        try {
          if (client.getCurrentUser().getPlanningPoker().getPlanningPokerID() == sender.getPlanningPoker().getPlanningPokerID())
          {
            client.receiveMessage(message);
            System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", ChatServerModelImpl: Sending message to: " + client.hashCode());
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
        catch (NullPointerException ignored)
        {

        }
      });
      sendMessageThread.setDaemon(true);
      sendMessageThread.start();
    }
  }

  @Override
  public void addUserToSession(User user, ArrayList<ClientConnection_RMI> connectedClients, ServerConnection_RMI server) throws RemoteException {
    List<Thread> threadList = new ArrayList<>();
    AtomicBoolean userNotFound = new AtomicBoolean(true);

    for (ClientConnection_RMI client : connectedClients) {
      Thread addUserThread = new Thread(() -> {
        try {
          if (client.getCurrentUser().getPlanningPoker().getPlanningPokerID() == user.getPlanningPoker().getPlanningPokerID()) {
            for (User existingUser : user.getPlanningPoker().getConnectedUsers()) {
              if (existingUser.getUsername().equals(client.getCurrentUser().getUsername())) {
                userNotFound.set(false);
              }
            }

            if (userNotFound.get()) {
              user.getPlanningPoker().getConnectedUsers().add(client.getCurrentUser());
            }
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
        } catch (NullPointerException ignored) {
        }
      });
      addUserThread.setDaemon(true);
      threadList.add(addUserThread);
      addUserThread.start();
    }
    // Wait for the created threads to finish their evaluations.
    for (Thread thread : threadList) {
      try {
        thread.join();
      } catch (InterruptedException ignored) {}
    }
    // Only broadcast changed userlist if there were actually any changes made:
    if(!userNotFound.get()) {
      broadcastUsers(user, connectedClients, server, user.getPlanningPoker());
    }
  }

  @Override
  public void broadcastUsers(User user, ArrayList<ClientConnection_RMI> connectedClients, ServerConnection_RMI server, PlanningPoker planningPoker) throws RemoteException {
    ArrayList<User> userList = new ArrayList<>();
    ArrayList<Thread> threadList = new ArrayList<>();

    //Check which clients are connected to the current game:
    for (ClientConnection_RMI client : connectedClients)
    {
      Thread newThread = new Thread(() -> {
        try {
          if (client.getCurrentUser().getPlanningPoker().getPlanningPokerID() == planningPoker.getPlanningPokerID()) {
            userList.add(client.getCurrentUser());
          }
        } catch (NullPointerException | RemoteException e) {
          System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", ChatServerModelImpl: User not found in game. Ignoring user.");
        }
      });
      newThread.setDaemon(true);
      threadList.add(newThread);
      newThread.start();
    }

    // Wait for all spawned threads to finish executing:
    for (Thread thread : threadList) {
      try {
        thread.join();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

    // Broadcast to the remaining clients:
    for (ClientConnection_RMI client : connectedClients) {
      Thread sendUserThread = new Thread(() -> {
        try {
            if (client.getCurrentUser().getPlanningPoker().getPlanningPokerID() == planningPoker.getPlanningPokerID()) {
              System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", ChatServerModelImpl: Sending user to client");
              client.receiveUser(userList);
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
        } catch (NullPointerException ignored) {}
      });
      sendUserThread.setDaemon(true);
      sendUserThread.start();
    }
  }

  @Override
  public synchronized void broadcastUsersWithException(User removedUser, ArrayList<ClientConnection_RMI> connectedClients, ServerConnection_RMI server, PlanningPoker planningPoker) throws RemoteException {
    ArrayList<User> userList = new ArrayList<>();
    ArrayList<ClientConnection_RMI> clientList = new ArrayList<>(connectedClients);
    ArrayList<Thread> threadList = new ArrayList<>();
    //planningPoker.getConnectedUsers().clear();
    for (ClientConnection_RMI client : clientList) {
      Thread userListThread = new Thread (() -> {
        try {
          if (!client.getCurrentUser().equals(removedUser)) {
            if (client.getCurrentUser().getPlanningPoker().getPlanningPokerID() == planningPoker.getPlanningPokerID()) {
              userList.add(client.getCurrentUser());
              //planningPoker.getConnectedUsers().add(client.getCurrentUser());
            }
          }
        } catch (NullPointerException ignored) {
          System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", ChatServerModelImpl: User found not ind game. Ignoring user.");
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
      userListThread.setDaemon(true);
      threadList.add(userListThread);
      userListThread.start();
    }
    // Wait for all threads to finish executing:
    for (Thread thread : threadList) {
      try {
        thread.join();
      } catch (InterruptedException ignored) {}
    }

    // Start sending clients
    for (ClientConnection_RMI client : clientList) {
      Thread sendUserThread = new Thread(() -> {
        try {
          if (client.getCurrentUser().getPlanningPoker().getPlanningPokerID() == planningPoker.getPlanningPokerID()) {
            System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", ChatServerModelImpl: Sending user to client");
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
        } catch (NullPointerException ignored) {}
      });
      sendUserThread.setDaemon(true);
      sendUserThread.start();
    }
  }

  @Override
  public void removeUserFromSession(User user, ArrayList<ClientConnection_RMI> connectedClients, ServerConnection_RMI server) throws RemoteException {

    boolean userFound = false;
    for (User existingUser : user.getPlanningPoker().getConnectedUsers()) {
      if(existingUser.getUsername().equals(user.getUsername())) {
        userFound = true;
      }
    }

    if(userFound) {
      System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", ChatServerModelImpl: Removing user from session");
      PlanningPoker temp = user.getPlanningPoker();
      temp.getConnectedUsers().remove(user);
      for (int i = 0; i < temp.getConnectedUsers().size(); i++) {
        if(temp.getConnectedUsers().get(i).getUsername().equals(user.getUsername())) {
          temp.getConnectedUsers().remove(i);
          i--;
        }
      }
      broadcastUsersWithException(user, connectedClients, server, temp);
    }
  }

}
