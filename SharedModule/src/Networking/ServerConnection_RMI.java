package Networking;

import DataTypes.User;

import java.rmi.Remote;

public interface ServerConnection_RMI extends Remote {
    void registerClient(ClientConnection_RMI client);
    void unRegisterClient(ClientConnection_RMI client);

    void sendMessage(String message, User sender);

    void validateUser(String username, String password);

    void createUser(String username, String password);

    void registerClientListener(ClientConnection_RMI client);
}
