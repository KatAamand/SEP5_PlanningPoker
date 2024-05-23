package Networking.ClientInterfaces;

import DataTypes.Task;
import java.beans.PropertyChangeListener;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * The TaskClientInterface interface defines all the key methods relating to interfacing the client-side application with the server as they relate to the Tasks associated with each Planning Poker Game.
 * These methods enable the server and client to communicate with each other, and can be called from either the server or the client.
 * It extends the Remote interface and is thus prepared for network use with RMI.
 */
public interface TaskClientInterface extends Remote
{

  /** Causes the Local client where this method is implemented, to update the local Planning Poker object with the most recent list of tasks.
   * It is mainly called from the server towards the client.
   * @param planningPokerId The Game ID for the Planning Poker game, to which the specified Task should be removed.
   * @throws RemoteException If an error relating to the RMI connection is encountered.
   */
  void loadTaskListFromServer(int planningPokerId) throws RemoteException;


  /**
   * Attempts to add the specified Task to the Planning Poker game with the specified game id, if such a game exists.
   * The implementation should query the proper method directly at the server.
   * @param task The Task that should be added to the specified Planning Poker game.
   * @param planningPokerId The Game ID for the Planning Poker game, to which the specified Task should be added.
   * @throws RemoteException If an error relating to the RMI connection is encountered.
   */
  void addTaskToServer(Task task, int planningPokerId) throws RemoteException;


  /**
   * Attempts to remove the specified Task from the Planning Poker game with the specified game id, if such a game exists.
   * The implementation should query the proper method directly at the server.
   * @param task The Task that should be removed from the specified Planning Poker game.
   * @param planningPokerId The Game ID for the Planning Poker game, to which the specified Task should be removed.
   * @return True, if the Task was successfully removed from the specified game. False, if not.
   * @throws RemoteException If an error relating to the RMI connection is encountered.
   */
  boolean removeTaskFromServer(Task task, int planningPokerId) throws RemoteException;


  /**
   * Attempts to edit the specified Task in the Planning Poker game with the specified game id, if such a game exists. The implementation should query the proper method directly at the server.
   * This method requires that both the un-modified Task and the modified Task are specified.
   * The method finds the un-modified task in the specified Planning Poker games list of tasks, and replaces this Task with the modified one.
   * This approach allows for the Task ordering / priority to remain unchanged, so modified tasks are not continuously appended to the end of the task list.
   * @param oldTask The un-modified task, which should be found and replaced.
   * @param newTask The modified task, which should replace the un-modified one.
   * @param planningPokerId The Game ID for the Planning Poker game, from which the specified Task should be edited.
   * @return True, if the specified Task was successfully edited. False, if it failed, or the specified game does not exist.
   * @throws RemoteException If an error relating to the RMI connection is encountered.
   */
  boolean editTaskOnServer(Task oldTask, Task newTask, int planningPokerId) throws RemoteException;


  /** Prompts the server to broadcast the specified list of skipped tasks to all clients who are currently
   * connected to the Planning Poker game specified by the given planningPokerId. It is mainly called from the client towards the server.
   * @param skippedTasksList An ArrayList containing a list of tasks, that each connected client should skip.
   * @param planningPokerId The Game ID for the Planning Poker game, inside which to find connected users and broadcast the skipped task list to.
   * @throws RemoteException If an error relating to the RMI connection is encountered.
   */
  void broadcastSkipTasksOnServer(ArrayList<Task> skippedTasksList, int planningPokerId) throws RemoteException;


  /** Causes the Local client where this method is implemented, to update the local Planning Poker object with the most recent list of skipped tasks.
   * It is mainly called from the server towards the client.
   * @param skippedTasksList An ArrayList containing a list of tasks, that the local client should skip.
   * @throws RemoteException If an error relating to the RMI connection is encountered.
   */
  void updateSkippedTaskList(ArrayList<Task> skippedTasksList) throws RemoteException;


  /**<p>Assigns a listener to this subject<br></p>
   * @param listener A PropertyChangeListener, i.e. some code or methods that should be executed when a PropertyChangeEvent is fired.
   * @throws RemoteException If an error relating to the RMI connection is encountered.
   */
  void addPropertyChangeListener(PropertyChangeListener listener) throws RemoteException;


  /**<p>Assigns a listener to this subject<br></p>
   * @param name A String reference to the event name a specific PropertyChangeEvent.
   * @param listener A PropertyChangeListener, i.e. some code or methods that should be executed when a named specific PropertyChangeEvent is fired.
   * @throws RemoteException If an error relating to the RMI connection is encountered.
   */
  void addPropertyChangeListener(String name, PropertyChangeListener listener) throws RemoteException;


  /**<p>Removes a listener from this subject<br></p>
   * @param listener A PropertyChangeListener, i.e. some code or methods that should be executed when a named specific PropertyChangeEvent is fired.
   * @throws RemoteException If an error relating to the RMI connection is encountered.
   */
  void removePropertyChangeListener(PropertyChangeListener listener) throws RemoteException;


  /**<p>Removes a listener from this subject<br></p>
   * @param name A String reference to the event name a specific PropertyChangeEvent.
   * @param listener A PropertyChangeListener, i.e. some code or methods that should be executed when a named specific PropertyChangeEvent is fired.
   * @throws RemoteException If an error relating to the RMI connection is encountered.
   */
  void removePropertyChangeListener(String name, PropertyChangeListener listener) throws RemoteException;
}
