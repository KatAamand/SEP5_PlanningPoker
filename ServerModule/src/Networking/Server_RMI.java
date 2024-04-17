package Networking;

import DataTypes.User;
import Model.Chat.ChatServerModel;
import Model.Chat.ChatServerModelImpl;
import Model.Game.GameServerModel;
import Model.Login.LoginServerModel;
import Model.Main.MainServerModel;
import Model.Task.TaskServerModel;

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

    public Server_RMI() throws RemoteException {
        connectedClients = new ArrayList<>();
        chatServerModel = ChatServerModelImpl.getInstance();

        UnicastRemoteObject.exportObject(this, 0);
    }

    @Override
    public void registerClient(ClientConnection_RMI client) throws RemoteException {
        connectedClients.add(client);
    }

    @Override
    public void unRegisterClient(ClientConnection_RMI client) throws RemoteException {
        connectedClients.remove(client);
    }

    @Override
    public void sendMessage(String message, User sender) throws RemoteException {
        chatServerModel.receiveAndBroadcastMessage(message, sender, connectedClients);
    }

    // Requests for login
    @Override
    public void validateUser(String username, String password) throws RemoteException {
        loginServerModel.validateUser(username, password);
    }

    @Override
    public void createUser(String username, String password) throws RemoteException {
        loginServerModel.createUser(username, password);
    }

    @Override
    public void registerClientListener(ClientConnection_RMI client) throws RemoteException {
        PropertyChangeListener listener = event -> {
             switch (event.getPropertyName()) {
                case "userLoginSuccess":
                    client.updateUser((User) event.getNewValue());
                    break;
                case "userCreatedSuccess":
                    client.userCreatedSuccessfully();
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
