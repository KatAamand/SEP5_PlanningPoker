package Networking.ClientInterfaces;

import Util.PropertyChangeSubject;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LobbyClientInterface extends Remote {
    void startGame(int connectedGameId) throws RemoteException;
}
