package Networking.ClientInterfaces;

import DataTypes.PlanningPoker;
import DataTypes.User;
import DataTypes.UserRoles.UserRole;

import java.beans.PropertyChangeListener;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The MainClientInterface interface defines all the key methods relating to interfacing the client-side application with the server,
 * as they relate to Planning Poker game creation, validation, update and applying UserRoles to specified users.
 * These methods enable the server and client to communicate with each other, and can be called from either the server or the client.
 * It extends the Remote interface and is thus prepared for network use with RMI.
 */
public interface MainClientInterface extends Remote {

    /**
    * Validates the provided Planning Poker ID by communicating with the server.
    * Fires a property change event if the ID is validated successfully.
    * @param planningPokerID the ID of the Planning Poker game to be validated.
    * @return true if the ID is validated, false otherwise.
    * @throws RemoteException if there is an issue with the remote method call.
     */
    boolean validatePlanningPokerID(int planningPokerID) throws RemoteException;

    /**
     * Method implementation queries the server object for the creation of a new Planning Poker game.
     * If the server successfully creates a game it will return the newly created Planning Poker Game object to the client.<br>
     * Should be called from the client-side connection towards the server. <br>
     * Causes the event 'planningPokerCreatedSuccess' to fire, if creation was successful.
     *
     * @return <code>NULL</code> if the server failed to create a Planning Poker game. Otherwise, returns the Planning Poker object that was created.
     * @throws RemoteException Is thrown if RMI related connection errors occur.
     */
    PlanningPoker createPlanningPoker() throws RemoteException;


    /**
     * Method implementation causes the local client to query the server for the most recent version of the Planning Poker game with the specified planningPokerId.<br>
     * Should be called from the server-side connection towards the local client. <br>
     * Causes the event 'PlanningPokerObjUpdated' to fire, if the server returned a NON-NULL Planning Poker game.
     *
     * @param planningPokerId The unique Planning Poker game id for the Planning Poker game that should be loaded from the server.
     * @throws RemoteException Is thrown if RMI related connection errors occur.
     */
    void updatePlanningPokerObj(int planningPokerId) throws RemoteException;


    /**
     * Queries the server to apply the given UserRole to the given User in the given Planning Poker.
     * If the server properly applied the role to the specified user, it the modified user is returned.
     * Otherwise <code>NULL is returned</code></bode>
     *
     * @param roleToApply       The UserRole that should be applied to the given User.
     * @param planningPokerId   The unique Planning Poker game id for the Planning Poker game inside which the given User should be found and given the specified UserRole.
     * @param userToReceiveRole The User that should have the given UserRole applied.
     * @return The modified User with the specified role applied.
     * @throws RemoteException Is thrown if any error occurs while trying to apply the UserRole to the User in the given game.
     */
    User setRoleOnServer(UserRole roleToApply, int planningPokerId, User userToReceiveRole) throws RemoteException;


    /**
     * <p>Assigns a listener to this subject<br></p>
     *
     * @param listener A PropertyChangeListener, i.e. some code or methods that should be executed when a PropertyChangeEvent is fired.
     * @throws RemoteException If an error relating to the RMI connection is encountered.
     */
    void addPropertyChangeListener(PropertyChangeListener listener) throws RemoteException;


    /**
     * <p>Assigns a listener to this subject<br></p>
     *
     * @param name     A String reference to the event name a specific PropertyChangeEvent.
     * @param listener A PropertyChangeListener, i.e. some code or methods that should be executed when a named specific PropertyChangeEvent is fired.
     * @throws RemoteException If an error relating to the RMI connection is encountered.
     */
    void addPropertyChangeListener(String name, PropertyChangeListener listener) throws RemoteException;


    /**
     * <p>Removes a listener from this subject<br></p>
     *
     * @param listener A PropertyChangeListener, i.e. some code or methods that should be executed when a named specific PropertyChangeEvent is fired.
     * @throws RemoteException If an error relating to the RMI connection is encountered.
     */
    void removePropertyChangeListener(PropertyChangeListener listener) throws RemoteException;


    /**
     * <p>Removes a listener from this subject<br></p>
     *
     * @param name     A String reference to the event name a specific PropertyChangeEvent.
     * @param listener A PropertyChangeListener, i.e. some code or methods that should be executed when a named specific PropertyChangeEvent is fired.
     * @throws RemoteException If an error relating to the RMI connection is encountered.
     */
    void removePropertyChangeListener(String name, PropertyChangeListener listener) throws RemoteException;
}
