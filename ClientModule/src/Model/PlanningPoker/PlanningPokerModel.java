package Model.PlanningPoker;

import DataTypes.PlanningPoker;
import DataTypes.User;
import Util.PropertyChangeSubject;
import java.rmi.RemoteException;

/**
 * PlanningPokerModel defines the interface to the model which is responsible for high level Planning Poker game integration related methods and data in the context of a Planning Poker game.
 * It extends the PropertyChangeSubject interface and is thus prepared for use with Observer Pattern principles.
 * This interface is not intended to manage the more detailed aspects of the Planning Poker Game, such as the Chat, the Tasks, the Lobby or the Game itself.
 * It is instead mainly used to provide integration between these sub-elements of the game, as well as providing local storage of the Planning Poker game itself from which sub-elements
 * can query the game data.
 */
public interface PlanningPokerModel extends PropertyChangeSubject
{
  /** Primary initialization method of interfaced class. Intended for use in a JavaFx environment
   * where certain operations must be run on the javaFx thread in a Platform.runLater(() ->) method
   * in order to avoid errors and increase application stability.
   * @throws RemoteException Is thrown when an issue occurs relating to the Server / Client connection.
   */
  void initialize() throws RemoteException;


  /** Returns a reference to the local user. The implementation works in conjunction with a local static Session object from which
   * this method gets the currently set local user for this local client.
   * @return a reference to the local User object
   */
  User getLocalUser();


  /** Returns a reference to the currently active Planning Poker game. The local game data is stored locally inside the class implementing this interface.
   * @return a PlanningPoker object containing the most updated/recent Planning Poker game data saved on this local client. Returns <code>NULL</code> if
   * the local client is not connected to any ongoing Planning Poker games.
   */
  PlanningPoker getActivePlanningPokerGame();


  /** Sets the local reference to the currently active Planning Poker game. The local game data is stored locally inside the class implementing this interface.
   * @param activeGame a PlanningPoker object
   */
  void setActivePlanningPokerGame(PlanningPoker activeGame);


  /** Method to confirm that the local user is assigned the proper role as well as having the proper permissions relating to this role, in relation to the included PlanningPoker game parameter.
   * Simple compares the local user to the users assigned to the Planning Poker game object, and ensures that role and permission
   * changes that might have originated on a foreign client / server are properly applied to the local User. Local user will end up with the permissions and roles determined in the PlanningPoker object,
   * if there are any differences.
   * @param game PlanningPoker object to compare the local Users permissions and roles against.
   */
  void confirmLocalUserHasProperRoleAndPermissions(PlanningPoker game);


  /** Resets all user role related permissions when the local client leaves the currently active planning poker game.
   */
  void resetUserPermissionUponLeavingGame();


  /**
   * Requests to start the Planning Poker game.
   * This method signals the server to begin the game for all connected clients.
   */
  void requestStartGame();
}
