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
        taskServerModel.addPropertyChangeListener("TaskDataChanged", evt -> {
            //Broadcast the changes to the tasklist to all clients:
            broadcastTaskListUpdate();
        });

    }

    private void generateDummyTaskData()
    {
        taskServerModel.addTask(new Task("Implement User Authentication", "Develop a user authentication system using OAuth 2.0 to allow users to securely log in with their Google or GitHub accounts."));
        taskServerModel.addTask(new Task("Optimize Database Queries", "Review and optimize database queries in the application's backend to improve performance and reduce response times."));
        taskServerModel.addTask(new Task("Refactor UI Components", "Refactor the frontend UI components using a modern framework like React or Vue.js to enhance user experience and maintainability."));
        taskServerModel.addTask(new Task("Implement RESTful API Endpoints", "Design and implement RESTful API endpoints for CRUD operations to enable seamless interaction between the frontend and backend components."));
        taskServerModel.addTask(new Task("Enhance Error Handling", "Improve error handling mechanisms throughout the application to provide clear and informative error messages for users and developers alike."));
    }

    private void broadcastTaskListUpdate()
    {
        System.out.println("Server: Broadcasting changes to the task list to all clients");
        for (ClientConnection_RMI client : connectedClients)
        {
            try
            {
                client.loadTaskList();
            }
            catch (RemoteException e)
            {
                //TODO: Add proper exception handling.
                e.printStackTrace();
            }
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
        chatServerModel.receiveAndBroadcastMessage(message, sender, connectedClients);
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
                default:
                    System.out.println("Unrecognized event: " + event.getPropertyName());
                    break;
            }
        };

        listeners.put(client, listener);

        loginServerModel.addPropertyChangeListener("userLoginSuccess", listener);
        loginServerModel.addPropertyChangeListener("userCreatedSuccess", listener);
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

    @Override public void validatePlanningPokerID(String planningPokerID) throws RemoteException
    {
        //TODO: Missing implementation
    }

    @Override public void createPlanningPoker() throws RemoteException
    {
        //TODO: Missing implementation
    }
}
