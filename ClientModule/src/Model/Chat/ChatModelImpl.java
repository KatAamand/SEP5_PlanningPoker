package Model.Chat;

import Application.ClientFactory;
import DataTypes.Message;
import DataTypes.User;
import Networking.Client;
import javafx.application.Platform;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.rmi.RemoteException;

public class ChatModelImpl implements ChatModel
{
  private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

  private Client clientConnection;



  /** Primary constructor. Defers most of the declarations and definitions to the init method,
   * which is run inside a Platform.runLater statement for increased thread safety while using javaFx. */
  public ChatModelImpl() {
    //Assign the network connection:
    try
    {
      clientConnection = (Client) ClientFactory.getInstance().getClient();
    }
    catch (RemoteException e)
    {
      //TODO: Properly handle this error!
      e.printStackTrace();
    }

    //Initialize remaining data:
    Platform.runLater(this::init);
  }



  @Override public void init()
  {
    //TODO Initialize relevant data that might affect the javaFx thread here.


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
    clientConnection.setProductOwner(user);
  }


  /** Assigns all the required listeners to the clientConnection allowing for Observable behavior betweeen these classes. */
  private void assignListeners()
  {
    //TODO define the listeners that should be added to the Client here.
    clientConnection.addPropertyChangeListener("messageReceived", evt -> {
      //Sends empty message before every real message, to "clear" the message property in the viewmodel so that the same message can be sent twice in a row.
      propertyChangeSupport.firePropertyChange("messageReceived", null, new Message(""));

      //Here the actual messsage gets sent
      propertyChangeSupport.firePropertyChange("messageReceived", null, evt.getNewValue());
      });

    clientConnection.addPropertyChangeListener("userReceived", evt -> {
      propertyChangeSupport.firePropertyChange("userReceived", null, evt.getNewValue());
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
