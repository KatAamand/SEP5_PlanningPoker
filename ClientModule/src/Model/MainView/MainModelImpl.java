package Model.MainView;

import Application.ClientFactory;
import Networking.Client;

import Util.PropertyChangeSubject;
import javafx.application.Platform;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.rmi.RemoteException;

public class MainModelImpl implements MainModel, PropertyChangeSubject
{
  private PropertyChangeSupport support;
  private Client clientConnection;



  /** Primary constructor. Defers most of the declarations and definitions to the init method,
   * which is run inside a Platform.runLater statement for increased thread safety while using javaFx. */

  public MainModelImpl() {
    support = new PropertyChangeSupport(this);
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
    try
    {
      this.assignListeners();
    }
    catch (RemoteException e)
    {
      throw new RuntimeException(e);
    }
  }

  @Override public void requestCreatePlanningPokerID()
  {
    clientConnection.createPlanningPoker();
  }

  @Override public void requestConnectPlanningPoker(String planningPokerID)
  {
clientConnection.validatePlanningPokerID(planningPokerID);
  }




  /** Assigns all the required listeners to the clientConnection allowing for Observable behavior betweeen these classes. */
  private void assignListeners() throws RemoteException
  {
    //TODO define the listeners that should be added to the Client here.

    //Example:
    clientConnection.addPropertyChangeListener("DataChanged", evt -> {
      System.out.println("This is an example");});
    //End of example
  }

  @Override public void addPropertyChangeListener(PropertyChangeListener listener) {
    support.addPropertyChangeListener(listener);
  }
  @Override public void addPropertyChangeListener(String name, PropertyChangeListener listener) {
    support.addPropertyChangeListener(name, listener);
  }
  @Override public void removePropertyChangeListener(PropertyChangeListener listener) {
    support.removePropertyChangeListener(listener);
  }
  @Override public void removePropertyChangeListener(String name, PropertyChangeListener listener) {
    support.removePropertyChangeListener(name, listener);
  }
}
