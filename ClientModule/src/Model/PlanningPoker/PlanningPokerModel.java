package Model.PlanningPoker;

import DataTypes.User;

public interface PlanningPokerModel
{
  /** Primary initialization method. Should be initialized shortly after any constructor, or run inside a
   * Platform.runLater() method to ensure increase thread safety with javaFx.*/
  void init();

  User getLocalUser();
}
