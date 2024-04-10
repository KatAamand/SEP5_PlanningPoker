package Model;

import Application.ClientFactory;
import Networking.ClientConnection_RMI;

import DataTypes.User;
import Networking.Client_RMI;
import javafx.application.Platform;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.rmi.RemoteException;

/** Implementation of the client side model. This class handles data related logical operations on the client side,
 * and queries server related data through the Client_RMI network class.*/
public class ClientModel_Impl implements ClientModel
{
  private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
  private Client_RMI clientConnection;
  private User localUser;
  private Session session;



  /** Primary constructor. Defers most of the declarations and definitions to the init method,
   * which is run inside a Platform.runLater statement for increased thread safety while using javaFx. */
  public ClientModel_Impl(/* ???Any relevant attributes??? */)
  {
    Platform.runLater(this::init);
  }



  @Override public void init(/* ???Any relevant attributes??? */)
  {
    //Assign the network connection:
    try
    {
      clientConnection = (Client_RMI) ClientFactory.getInstance().getClient();
    }
    catch (RemoteException e)
    {
      //TODO: Properly handle this error!
      e.printStackTrace();
    }

    //TODO Initialize relevant data that might affect the javaFx thread here.


    //Assign all PropertyChangeListeners:
    this.assignListeners();
  }




  @Override public void sendMessage(String message)
  {
    //TODO
  }




  /** Assigns all the required listeners to the clientConnection allowing for Observable behavior betweeen these classes. */
  private void assignListeners()
  {
    //TODO define the listerners that should be added to the Client here.
    //Example:
    clientConnection.addPropertyChangeListener("DataChanged", evt -> {
      System.out.println("This is an example");});
    //End of example
  }





  public ClientModel_Impl(ClientConnection_RMI client) {
  }


  @Override public void addPropertyChangeListener(PropertyChangeListener listener)
  {
    propertyChangeSupport.addPropertyChangeListener(listener);
  }
  @Override public void addPropertyChangeListener(String name, PropertyChangeListener listener)
  {
    propertyChangeSupport.addPropertyChangeListener(name, listener);
  }
  @Override public void removePropertyChangeListener(PropertyChangeListener listener)
  {
    propertyChangeSupport.removePropertyChangeListener(listener);
  }
  @Override public void removePropertyChangeListener(String name, PropertyChangeListener listener)
  {
    propertyChangeSupport.removePropertyChangeListener(name, listener);
  }
}
