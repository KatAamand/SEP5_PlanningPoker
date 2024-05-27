package Networking.ClientInterfaces;


import DataTypes.UserCardData;
import java.beans.PropertyChangeListener;
import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * The GameClientInterface interface defines the key methods relating to interfacing the client-side application with the server,
 * as they relate to interactions in the Game / Estimation phase of the Planning Poker game. Task and chat related details are handled in their own separate implementations.
 * These methods enable the server and client to communicate with each other, and can be called from either the server or the client.
 * It extends the Remote interface and is thus prepared for network use with RMI.
 */
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


    /**<p>Assigns a listener to this subject<br></p>
     * @param listener A PropertyChangeListener, i.e. some code or methods that should be executed when a PropertyChangeEvent is fired.
     * @throws RemoteException If an error relating to the RMI connection is encountered.
     */
    void addPropertyChangeListener(PropertyChangeListener listener) throws RemoteException;


    /**<p>Assigns a listener to this subject<br></p>
     * @param name A String reference to the event name a specific PropertyChangeEvent.
     * @param listener A PropertyChangeListener, i.e. some code or methods that should be executed when a named specific PropertyChangeEvent is fired.
     * @throws RemoteException If an error relating to the RMI connection is encountered.
     */
    void addPropertyChangeListener(String name, PropertyChangeListener listener) throws RemoteException;


    /**<p>Removes a listener from this subject<br></p>
     * @param listener A PropertyChangeListener, i.e. some code or methods that should be executed when a named specific PropertyChangeEvent is fired.
     * @throws RemoteException If an error relating to the RMI connection is encountered.
     */
    void removePropertyChangeListener(PropertyChangeListener listener) throws RemoteException;


    /**<p>Removes a listener from this subject<br></p>
     * @param name A String reference to the event name a specific PropertyChangeEvent.
     * @param listener A PropertyChangeListener, i.e. some code or methods that should be executed when a named specific PropertyChangeEvent is fired.
     * @throws RemoteException If an error relating to the RMI connection is encountered.
     */
    void removePropertyChangeListener(String name, PropertyChangeListener listener) throws RemoteException;
}
