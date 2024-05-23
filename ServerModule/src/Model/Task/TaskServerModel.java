package Model.Task;

import DataTypes.Task;
import Networking.ClientConnection_RMI;
import Networking.ServerConnection_RMI;
import Util.PropertyChangeSubject;
import java.util.ArrayList;
import java.util.Map;

public interface TaskServerModel extends PropertyChangeSubject
{
  void addTask(Task task, int planningPokerId);
  boolean removeTask(Task task, int planningPokerId);
  boolean editTask(Task task, Task updatedTask, int planningPokerId);
  ArrayList<Task> getTaskList(int planningPokerId);
  ArrayList<Task> getTaskListFromDB(int planningPokerId);
  void broadcastTaskListUpdate(Map<Integer, ArrayList<ClientConnection_RMI>> clientList, ServerConnection_RMI server, int planningPokerId);
}
