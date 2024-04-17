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

  void loadTaskList() throws RemoteException;
  void addTask(Task task) throws RemoteException;
}
