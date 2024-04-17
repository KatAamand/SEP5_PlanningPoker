package Model.Login;

import Application.ClientFactory;

import DataTypes.User;
import Networking.ClientInterfaces.LoginClientInterface;
import Networking.Client_RMI;
import javafx.application.Platform;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.rmi.RemoteException;

/** Implementation of the client side model. This class handles data related logical operations on the client side,
 * and queries server related data through the Client_RMI network class.*/
public class LoginModelImpl implements LoginModel
{
  private final PropertyChangeSupport support;
  private LoginClientInterface clientConnection;

  public LoginModelImpl() {
    support = new PropertyChangeSupport(this);

    //Assign the network connection:
    try {
      clientConnection = ClientFactory.getInstance().getClient();
    } catch (RemoteException e) {
      //TODO: Properly handle this error!
      e.printStackTrace();
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
  public void requestLogin(String username, String password) {
    clientConnection.validateUser(username, password);
  }

  @Override
  public void requestCreateUser(String username, String password) {
    clientConnection.createUser(username, password);
  }



  @Override public void addPropertyChangeListener(PropertyChangeListener listener)
  {
    support.addPropertyChangeListener(listener);
  }
  @Override public void addPropertyChangeListener(String name, PropertyChangeListener listener)
  {
    support.addPropertyChangeListener(name, listener);
  }
  @Override public void removePropertyChangeListener(PropertyChangeListener listener)
  {
    support.removePropertyChangeListener(listener);
  }
  @Override public void removePropertyChangeListener(String name, PropertyChangeListener listener)
  {
    support.removePropertyChangeListener(name, listener);
  }


  /** Assigns all the required listeners to the clientConnection allowing for Observable behavior betweeen these classes. */
  private void assignListeners()
  {
    //Example:
    clientConnection.addPropertyChangeListener("DataChanged", evt -> {
      System.out.println("This is an example");});
    //End of example
  }

}
