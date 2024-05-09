package Networking.ClientInterfaces;

import DataTypes.Task;

import java.beans.PropertyChangeListener;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TaskClientInterface extends Remote
{
  void addPropertyChangeListener(PropertyChangeListener listener) throws RemoteException;

  void addPropertyChangeListener(String name, PropertyChangeListener listener) throws RemoteException;

  void removePropertyChangeListener(PropertyChangeListener listener) throws RemoteException;

  void removePropertyChangeListener(String name, PropertyChangeListener listener) throws RemoteException;

  void loadTaskListFromServer(String gameId) throws RemoteException;
  void addTaskToServer(Task task, String gameId) throws RemoteException;
  boolean removeTaskFromServer(Task task, String gameId) throws RemoteException;
  boolean editTaskOnServer(Task oldTask, Task newTask, String gameId) throws RemoteException;
}
