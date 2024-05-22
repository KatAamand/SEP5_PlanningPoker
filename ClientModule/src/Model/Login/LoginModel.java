package Model.Login;

import DataTypes.User;
import Util.PropertyChangeSubject;

import java.util.ArrayList;

/**
 * LoginModel defines the interface to the model which is responsible for handling user creation and user login, as well as the data logic bridging the LoginView to the MainView.
 * It extends the PropertyChangeSubject interface and is thus prepared for use with Observer Pattern principles.
 */
public interface LoginModel extends PropertyChangeSubject {
  // TODO: Source code comment/document
  void setUserList(ArrayList<User> userList);

  // TODO: Source code comment/document
  ArrayList<User> getUserList();

  // TODO: Source code comment/document
  void requestLogin(String username, String password);


  // TODO: Source code comment/document
  void requestCreateUser(String username, String password);


  // TODO: Source code comment/document
  void requestLogout(String username, String password);


  // TODO: Source code comment/document
  boolean usernameAlreadyExists(String username);
}
