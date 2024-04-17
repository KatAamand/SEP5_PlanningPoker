package Model.Login;

import Util.PropertyChangeSubject;

/** Client-side interface that bridges the UI related elements with the data related elements. */
public interface LoginModel extends PropertyChangeSubject
{
  /** Primary initialization method. Should be initialized shortly after any constructor, or run inside a
   * Platform.runLater() method to ensure increase thread safety with javaFx.*/
  void init();
  void requestLogin(String username, String password);
  void requestCreateUser(String username, String password);
}
