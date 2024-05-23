package Model.MainView;

import Util.PropertyChangeSubject;
import java.rmi.RemoteException;

/**
 * MainModel defines the interface to the model which is responsible for the main application view, including handling creation and join requests to planning poker games.
 * It extends the PropertyChangeSubject interface and is thus prepared for use with Observer Pattern principles.
 */
public interface MainModel extends PropertyChangeSubject {

  /** Primary initialization method of interfaced class. Intended for use in a JavaFx environment
   * where certain operations must be run on the javaFx thread in a Platform.runLater(() ->) method
   * in order to avoid errors and increase application stability.
   */
  void init();


  // TODO: Source code comment/document
  void requestCreatePlanningPokerID() throws RemoteException;

  /**
   * Requests to connect to an existing Planning Poker game with the specified planningPoker ID.
   * This method validates the planningPoker ID and assigns a role to the current user based on the existing roles in the game.
   * @param planningPokerID the ID of the Planning Poker game to connect to.
   * @throws RemoteException if there is an issue with the remote method invocation.
   */
  void requestConnectPlanningPoker(int planningPokerID) throws RemoteException;
}
