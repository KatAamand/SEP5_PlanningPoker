package Model.Task;

import DataTypes.Task;
import Util.PropertyChangeSubject;
import java.util.ArrayList;

public interface TaskServerModel extends PropertyChangeSubject
{
  void addTask(Task task, String gameId);

  void removeTask(Task task, String gameId);

  ArrayList<Task> getTaskList(String gameId);
}
