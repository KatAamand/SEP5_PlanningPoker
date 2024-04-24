package Model.Game;

import DataTypes.Task;

public interface GameModel {
  /** Primary initialization method. Should be initialized shortly after any constructor, or run inside a
   * Platform.runLater() method to ensure increase thread safety with javaFx.*/
  void init();
  Task nextTaskToEvaluate();
}
