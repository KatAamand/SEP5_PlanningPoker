package Model.MainView;

import DataTypes.PlanningPoker;
import Util.PropertyChangeSubject;

import java.rmi.RemoteException;

public interface MainModel extends PropertyChangeSubject
{
  /** Primary initialization method. Should be initialized shortly after any constructor, or run inside a
   * Platform.runLater() method to ensure increase thread safety with javaFx.*/
  void init();
  void requestCreatePlanningPokerID() throws RemoteException;
  void requestConnectPlanningPoker(String planningPokerID) throws RemoteException;
}
