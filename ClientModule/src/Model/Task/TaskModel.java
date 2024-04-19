package Model.Task;

import DataTypes.Task;
import Util.PropertyChangeSubject;

import java.rmi.RemoteException;
import java.util.ArrayList;

public interface TaskModel extends PropertyChangeSubject
{
  /** Primary initialization method. Should be initialized shortly after any constructor, or run inside a
   * Platform.runLater() method to ensure increase thread safety with javaFx.*/
  void init();

  ArrayList<Task> getTaskList();

  void addTask(Task task);
}
