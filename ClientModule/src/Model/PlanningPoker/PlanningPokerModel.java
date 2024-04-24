package Model.PlanningPoker;

import DataTypes.PlanningPoker;
import DataTypes.User;

import java.rmi.RemoteException;

public interface PlanningPokerModel
{
  /** Primary initialization method. Should be initialized shortly after any constructor, or run inside a
   * Platform.runLater() method to ensure increase thread safety with javaFx.*/
  void init() throws RemoteException;

  User getLocalUser();
}
