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

public interface GameServerModel extends PropertyChangeSubject
{
    ArrayList<Effort> getEffortList();
    ArrayList<Effort> getEffortListFromDB();
    void broadcastListOfSkippedTasksToClients(Map<Integer, ArrayList<ClientConnection_RMI>> clientList, ArrayList<Task> skippedTaskList, int planningPokerId, ServerConnection_RMI server);
    void placeCard(UserCardData userCardData, ArrayList<ClientConnection_RMI> connectedClients, ServerConnection_RMI server);
    void clearPlacedCards(ArrayList<ClientConnection_RMI> connectedClients, Server_RMI serverRmi);
}
