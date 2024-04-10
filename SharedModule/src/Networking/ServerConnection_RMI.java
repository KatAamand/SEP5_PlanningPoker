package Networking;

import java.rmi.Remote;

public interface ServerConnection_RMI extends Remote {
    void registerClient(ClientConnection_RMI client);
    void unRegisterClient(ClientConnection_RMI client);

}
