package Networking.ClientInterfaces;

import DataTypes.PlanningPoker;
import java.rmi.Remote;
import java.beans.PropertyChangeListener;
import java.rmi.RemoteException;

public interface MainClientInterface extends Remote {

  boolean validatePlanningPokerID(String planningPokerID) throws RemoteException;
  PlanningPoker createPlanningPoker() throws RemoteException;
  void addPropertyChangeListener(PropertyChangeListener listener) throws RemoteException;

  void addPropertyChangeListener(String name, PropertyChangeListener listener) throws RemoteException;

  void removePropertyChangeListener(PropertyChangeListener listener) throws RemoteException;

  void removePropertyChangeListener(String name, PropertyChangeListener listener) throws RemoteException;


}
