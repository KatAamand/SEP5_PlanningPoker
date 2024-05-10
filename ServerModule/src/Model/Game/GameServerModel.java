package Model.Game;

import DataTypes.Effort;
import DataTypes.Task;
import Networking.ClientConnection_RMI;
import Networking.ServerConnection_RMI;
import Util.PropertyChangeSubject;

import java.util.ArrayList;
import java.util.Map;

public interface GameServerModel extends PropertyChangeSubject
{
    ArrayList<Effort> getEffortList();

    ArrayList<Effort> getEffortListFromDB();
    void broadcastListOfSkippedTasksToClients(Map<String, ArrayList<ClientConnection_RMI>> clientList, ArrayList<Task> skippedTaskList, String gameId, ServerConnection_RMI server);
}
