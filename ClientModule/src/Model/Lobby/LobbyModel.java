package Model.Lobby;

import Util.PropertyChangeSubject;

/**
 * LobbyModel defines the interface to the model/class which is responsible for handling a small portion of the Planning Poker game flow.
 * The lobbyModel handles the logic relating to the initial creation and setup of the Planning Poker game, this mainly being adding Tasks to later estimate efforts for.
 * It extends the PropertyChangeSubject interface and is thus prepared for use with Observer Pattern principles.
 */
public interface LobbyModel extends PropertyChangeSubject
{
  /** Primary initialization method of interfaced class. Intended for use in a JavaFx environment
   * where certain operations must be run on the javaFx thread in a Platform.runLater(() ->) method
   * in order to avoid errors and increase application stability.
   */
  void init();
}
