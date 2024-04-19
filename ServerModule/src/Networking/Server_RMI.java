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

        //Load some test/Dummy data:
        generateDummyTaskData();

        //Assign Listeners:
        //TODO: Rework this approach to be used with the HashMap implementation below.
        // However, current hanging issues when multiple clients are connected should be resolved first.
        taskServerModel.addPropertyChangeListener("TaskDataChanged", evt -> {
            //Broadcast the changes to the tasklist to all clients:
            broadcastTaskListUpdate();
        });
    }


    private void generateDummyTaskData() {
        taskServerModel.addTask(new Task("Implement User Authentication", "Develop a user authentication system using OAuth 2.0 to allow users to securely log in with their Google or GitHub accounts."));
        taskServerModel.addTask(new Task("Optimize Database Queries", "Review and optimize database queries in the application's backend to improve performance and reduce response times."));
        taskServerModel.addTask(new Task("Refactor UI Components", "Refactor the frontend UI components using a modern framework like React or Vue.js to enhance user experience and maintainability."));
        taskServerModel.addTask(new Task("Implement RESTful API Endpoints", "Design and implement RESTful API endpoints for CRUD operations to enable seamless interaction between the frontend and backend components."));
        taskServerModel.addTask(new Task("Enhance Error Handling", "Improve error handling mechanisms throughout the application to provide clear and informative error messages for users and developers alike."));
    }


    private void broadcastTaskListUpdate() {
        for (ClientConnection_RMI client : connectedClients)
        {
            //Create a new thread for each connected client, and then call the desired broadcast operation. This minimizes server lag/hanging due to clients who have lost connection.
            Thread transmitThread = new Thread(() -> {
                try {
                    client.loadTaskListFromServer();
                }
                catch (RemoteException e) {
                    if(String.valueOf(e.getCause()).equals("java.net.ConnectException: Connection refused: connect")) {
                        System.out.println("ERROR: Connection to client lost. Unregistering client -> [" + client + "]");

                        //Unregisters clients from the server, who have lost connection in order to avoid further server errors.
                        unRegisterClient(client);
                    }
                    else {
                        //Error is something else:
                        e.printStackTrace();
                    }
                }
            });
            transmitThread.setDaemon(true);
            transmitThread.start();
        }
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
    public void validateUser(String username, String password) {
        try {
            System.out.println("Server_RMI: user trying to validate");
            loginServerModel.validateUser(username, password);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createUser(String username, String password) {
        try {
            System.out.println("Server trying to create user");
            loginServerModel.createUser(username, password);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void registerClientListener(ClientConnection_RMI client) {
        PropertyChangeListener listener = event -> {
             switch (event.getPropertyName()) {
                case "userLoginSuccess":
                    try {
                        System.out.println("userLoginSuccess sent to client");
                        client.updateUser((User) event.getNewValue());
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "userCreatedSuccess":
                    try {
                        System.out.println("userCreatedSuccess sent to client");
                        client.userCreatedSuccessfully();
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                    // TODO: Commented out below, since this approach causes all clients to hang when multiple clients are connected and even one of the clients has lost connection.
                    // TODO: Using my initial approach for now. Test by opening a client - doing something (ie. add a task) - close the client and open a new - try adding another task!
                 /*case "TaskDataChanged":
                     System.out.println("Server: Broadcasting changes to the task list to all clients");
                     try {
                         client.loadTaskList();
                     } catch (RemoteException e) {
                         throw new RuntimeException(e);
                     }
                     break;*/
                default:
                    System.out.println("Unrecognized event: " + event.getPropertyName());
                    break;
            }
        };

        listeners.put(client, listener);

        loginServerModel.addPropertyChangeListener("userLoginSuccess", listener);
        loginServerModel.addPropertyChangeListener("userCreatedSuccess", listener);
        //taskServerModel.addPropertyChangeListener("TaskDataChanged", listener);
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
