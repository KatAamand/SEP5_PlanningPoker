package Networking;

import java.util.ArrayList;
import java.util.List;

public class Server_RMI implements ServerConnection_RMI {
    private ServerModel model;
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
