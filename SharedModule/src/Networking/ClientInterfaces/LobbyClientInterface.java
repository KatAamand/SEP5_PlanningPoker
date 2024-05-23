package Networking.ClientInterfaces;

import Util.PropertyChangeSubject;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LobbyClientInterface extends Remote {
    /**
     * Starts the game for the specified connected game ID.
     * @param connectedGameId the ID of the game to start.
     * @throws RemoteException if there is a communication error during the remote method call.
     */
    void startGame(int connectedGameId) throws RemoteException;
}
