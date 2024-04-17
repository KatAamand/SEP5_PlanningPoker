package Networking;

import DataTypes.User;
import Networking.ClientInterfaces.*;

import java.rmi.Remote;

public interface ClientConnection_RMI extends Remote, ChatClientInterface, LoginClientInterface, MainClientInterface, TaskClientInterface, GameClientInterface, LobbyClientInterface { // Extends all the client-interfaces

    User getCurrentUser();
}
