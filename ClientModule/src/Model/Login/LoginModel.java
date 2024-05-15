package Model.Login;

import DataTypes.User;
import Util.PropertyChangeSubject;

import java.util.ArrayList;

/** Client-side interface that bridges the UI related elements with the data related elements. */
public interface LoginModel extends PropertyChangeSubject
{
  void setUserList(ArrayList<User> userList);
  ArrayList<User> getUserList();
  void requestLogin(String username, String password);
  void requestCreateUser(String username, String password);
  void requestLogout(String username, String password);

  boolean usernameAlreadyExists(String username);
}
