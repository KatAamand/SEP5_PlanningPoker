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


  // TODO: Source code comment/document
  void sendMessage(Message message, User sender);


  // TODO: Source code comment/document
  void loadUsers();


  //void removeUserFromSession(); //TODO: This method is never used. It should be removed.


  // TODO: Source code comment/document
  void setProductOwner(User user);


  // TODO: Source code comment/document
  void setScrumMaster(User user);


  // TODO: Source code comment/document
  void startVoiceCall();


  // TODO: Source code comment/document
  void endVoiceCall();


  /** Applies admin privileges to the current Local User, if the given 'Admin Password' is valid.
   * @param user The user to which the admin privileges should be assigned.
   * @param adminPswd The administrator override password. <br>As of 22/05-24, this password is hard-coded as "admin".
   * @return True, if admin privileges were properly applied to the Local user. False, if they were not. Example: Returns false if the given password is invalid.
   */
  boolean setAdmin(User user, String adminPswd);
}
