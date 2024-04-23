package Networking;

import DataTypes.Task;
import DataTypes.User;
import Model.Chat.ChatServerModel;
import Model.Chat.ChatServerModelImpl;
import Model.Game.GameServerModel;
import Model.Game.GameServerModelImpl;
import Model.Login.LoginServerModel;
import Model.Login.LoginServerModelImpl;
import Model.Main.MainServerModel;
import Model.Main.MainServerModelImpl;
import Model.Task.TaskServerModel;
import Model.Task.TaskServerModelImpl;

import java.beans.PropertyChangeListener;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Server_RMI implements ServerConnection_RMI {
    private ChatServerModel chatServerModel;
    private LoginServerModel loginServerModel;
    private TaskServerModel taskServerModel;
    private GameServerModel gameServerModel;
    private MainServerModel mainServerModel;
    private ArrayList<ClientConnection_RMI> connectedClients;
    private Map<ClientConnection_RMI, PropertyChangeListener> listeners = new HashMap<>();

    public Server_RMI() throws RemoteException {
        UnicastRemoteObject.exportObject(this, 0);

        connectedClients = new ArrayList<>();
        chatServerModel = ChatServerModelImpl.getInstance();
        loginServerModel = LoginServerModelImpl.getInstance();
        taskServerModel = TaskServerModelImpl.getInstance();
        gameServerModel = GameServerModelImpl.getInstance();
        mainServerModel = MainServerModelImpl.getInstance();
    }


    @Override
    public void registerClient(ClientConnection_RMI client) {
        connectedClients.add(client);
    }

    @Override
    public void unRegisterClient(ClientConnection_RMI client) {
        connectedClients.remove(client);
    }

    @Override
    public void sendMessage(String message, User sender) {
        try {
            chatServerModel.receiveAndBroadcastMessage(message, sender, connectedClients);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }


    // Requests for login
    @Override
    public void validateUser(String username, String password, ClientConnection_RMI client) {
        try {
            System.out.println("Server_RMI: user trying to validate");
            User user = loginServerModel.validateUser(username, password);
            sendUpdateToClient(() -> {
                try {
                    client.updateUser(user);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }, client);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createUser(String username, String password, ClientConnection_RMI client) {
        try {
            System.out.println("Server trying to create user");
            Boolean isLoginCreated;

            isLoginCreated = loginServerModel.createUser(username, password);
            if (isLoginCreated) {
                sendUpdateToClient(() -> {
                    try {
                        client.userCreatedSuccessfully();
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }, client);
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    // Adding listeners to the client
    @Override
    public void registerClientListener(ClientConnection_RMI client) {
        PropertyChangeListener listener = event -> {
             switch (event.getPropertyName()) {
                case "TaskDataChanged":
                     System.out.println("Server: Broadcasting changes to the task list to all clients");
                     sendUpdateToClient(() -> {
                         try {
                             client.loadTaskListFromServer();
                         } catch (RemoteException e) {
                             throw new RuntimeException(e);
                         }
                     }, client);
                     break;
                default:
                    System.out.println("Unrecognized event: " + event.getPropertyName());
                    break;
            }
        };

        listeners.put(client, listener);

        taskServerModel.addPropertyChangeListener("TaskDataChanged", listener);
    }

    private void sendUpdateToClient(Runnable updateAction, ClientConnection_RMI client) {
        try {
            updateAction.run();
        } catch (Exception e) {
            System.out.println("Failed to send update to client, trying again..");
            try {
                Thread.sleep(1000);
                updateAction.run();
            } catch(InterruptedException e2) {
                System.out.println("Failed to send update to client second time, unregistering client");
                try {
                    unRegisterClientListener(client);
                } catch (RemoteException e3) {
                    System.out.println("Failed to unregister client");
                }
            }
        }
    }

    @Override
    public void unRegisterClientListener(ClientConnection_RMI client) throws RemoteException {
        PropertyChangeListener listener = listeners.get(client);

        taskServerModel.removePropertyChangeListener("TaskDataChanged", listener);

        listeners.remove(client);
    }


    //Task related requests
    @Override public ArrayList<Task> getTaskList() throws RemoteException
    {
        //TODO: Add proper session handling here. Tasks should be retrieved from a specific session.
        return taskServerModel.getTaskList();
    }

    @Override public void addTask(Task task) throws RemoteException
    {
        //TODO: Add proper session handling here. Tasks should be assigned to a specific session.
        taskServerModel.addTask(task);
    }


    // MainView related requests
    @Override public void validatePlanningPokerID(String planningPokerID)
    {
      System.out.println("Server_RMI: planningPokerID trying to validate");
      mainServerModel.validatePlanningPoker(planningPokerID);
    }

    @Override public void createPlanningPoker()
    {
      System.out.println("Server trying to create planningPokerID");
      mainServerModel.createPlanningPoker();
    }
}
