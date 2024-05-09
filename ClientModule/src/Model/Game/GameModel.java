package Model.Game;

import DataTypes.Effort;
import DataTypes.Task;

import java.util.ArrayList;

public interface GameModel {
  /** Primary initialization method. Should be initialized shortly after any constructor, or run inside a
   * Platform.runLater() method to ensure increase thread safety with javaFx.*/
  void init();
  Task nextTaskToEvaluate();
  ArrayList<Effort> getEffortList();
}
