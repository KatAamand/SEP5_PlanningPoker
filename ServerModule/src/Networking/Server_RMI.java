package Networking;

import DataTypes.User;
import Model.Chat.ChatServerModel;
import Model.Chat.ChatServerModelImpl;
import Model.Game.GameServerModel;
import Model.Login.LoginServerModel;
import Model.Main.MainServerModel;
import Model.Task.TaskServerModel;

import java.util.ArrayList;
import java.util.List;

public class Server_RMI implements ServerConnection_RMI {
    private ChatServerModel chatServerModel;
    private LoginServerModel loginServerModel;
    private TaskServerModel taskServerModel;
    private GameServerModel gameServerModel;
    private MainServerModel mainServerModel;

    private ArrayList<ClientConnection_RMI> connectedClients;

    public Server_RMI() {
        connectedClients = new ArrayList<>();
        chatServerModel = ChatServerModelImpl.getInstance();
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
}
