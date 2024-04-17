package Model.Login;

import Util.PropertyChangeSubject;

/** Client-side interface that bridges the UI related elements with the data related elements. */
public interface LoginModel extends PropertyChangeSubject
{
  void requestLogin(String username, String password);
  void requestCreateUser(String username, String password);
}
