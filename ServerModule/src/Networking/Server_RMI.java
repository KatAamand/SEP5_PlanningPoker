package Networking;

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
}
