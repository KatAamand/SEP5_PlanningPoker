package Model.Chat;

import DataTypes.Message;
import DataTypes.User;
import Util.PropertyChangeSubject;

/**
 * ChatModel defines the interface to the model/class which is responsible for handling the assignment of roles to connected users, as well as the text chat and voice chat.
 * It extends the PropertyChangeSubject interface and is thus prepared for use with Observer Pattern principles.
 */
public interface ChatModel extends PropertyChangeSubject {


  /** Primary initialization method of interfaced class. Intended for use in a JavaFx environment
   * where certain operations must be run on the javaFx thread in a Platform.runLater(() ->) method
   * in order to avoid errors and increase application stability.
   */
  void init();


  /** This method is responsible for sending a message from the local user to the server, which then broadcasts the message to the rest of the planning poker session */
  void sendMessage(Message message, User sender);


  /** This method requests the userlist for hte current session of planning poker from the server */
  void loadUsers();


  /** This method sets the product owner of the planning poker session from which it is sent
   * @param user the user which is intended to become product owner */
  void setProductOwner(User user);


  /** This method sets the scrum master of the planning poker session from which it is sent
   * @param user the user which is intended to become scrum master */
  void setScrumMaster(User user);


  /** This method starts the voice chat client for the current planning poker session*/
  void startVoiceCall();


  /** This method stops the voice chat client for the current planning poker session */
  void endVoiceCall();


  /** Applies admin privileges to the current Local User, if the given 'Admin Password' is valid.
   * @param user The user to which the admin privileges should be assigned.
   * @param adminPswd The administrator override password. <br>As of 22/05-24, this password is hard-coded as "admin".
   * @return True, if admin privileges were properly applied to the Local user. False, if they were not. Example: Returns false if the given password is invalid.
   */
  boolean setAdmin(User user, String adminPswd);
}
