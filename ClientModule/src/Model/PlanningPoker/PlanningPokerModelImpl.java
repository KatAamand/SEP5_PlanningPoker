package Model.PlanningPoker;

import Application.ClientFactory;
import Application.ModelFactory;
import Application.Session;
import DataTypes.PlanningPoker;
import DataTypes.User;
import Networking.Client;
import javafx.application.Platform;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.rmi.RemoteException;

public class PlanningPokerModelImpl implements PlanningPokerModel
{
  private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
  private PlanningPoker activePlanningPokerGame;



  public PlanningPokerModelImpl() {
    activePlanningPokerGame = null;

    Platform.runLater(() -> {
      try {
        initialize();
      } catch (RemoteException e) {
        throw new RuntimeException(e);
      }
    });
  }

  @Override public PlanningPoker getActivePlanningPokerGame()  {
    //Ensure that the returned PlanningPoker game instance also corresponds with the one in the primary instance of this class!
    try {
      if(! ModelFactory.getInstance().getPlanningPokerModel().equals(this)) {
        setActivePlanningPokerGame(ModelFactory.getInstance().getPlanningPokerModel().getActivePlanningPokerGame());
      }
    } catch (RemoteException e) {
      throw new RuntimeException();
    }

    return this.activePlanningPokerGame;
  }

  @Override
  public void removeUserFromSession() {
      try {
          ModelFactory.getInstance().getChatModel().removeUserFromSession();
      } catch (RemoteException e) {
          throw new RuntimeException(e);
      }
  }

  public void setActivePlanningPokerGame(PlanningPoker activeGame) {
    this.activePlanningPokerGame = activeGame;
    Session.setConnectedGameId(activePlanningPokerGame.getPlanningPokerID());
  }

  @Override public void initialize() throws RemoteException
  {
    //Assign all PropertyChangeListeners:
    assignListeners();
  }

  @Override public User getLocalUser()
  {
    return Session.getCurrentUser();
  }

  /** Assigns all the required listeners to the clientConnection allowing for Observable behavior between these classes. */
  private void assignListeners() throws RemoteException {
    ((Client) ClientFactory.getInstance().getClient()).addPropertyChangeListener("planningPokerIDValidatedSuccess", evt -> {
      //Does nothing at the moment.
    });

    ((Client) ClientFactory.getInstance().getClient()).addPropertyChangeListener("planningPokerCreatedSuccess", evt -> {
      setActivePlanningPokerGame((PlanningPoker) evt.getNewValue());
      propertyChangeSupport.firePropertyChange("PlanningPokerObjUpdated", null, null);
    });

    ((Client) ClientFactory.getInstance().getClient()).addPropertyChangeListener("PlanningPokerObjUpdated", evt -> {
      setActivePlanningPokerGame((PlanningPoker) evt.getNewValue());
      propertyChangeSupport.firePropertyChange("PlanningPokerObjUpdated", null, null);
    });

    ((Client) ClientFactory.getInstance().getClient()).addPropertyChangeListener("UpdatedLocalUser", evt -> {
      propertyChangeSupport.firePropertyChange("UpdatedLocalUser", null, null);
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


  @Override public void removePropertyChangeListener(String name, PropertyChangeListener listener)
  {
    propertyChangeSupport.removePropertyChangeListener(name, listener);
  }
}
