package Model.Lobby;

import Util.PropertyChangeSubject;

public interface LobbyModel extends PropertyChangeSubject
{
  /** Primary initialization method. Should be initialized shortly after any constructor, or run inside a
   * Platform.runLater() method to ensure increase thread safety with javaFx.*/
  void init();
}
