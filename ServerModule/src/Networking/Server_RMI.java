package Networking;

import DataTypes.*;
import DataTypes.UserRoles.UserRole;
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
    private Map<Integer, ArrayList<ClientConnection_RMI>> clientsInEachGame = new HashMap<>();

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
        taskServerModel.addPropertyChangeListener("TaskDataChanged", (evt) -> {
            if(evt.getNewValue() != null) {
                broadcastTaskListUpdate((Integer) evt.getNewValue());
            }}
        );
    }


    @Override
    public void registerClient(ClientConnection_RMI client) {
        connectedClients.add(client);
    }

    @Override public void registerClientToGame(ClientConnection_RMI client, int planningPokerId) throws RemoteException
    {
        ArrayList<ClientConnection_RMI> existingClients;

        if(clientsInEachGame.get(planningPokerId) != null) {
            existingClients = clientsInEachGame.get(planningPokerId);
        } else {
            existingClients = new ArrayList<>();
        }

        if(!existingClients.contains(client)) {
            existingClients.add(client);
            clientsInEachGame.put(planningPokerId, existingClients);
        }
    }

    @Override
    public void unRegisterClient(ClientConnection_RMI client) {
        connectedClients.remove(client);
        this.unRegisterClientsFromAnyConnectedGames(client);
    }

    @Override public void unRegisterClientFromGame(ClientConnection_RMI client, int planningPokerId)
    {
        ArrayList<ClientConnection_RMI> existingClients = clientsInEachGame.get(planningPokerId);

        if(existingClients != null && existingClients.remove(client)) {
            clientsInEachGame.put(planningPokerId, existingClients);
        }
    }

    private void unRegisterClientsFromAnyConnectedGames(ClientConnection_RMI client) {
        ArrayList<Integer> gameIdsToRemoveClientsFrom = new ArrayList<>();
        ArrayList<ClientConnection_RMI> clientsToRemove = new ArrayList<>();

        for (Map.Entry<Integer, ArrayList<ClientConnection_RMI>> contents : clientsInEachGame.entrySet()) {
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
    private void broadcastTaskListUpdate(int planningPokerId) {
        taskServerModel.broadcastTaskListUpdate(clientsInEachGame, this, planningPokerId);
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
    @Override public ArrayList<Task> getTaskList(int planningPokerId) throws RemoteException
    {
        return taskServerModel.getTaskList(planningPokerId);
    }

    @Override public void addTask(Task task, int planningPokerId) throws RemoteException
    {
        // Create the task:
        taskServerModel.addTask(task, planningPokerId);

        // Update the Planning Poker object, so that it now holds the proper list of assigned tasks.
        mainServerModel.getPlanningPokerGame(planningPokerId).setTaskList(taskServerModel.getTaskList(planningPokerId));
    }

    @Override public boolean removeTask(Task task, int planningPokerId) throws RemoteException
    {
        // Attempt to remove the task:
        boolean success = taskServerModel.removeTask(task, planningPokerId);
        if(success) {
            // If successful, update the Planning Poker object, so that it now holds the proper list of assigned tasks.
            mainServerModel.getPlanningPokerGame(planningPokerId).setTaskList(taskServerModel.getTaskList(planningPokerId));
        }
        return success;
    }

    @Override public boolean editTask(Task oldTask, Task newTask, int planningPokerId) throws RemoteException
    {
        // Attempt to edit the task:
        boolean success = taskServerModel.editTask(oldTask, newTask, planningPokerId);
        if(success) {
            // If successful, update the Planning Poker object, so that it now holds the proper list of assigned tasks.
            mainServerModel.getPlanningPokerGame(planningPokerId).setTaskList(taskServerModel.getTaskList(planningPokerId));
        }
        return success;
    }

    @Override public void broadcastSkipTasks(ArrayList<Task> skippedTasksList, int planningPokerId) throws RemoteException
    {
        // Attempt to broadcast the list of tasks to skip in the current game's UI to all clients:
        gameServerModel.broadcastListOfSkippedTasksToClients(clientsInEachGame, skippedTasksList, planningPokerId, this);
    }

    // MainView related requests
    @Override public boolean validatePlanningPokerID(int planningPokerID)
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

    @Override public PlanningPoker loadPlanningPokerGame(int planningPokerId, ClientConnection_RMI client) throws RemoteException {
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

    @Override
    public void placeCard(UserCardData userCardData) throws RemoteException {
        System.out.println("Server_RMI: Requesting placed card");
        gameServerModel.placeCard(userCardData, connectedClients, this);
    }

    @Override
    public void requestClearPlacedCards() throws RemoteException {
        gameServerModel.clearPlacedCards(connectedClients, this);
    }

    /** These roles are set from the following client classes:<br>
     - Scrum Master is set on game creation of a Planning Poker game. The call cascades from MainModelImpl and down to the server connection. <br>
     - Product Owner is currently NOT set anywhere. Should be set from within the PlanningPokerViewModel once implemented.<br>
     - Developer is currently assigned to each user (other than the game creator) who joins a game.<br>
     */
    @Override public User setRoleInPlanningPokerGame(UserRole roleToApply, User userToReceiveRole, int planningPokerId) throws RemoteException
    {
        // Apply the role to the specified user in the specified game:
        User returnedUser = mainServerModel.applyPlanningPokerRole(roleToApply, userToReceiveRole, planningPokerId);
        if(returnedUser != null) {
            // Broadcast the updated PlanningPoker Object to all connected clients in the specified game
            mainServerModel.broadcastPlanningPokerObjUpdate(clientsInEachGame, this, planningPokerId);
            return returnedUser;
        } else {
            // Failed to set the role. Do nothing further.
            System.out.println("Server_RMI: Failed to set the Role [" + roleToApply + "] to user [" + userToReceiveRole.getUsername() + "]");
            return null;
        }
    }

    @Override
    public void requestShowCards() throws RemoteException {
        gameServerModel.showCards(connectedClients, this);
    }

    @Override
    public void broadcastUserInSession(User user) throws RemoteException {
        chatServerModel.broadcastUsers(user, connectedClients, this, user.getPlanningPoker());
    }

    @Override
    public void setProductOwner(User user) throws RemoteException {
        mainServerModel.setProductOwner(user, connectedClients, this);
    }

    @Override
    public ArrayList<User> requestUserList() throws RemoteException {
        return loginServerModel.requestUserList();
    }

    @Override
    public ArrayList<Effort> getEffortList() throws RemoteException {
        return gameServerModel.getEffortList();
    }
}
