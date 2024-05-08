package Networking;

import DataTypes.Message;
import DataTypes.PlanningPoker;
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
import java.util.List;
import java.util.Map;

public class Server_RMI implements ServerConnection_RMI {
    private ChatServerModel chatServerModel;
    private LoginServerModel loginServerModel;
    private TaskServerModel taskServerModel;
    private GameServerModel gameServerModel;
    private MainServerModel mainServerModel;
    private ArrayList<ClientConnection_RMI> connectedClients;
    private Map<ClientConnection_RMI, PropertyChangeListener> listeners = new HashMap<>();
    private Map<String, ArrayList<ClientConnection_RMI>> clientsInEachGame = new HashMap<>();

    public Server_RMI() throws RemoteException {
        UnicastRemoteObject.exportObject(this, 0);

        connectedClients = new ArrayList<>();
        chatServerModel = ChatServerModelImpl.getInstance();
        loginServerModel = LoginServerModelImpl.getInstance();
        taskServerModel = TaskServerModelImpl.getInstance();
        gameServerModel = GameServerModelImpl.getInstance();
        mainServerModel = MainServerModelImpl.getInstance();

        //Assign server listeners
        assignServerListeners();
    }

    /** These listeners are local to this server, and listen for events inside the server models. <br> They are not intended for clients to call. */
    private void assignServerListeners() {
        taskServerModel.addPropertyChangeListener("TaskDataChanged", (evt) -> broadcastTaskListUpdate((String) evt.getNewValue()));
    }


    @Override
    public void registerClient(ClientConnection_RMI client) {
        connectedClients.add(client);
    }

    @Override public void registerClientToGame(ClientConnection_RMI client, String gameId) throws RemoteException
    {
        ArrayList<ClientConnection_RMI> existingClients;

        if(clientsInEachGame.get(gameId) != null) {
            existingClients = clientsInEachGame.get(gameId);
        } else {
            existingClients = new ArrayList<>();
        }

        if(!existingClients.contains(client)) {
            existingClients.add(client);
            clientsInEachGame.put(gameId, existingClients);
        }
    }

    @Override
    public void unRegisterClient(ClientConnection_RMI client) {
        connectedClients.remove(client);
        this.unRegisterClientsFromAnyConnectedGames(client);
    }

    @Override public void unRegisterClientFromGame(ClientConnection_RMI client, String gameId)
    {
        ArrayList<ClientConnection_RMI> existingClients = clientsInEachGame.get(gameId);

        if(existingClients != null && existingClients.remove(client)) {
            clientsInEachGame.put(gameId, existingClients);
        }
    }

    private void unRegisterClientsFromAnyConnectedGames(ClientConnection_RMI client) {
        ArrayList<String> gameIdsToRemoveClientsFrom = new ArrayList<>();
        ArrayList<ClientConnection_RMI> clientsToRemove = new ArrayList<>();

        for (Map.Entry<String, ArrayList<ClientConnection_RMI>> contents : clientsInEachGame.entrySet()) {
            for (int i = 0; i < contents.getValue().size(); i++)
            {
                if(contents.getValue().get(i).equals(client)) {
                    gameIdsToRemoveClientsFrom.add(contents.getKey());
                    clientsToRemove.add(contents.getValue().get(i));
                }
            }
        }
        for (int i = 0; i < gameIdsToRemoveClientsFrom.size(); i++)
        {
            this.unRegisterClientFromGame(clientsToRemove.get(i), gameIdsToRemoveClientsFrom.get(i));
        }
    }

    @Override
    public void sendMessage(Message message, User sender) {
        try {
            chatServerModel.receiveAndBroadcastMessage(message, sender, connectedClients, this);
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

            if (user == null) {
                client.userValidationFailed("Forkert brugernavn eller password");
            } else {
                sendUpdateToClient(() -> {
                    try {
                        client.updateUser(user);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }, client);
            }

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

    @Override public void logoutUser(String username, String password) throws RemoteException
    {
        //TODO: This feature is not implemented yet.
        System.out.println("Server_RMI: Logging out user " + username);
        loginServerModel.logoutUser(username, password);
    }

    // Adding listeners to the client
    @Override
    public void registerClientListener(ClientConnection_RMI client) {
        PropertyChangeListener listener = event -> {
             switch (event.getPropertyName()) {

                default:
                    System.out.println("Unrecognized event: " + event.getPropertyName());
                    break;
            }
        };

        listeners.put(client, listener);
    }

    /** Responsible for broadcasting taskListUpdates ONLY to the clients connected to the Game with the provided gameId */
    private void broadcastTaskListUpdate(String gameId) {
        taskServerModel.broadcastTaskListUpdate(clientsInEachGame, this, gameId);
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

        listeners.remove(client);
    }


    //Task related requests
    @Override public ArrayList<Task> getTaskList(String gameId) throws RemoteException
    {
        return taskServerModel.getTaskList(gameId);
    }

    @Override public void addTask(Task task, String gameId) throws RemoteException
    {
        // Create the task
        taskServerModel.addTask(task, gameId);

        // Update the Planning Poker object, so that it now holds the proper list of assigned tasks.
        mainServerModel.getPlanningPokerGame(gameId).setTaskList(taskServerModel.getTaskList(gameId));
    }

    @Override public boolean removeTask(Task task, String gameId) throws RemoteException
    {
        // Attempt to remove the task
        boolean success = taskServerModel.removeTask(task, gameId);
        if(success) {
            // If successful, update the Planning Poker object, so that it now holds the proper list of assigned tasks.
            mainServerModel.getPlanningPokerGame(gameId).setTaskList(taskServerModel.getTaskList(gameId));
        }
        return success;
    }

    // MainView related requests
    @Override public boolean validatePlanningPokerID(String planningPokerID)
    {
      System.out.println("Server_RMI: planningPokerID [" + planningPokerID + "] trying to validate");
      return mainServerModel.validatePlanningPoker(planningPokerID);
    }

    @Override public PlanningPoker createPlanningPoker(ClientConnection_RMI client) throws RemoteException
    {
      System.out.println("Server_RMI: trying to create planningPokerID");
      PlanningPoker planningPoker = mainServerModel.createPlanningPoker();

      if(planningPoker != null) {
          this.registerClientToGame(client, planningPoker.getPlanningPokerID());
          this.mainServerModel.getPlanningPokerGame(planningPoker.getPlanningPokerID()).addUserToSession(client.getCurrentUser());
      }
      return planningPoker;
    }

    @Override public PlanningPoker loadPlanningPokerGame(String planningPokerId, ClientConnection_RMI client) throws RemoteException {
        System.out.println("Server_RMI: planningPokerID trying to validate");
        PlanningPoker planningPoker = mainServerModel.getPlanningPokerGame(planningPokerId);

        if(planningPoker != null) {
            this.registerClientToGame(client, planningPokerId);
            this.mainServerModel.getPlanningPokerGame(planningPoker.getPlanningPokerID()).addUserToSession(client.getCurrentUser());
        }
        return planningPoker;
    }

    @Override
    public void addConnectedUserToSession(User user) throws RemoteException {
            chatServerModel.addUserToSession(user, connectedClients, this);
    }

    @Override
    public void removeUserFromSession(User user) throws RemoteException {
        chatServerModel.removeUserFromSession(user, connectedClients, this);
    }


}
