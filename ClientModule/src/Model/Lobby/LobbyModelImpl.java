package Model.Lobby;

import Application.ClientFactory;
import Model.PlanningPoker.PlanningPokerModelImpl;
import Networking.Client;
import javafx.application.Platform;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.rmi.RemoteException;

public class LobbyModelImpl extends PlanningPokerModelImpl implements LobbyModel
{
  private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);



  /** Primary constructor. Defers most of the declarations and definitions to the init method,
   * which is run inside a Platform.runLater statement for increased thread safety while using javaFx. */
  public LobbyModelImpl() throws RemoteException
  {
    super();
    super.initialize();
    //Initialize remaining data:
    Platform.runLater(this::init);
  }



  @Override public void init()
  {
    //Assign all PropertyChangeListeners:
    this.assignListeners();
  }



  /** Assigns all the required listeners to the clientConnection allowing for Observable behavior betweeen these classes. */
  private void assignListeners()
  {
    super.addPropertyChangeListener("PlanningPokerObjUpdated", evt -> {
      propertyChangeSupport.firePropertyChange("PlanningPokerObjUpdated", null, null);
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
