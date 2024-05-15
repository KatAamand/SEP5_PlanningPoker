package Networking.ClientInterfaces;

import DataTypes.Task;

import java.beans.PropertyChangeListener;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface TaskClientInterface extends Remote
{
  void addPropertyChangeListener(PropertyChangeListener listener) throws RemoteException;

  void addPropertyChangeListener(String name, PropertyChangeListener listener) throws RemoteException;

  void removePropertyChangeListener(PropertyChangeListener listener) throws RemoteException;

  void removePropertyChangeListener(String name, PropertyChangeListener listener) throws RemoteException;

  void loadTaskListFromServer(int planningPokerId) throws RemoteException;
  void addTaskToServer(Task task, int planningPokerId) throws RemoteException;
  boolean removeTaskFromServer(Task task, int planningPokerId) throws RemoteException;
  boolean editTaskOnServer(Task oldTask, Task newTask, int planningPokerId) throws RemoteException;
  void broadcastSkipTasksOnServer(ArrayList<Task> skippedTasksList, int planningPokerId) throws RemoteException;
  void updateSkippedTaskList(ArrayList<Task> skippedTasksList) throws RemoteException;
}
