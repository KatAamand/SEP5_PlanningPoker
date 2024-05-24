package Model.Task;

import DataTypes.Task;
import Networking.ClientConnection_RMI;
import Networking.ServerConnection_RMI;
import Util.PropertyChangeSubject;
import java.util.ArrayList;
import java.util.Map;


/**
 * The TaskServerModel interface defines all the key methods relating to handling the server-side logic of managing tasks inside each Planning Poker game.
 * It extends the PropertyChangeSubject interface and is thus prepared for use with Observer Pattern principles.
 */
public interface TaskServerModel extends PropertyChangeSubject
{

  /**
   * Adds the specified Task to the Planning Poker game with the specified game id, if such a game exists.
   * @param task The Task that should be added to the specified Planning Poker game.
   * @param planningPokerId The Game ID for the Planning Poker game, to which the specified Task should be added.
   */
  void addTask(Task task, int planningPokerId);


  /**
   * Removes the specified Task from the Planning Poker game with the specified game id, if such a game exists.
   * @param task The Task that should be removed.
   * @param planningPokerId The Game ID for the Planning Poker game, from which the specified Task should be removed.
   * @return True, if the specified Task was successfully removed. False, if it failed or the specified game does not exist.
   */
  boolean removeTask(Task task, int planningPokerId);


  /**
   * Edits the specified Task in the Planning Poker game with the specified game id, if such a game exists.
   * This method requires that both the un-modified Task and the modified Task are specified.
   * The method finds the un-modified task in the specified Planning Poker games list of tasks, and replaces this Task with the modified one.
   * This approach allows for the Task ordering / priority to remain unchanged, so modified tasks are not continuously appended to the end of the task list.
   * @param task The un-modified task, which should be found and replaced.
   * @param updatedTask The modified task, which should replace the un-modified one.
   * @param planningPokerId The Game ID for the Planning Poker game, from which the specified Task should be edited.
   * @return True, if the specified Task was successfully edited. False, if it failed, or the specified game does not exist.
   */
  boolean editTask(Task oldTask, Task updatedTask, int planningPokerId);


  /**
   * Returns a List of all the Task objects in the Planning Poker game with the specified game id, if such a game exists.
   * @param planningPokerId The Game ID for the Planning Poker game, from which to load all current Tasks.
   * @return An ArrayList containing all the current Tasks associated with the Planning Poker game with the specified game id.
   */
  ArrayList<Task> getTaskList(int planningPokerId);


  // TODO: Source code comment/document
  ArrayList<Task> getTaskListFromDB(int planningPokerId);


  /** Broadcasts to all connected clients in the specified Planning Poker game, that a change to the task list was made and that all connected clients should re-load their currently active task lists. <br>
   * If connection cannot be established with a client,this method will forward a request to the server to unregister the problematic client in order to reduce server lag and server crashing.
   * @param clientList A reference to the Map containing the list of connected clients to each Planning Poker game.
   * @param planningPokerId The Game ID for the Planning Poker game, inside which to find connected users and broadcast the skipped task list to.
   * @param server A reference to the server object responsible for removing clients where connectivity issues occur (disconnect, etc.).
   */
  void broadcastTaskListUpdate(Map<Integer, ArrayList<ClientConnection_RMI>> clientList, ServerConnection_RMI server, int planningPokerId);
}
