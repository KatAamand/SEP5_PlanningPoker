package Networking;

import Model.Chat.ChatServerModel;
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

    private List<ClientConnection_RMI> connectedClients;

    public Server_RMI() {
        connectedClients = new ArrayList<>();
    }

    @Override
    public void registerClient(ClientConnection_RMI client) {
        connectedClients.add(client);
    }

    @Override
    public void unRegisterClient(ClientConnection_RMI client) {
        connectedClients.remove(client);
    }
}
