package Model.Task;

import DataTypes.Task;
import Util.PropertyChangeSubject;
import java.util.ArrayList;

/**
 * TaskModel defines the interface to the model which is responsible for task related methods and data in the context of a Planning Poker game.
 * It extends the PropertyChangeSubject interface and is thus prepared for use with Observer Pattern principles.
 */
public interface TaskModel extends PropertyChangeSubject
{
  /** Primary initialization method of interfaced class. Intended for use in a JavaFx environment
   * where certain operations must be run on the javaFx thread in a Platform.runLater(() ->) method
   * in order to avoid errors and increase application stability.
   */
  void init();

  /** Returns a List reference to the Planning Poker tasks associated with the connected Planning Poker game.
   * @return an ArrayList<Task> containing all the tasks associated with the connected Planning Poker game.
   */
  ArrayList<Task> getTaskList();

  /** Adds a Planning Poker task to the currently connected Planning Poker game.
   * @param task The task to add to the currently connected Planning Poker game.
   */
  void addTask(Task task);

  /** Removes a Planning Poker task from the currently connected Planning Poker game.
   * @param task The task to remove from the currently connected Planning Poker game.
   * @return True if the task was successfully removed. False if removal for some reason failed.
   */
  boolean removeTask(Task task);

  /** Edits a Planning Poker task in the currently connected Planning Poker game. The implementation uses the uneditedTask
   * as the reference to find which task in the task list to manipulate and then replaces this unedited task with the editedTask.
   * @param uneditedTask A reference to, or copy of, the original task that is intended to be modified or edited.
   * @param editedTask A reference to, or copy of, the modified task that is intended to replace the original task in the task list.
   * @return True if the task was successfully edited. False if editing for some reason failed.
   */
  boolean editTask(Task uneditedTask, Task editedTask);
}
