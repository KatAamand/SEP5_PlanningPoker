package Model.Task;

import DataTypes.Task;
import Networking.ClientConnection_RMI;
import Networking.ServerConnection_RMI;
import Util.PropertyChangeSubject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface TaskServerModel extends PropertyChangeSubject
{
  void addTask(Task task, String gameId);
  boolean removeTask(Task task, String gameId);
  boolean editTask(Task oldTask, Task newTask, String gameId);
  ArrayList<Task> getTaskList(String gameId);
  void broadcastTaskListUpdate(Map<String, ArrayList<ClientConnection_RMI>> clientList, ServerConnection_RMI server, String gameId);
}
