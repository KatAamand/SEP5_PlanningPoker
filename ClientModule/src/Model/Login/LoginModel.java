package Model.Login;

import DataTypes.User;
import Util.PropertyChangeSubject;

import java.util.ArrayList;

/**
 * LoginModel defines the interface to the model which is responsible for handling user creation and user login, as well as the data logic bridging the LoginView to the MainView.
 * It extends the PropertyChangeSubject interface and is thus prepared for use with Observer Pattern principles.
 */
public interface LoginModel extends PropertyChangeSubject {

  /**
   * Sets the list of users.
   * @param userList the list of users to set.
   */
  void setUserList(ArrayList<User> userList);

  /**
   * Returns the list of users.
   * @return an ArrayList<User> containing all the users.
   */
  ArrayList<User> getUserList();

  /**
   * Requests to log in a user with the specified username and password.
   * @param username the username of the user attempting to log in.
   * @param password the password of the user attempting to log in.
   */
  void requestLogin(String username, String password);

  /**
   * Requests to create a new user with the specified username and password.
   * @param username the desired username for the new user.
   * @param password the desired password for the new user.
   */
  void requestCreateUser(String username, String password);

  /**
   * Requests to log out a user with the specified username and password.
   * @param username the username of the user attempting to log out.
   * @param password the password of the user attempting to log out.
   */
  void requestLogout(String username, String password);

  /**
   * Checks if a username already exists in the user list.
   * @param username the username to check for existence.
   * @return true if the username already exists, false otherwise.
   */
  boolean usernameAlreadyExists(String username);
}
