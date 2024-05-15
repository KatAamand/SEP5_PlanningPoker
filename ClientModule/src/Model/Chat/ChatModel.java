package Model.Chat;

import DataTypes.Message;
import DataTypes.User;
import Util.PropertyChangeSubject;

public interface ChatModel extends PropertyChangeSubject {
  /** Primary initialization method. Should be initialized shortly after any constructor, or run inside a
   * Platform.runLater() method to ensure increase thread safety with javaFx.*/
  void init();
  void sendMessage(Message message, User sender);

    void loadUsers();

  void removeUserFromSession();

    void setProductOwner(User user);
}
