package Networking.ClientInterfaces;


import DataTypes.UserCardData;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GameClientInterface extends Remote {
    void receivePlacedCard(UserCardData userCardData) throws RemoteException;
    void clearPlacedCards() throws RemoteException;

    void showCards() throws RemoteException;

    void receiveRecommendedEffort(String recommendedEffort) throws RemoteException;
}
