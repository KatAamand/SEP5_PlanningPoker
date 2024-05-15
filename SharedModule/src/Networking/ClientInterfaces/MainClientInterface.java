package Networking.ClientInterfaces;

import DataTypes.PlanningPoker;
import DataTypes.User;
import DataTypes.UserRoles.UserRole;
import java.rmi.Remote;
import java.beans.PropertyChangeListener;
import java.rmi.RemoteException;

public interface MainClientInterface extends Remote {

  boolean validatePlanningPokerID(int planningPokerID) throws RemoteException;
  PlanningPoker createPlanningPoker() throws RemoteException;
  void addPropertyChangeListener(PropertyChangeListener listener) throws RemoteException;
  void updatePlanningPokerObj(int planningPokerId) throws RemoteException;
  void addPropertyChangeListener(String name, PropertyChangeListener listener) throws RemoteException;

  void removePropertyChangeListener(PropertyChangeListener listener) throws RemoteException;

  void removePropertyChangeListener(String name, PropertyChangeListener listener) throws RemoteException;
  User setRoleOnServer(UserRole roleToApply, int planningPokerId, User userToReceiveRole) throws RemoteException;
  void setRoleInGameFromServer(DataTypes.UserRoles.UserRole userRole, java.lang.String s, DataTypes.User user) throws RemoteException;
}
