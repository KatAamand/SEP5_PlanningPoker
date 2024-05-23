package Networking.ClientInterfaces;

import java.beans.PropertyChangeListener;
import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * The LobbyClientInterface interface defines the key methods relating to interfacing the client-side application with the server,
 * as they relate to interactions in the Lobby / Setup phase of the Planning Poker game. Task and chat related details are handled in their own separate implementations.
 * These methods enable the server and client to communicate with each other, and can be called from either the server or the client.
 * It extends the Remote interface and is thus prepared for network use with RMI.
 */
public interface LobbyClientInterface extends Remote {


    // TODO: Source code comment/document
    void startGame(int connectedGameId) throws RemoteException;


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
