package Model.Task;

import DataTypes.Task;
import Util.PropertyChangeSubject;

import java.util.ArrayList;

public interface TaskServerModel extends PropertyChangeSubject
{
  void addTask(Task task);

  void removeTask(Task task);

  Task getTask(int index);

  //TODO: Should be refactored and become session dependent, instead of just returning all tasks in the entire database...
  ArrayList<Task> getTaskList();
}
