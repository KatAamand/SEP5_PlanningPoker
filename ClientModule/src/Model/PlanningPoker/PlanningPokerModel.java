package Model.PlanningPoker;

import DataTypes.PlanningPoker;
import DataTypes.User;
import Util.PropertyChangeSubject;

import java.rmi.RemoteException;

public interface PlanningPokerModel extends PropertyChangeSubject
{
  /** Primary initialization method. Should be initialized shortly after any constructor, or run inside a
   * Platform.runLater() method to ensure increase thread safety with javaFx.*/
  void initialize() throws RemoteException;

  User getLocalUser();
  PlanningPoker getActivePlanningPokerGame();
  void removeUserFromSession();
  void resetUserPermissionUponLeavingGame();
}
