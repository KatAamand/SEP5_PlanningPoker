package Networking.ClientInterfaces;


import DataTypes.UserCardData;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GameClientInterface extends Remote {

    /**
     * Receives the placed card data from the server.
     * @param userCardData the data of the card placed by the user.
     * @throws RemoteException if there is a communication error during the remote method call.
     */
    void receivePlacedCard(UserCardData userCardData) throws RemoteException;

    /**
     * Clears all placed cards.
     * @throws RemoteException if there is a communication error during the remote method call.
     */
    void clearPlacedCards() throws RemoteException;


    /**
     * Displays all placed cards.
     * @throws RemoteException if there is a communication error during the remote method call.
     */
    void showCards() throws RemoteException;


    /**
     * Receives the recommended effort from the server.
     * @param recommendedEffort the recommended effort as a string.
     * @throws RemoteException if there is a communication error during the remote method call.
     */
    void receiveRecommendedEffort(String recommendedEffort) throws RemoteException;

    /**
     * Starts the timer for the game.
     * @throws RemoteException if there is a communication error during the remote method call.
     */
    void startTimer() throws RemoteException;
}
