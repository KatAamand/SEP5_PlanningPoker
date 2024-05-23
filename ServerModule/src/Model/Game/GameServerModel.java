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

    // TODO: Source code comment/document
    ArrayList<Effort> getEffortList();


    // TODO: Source code comment/document
    ArrayList<Effort> getEffortListFromDB();


    /** Broadcasts the list of skipped tasks to all the given clients, whom connection can still be established with. If connection cannot be established with a client,
     * this method will forward a request to the server to unregister the problematic client in order to reduce server lag and server crashing.
     * @param clientList A reference to the Map containing the list of connected clients to each Planning Poker game.
     * @param skippedTaskList An ArrayList containing a list of tasks, that each connected client should skip.
     * @param planningPokerId The Game ID for the Planning Poker game, inside which to find connected users and broadcast the skipped task list to.
     * @param server A reference to the server object responsible for removing clients where connectivity issues occur (disconnect, etc.).
     */
    void broadcastListOfSkippedTasksToClients(Map<Integer, ArrayList<ClientConnection_RMI>> clientList, ArrayList<Task> skippedTaskList, int planningPokerId, ServerConnection_RMI server);


    // TODO: Source code comment/document
    void placeCard(UserCardData userCardData, ArrayList<ClientConnection_RMI> connectedClients, ServerConnection_RMI server);


    // TODO: Source code comment/document
    void clearPlacedCards(ArrayList<ClientConnection_RMI> connectedClients, Server_RMI serverRmi);


    // TODO: Source code comment/document
    void showCards(ArrayList<ClientConnection_RMI> connectedClients, Server_RMI serverRmi);


    // TODO: Source code comment/document
    void removeUser(String username);


    // TODO: Source code comment/document
    void requestStartGame(int connectedGameId, ArrayList<ClientConnection_RMI> connectedClients, Server_RMI serverRmi);


    // TODO: Source code comment/document
    void getRecommendedEffort(ArrayList<ClientConnection_RMI> connectedClients, Server_RMI serverRmi);


    // TODO: Source code comment/document
    void clearCardMap();
}
