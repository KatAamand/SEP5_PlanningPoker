package Model.Game;

import DataTypes.Effort;
import DataTypes.Task;
import DataTypes.UserCardData;
import Networking.ClientConnection_RMI;
import Networking.ServerConnection_RMI;
import Networking.Server_RMI;
import Util.PropertyChangeSubject;
import java.util.ArrayList;
import java.util.Map;

/**
 * The GameServerModel interface defines all the key methods relating to handling the server-side logic of the game phase of each Planning Poker game,
 * this includes applying efforts to tasks, skipping tasks and the playing cards.
 * It extends the PropertyChangeSubject interface and is thus prepared for use with Observer Pattern principles.
 */
public interface GameServerModel extends PropertyChangeSubject
{

    /**
     * Retrieves the list of Effort objects from the local cache.
     * @return an ArrayList<Effort> containing the efforts.
     */
    ArrayList<Effort> getEffortList();


    /**
     * Retrieves the list of Effort objects from the database.
     * @return an ArrayList<Effort> containing the efforts.
     */
    ArrayList<Effort> getEffortListFromDB();


    /** Broadcasts the list of skipped tasks to all the given clients, whom connection can still be established with. If connection cannot be established with a client,
     * this method will forward a request to the server to unregister the problematic client in order to reduce server lag and server crashing.
     * @param clientList A reference to the Map containing the list of connected clients to each Planning Poker game.
     * @param skippedTaskList An ArrayList containing a list of tasks, that each connected client should skip.
     * @param planningPokerId The Game ID for the Planning Poker game, inside which to find connected users and broadcast the skipped task list to.
     * @param server A reference to the server object responsible for removing clients where connectivity issues occur (disconnect, etc.).
     */
    void broadcastListOfSkippedTasksToClients(Map<Integer, ArrayList<ClientConnection_RMI>> clientList, ArrayList<Task> skippedTaskList, int planningPokerId, ServerConnection_RMI server);


    /**
     * Places a card for the user and broadcasts the new card to all connected clients.
     * @param userCardData the data of the card placed by the user.
     * @param connectedClients the list of connected clients.
     * @param server the server connection to handle client communications.
     */
    void placeCard(UserCardData userCardData, ArrayList<ClientConnection_RMI> connectedClients, ServerConnection_RMI server);


    /**
     * Clears all placed cards and notifies all connected clients.
     * @param connectedClients the list of connected clients.
     * @param serverRmi the server connection to handle client communications.
     */
    void clearPlacedCards(ArrayList<ClientConnection_RMI> connectedClients, Server_RMI serverRmi);


    /**
     * Shows all placed cards to all connected clients.
     * @param connectedClients the list of connected clients.
     * @param serverRmi the server connection to handle client communications.
     */
    void showCards(ArrayList<ClientConnection_RMI> connectedClients, Server_RMI serverRmi);


    /**
     * Removes a user from the card map.
     * @param username the username of the user to remove.
     */
    void removeUser(String username);


    /**
     * Requests the server to start the Planning Poker game for the specified game ID and notifies all connected clients.
     * @param connectedGameId the ID of the Planning Poker game to start.
     * @param connectedClients the list of connected clients.
     * @param serverRmi the server connection to handle client communications.
     */
    void requestStartGame(int connectedGameId, ArrayList<ClientConnection_RMI> connectedClients, Server_RMI serverRmi);


    /**
     * Gets the recommended effort based on the placed cards and sends it to all connected clients.
     * @param connectedClients the list of connected clients.
     * @param serverRmi the server connection to handle client communications.
     */
    void getRecommendedEffort(ArrayList<ClientConnection_RMI> connectedClients, Server_RMI serverRmi);


    /**
     * Clears the map of placed cards.
     */
    void clearCardMap();
}
