package Model.MainView;

import Util.PropertyChangeSubject;

public interface MainModel extends PropertyChangeSubject
{
  /** Primary initialization method. Should be initialized shortly after any constructor, or run inside a
   * Platform.runLater() method to ensure increase thread safety with javaFx.*/
  void init();

  void requestCreatePlanningPokerID();
  void requestConnectPlanningPoker(String planningPokerID);


}
