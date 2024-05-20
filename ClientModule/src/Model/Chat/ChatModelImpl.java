package Model.Chat;

import Application.ClientFactory;
import Application.Session;
import DataTypes.Message;
import DataTypes.User;
import DataTypes.UserRoles.UserRole;
import Model.PlanningPoker.PlanningPokerModelImpl;
import Networking.Client;
import Networking.VoiceChat.VoiceChatClient;
import javafx.application.Platform;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class ChatModelImpl extends PlanningPokerModelImpl implements ChatModel
{
  private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
  private Client clientConnection;
  private VoiceChatClient voiceChat;



  /** Primary constructor. Defers most of the declarations and definitions to the init method,
   * which is run inside a Platform.runLater statement for increased thread safety while using javaFx. */
  public ChatModelImpl() throws RemoteException {
    super();
    super.initialize();

    //Assign the network connection:
    try {
      clientConnection = (Client) ClientFactory.getInstance().getClient();
    } catch (RemoteException e) {
      throw new RuntimeException();
    }

    //Initialize remaining data:
    Platform.runLater(this::init);
  }



  @Override public void init()
  {
    //Assign all PropertyChangeListeners:
    this.assignListeners();
  }

  @Override
  public void sendMessage(Message message, User sender) {
      clientConnection.sendMessage(message, sender);
  }

  @Override
  public void loadUsers() {
    clientConnection.sendUser();
  }

  @Override
  public void removeUserFromSession() {
    clientConnection.removeUserFromSession();
  }

  @Override
  public void setProductOwner(User user) {
    //clientConnection.setProductOwner(user);

    // This method ensures that other connected clients also receive and update about the new product owner, as well as ensuring that the product owner is added to the Planning Poker obj on the server.
    clientConnection.setRoleInGame(UserRole.PRODUCT_OWNER, Session.getConnectedGameId(), user);
  }

  @Override
  public void setScrumMaster(User user) {
    clientConnection.setRoleInGame(UserRole.SCRUM_MASTER, Session.getConnectedGameId(), user);
  }

  @Override public boolean setAdmin(User user, String adminPswd)
  {
    if(adminPswd.equals(super.getActivePlanningPokerGame().getAdminOverridePassword())) {
      clientConnection.setRoleInGame(UserRole.ADMIN, Session.getConnectedGameId(), user);
      return true;
    } else {
      return false;
    }
  }
  @Override
  public void startVoiceCall() {
    this.voiceChat = VoiceChatClient.getInstance((Session.getConnectedGameId() + 5000));
    voiceChat.start();
  }

  @Override
  public void endVoiceCall() {
    voiceChat.stop();
    this.voiceChat = null;
  }


  /** Assigns all the required listeners to the clientConnection allowing for Observable behavior betweeen these classes. */
  private void assignListeners()
  {
    clientConnection.addPropertyChangeListener("messageReceived", evt -> {
      //Sends empty message before every real message, to "clear" the message property in the viewmodel so that the same message can be sent twice in a row.
      propertyChangeSupport.firePropertyChange("messageReceived", null, new Message(""));

      //Here the actual messsage gets sent
      propertyChangeSupport.firePropertyChange("messageReceived", null, evt.getNewValue());
      });

    clientConnection.addPropertyChangeListener("userReceived", evt -> {
      //same as above
      propertyChangeSupport.firePropertyChange("userReceived", null, new ArrayList<User>());
      propertyChangeSupport.firePropertyChange("userReceived", null, evt.getNewValue());
    });

    super.addPropertyChangeListener("PlanningPokerObjUpdated", evt -> {
      propertyChangeSupport.firePropertyChange("userReceived", null, super.getActivePlanningPokerGame().getConnectedUsers());
    });
  }

  @Override public void addPropertyChangeListener(PropertyChangeListener listener) {
    propertyChangeSupport.addPropertyChangeListener(listener);
  }
  @Override public void addPropertyChangeListener(String name, PropertyChangeListener listener) {
    propertyChangeSupport.addPropertyChangeListener(name, listener);
  }
  @Override public void removePropertyChangeListener(PropertyChangeListener listener) {
    propertyChangeSupport.removePropertyChangeListener(listener);
  }
  @Override public void removePropertyChangeListener(String name, PropertyChangeListener listener) {
    propertyChangeSupport.removePropertyChangeListener(name, listener);
  }
}
