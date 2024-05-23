package Networking;

import Networking.ClientInterfaces.*;

/**
 * ClientConnection_RMI defines the interface which is responsible for integrating all the specialized client connection interfaces into one.
 */
public interface ClientConnection_RMI extends ChatClientInterface, LoginClientInterface, MainClientInterface, TaskClientInterface, GameClientInterface, LobbyClientInterface {
    // Extends all the client-interfaces
}
