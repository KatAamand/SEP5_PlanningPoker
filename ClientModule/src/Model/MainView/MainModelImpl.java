package Model.MainView;

import Application.ClientFactory;
import Application.ModelFactory;
import Application.Session;
import DataTypes.PlanningPoker;
import Networking.Client;

import Util.PropertyChangeSubject;
import javafx.application.Platform;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.rmi.RemoteException;

public class MainModelImpl implements MainModel
{
  private PropertyChangeSupport support;
  private Client clientConnection;
  //private PlanningPoker activePlanningPokerGame;

  /**
   * Primary constructor. Defers most of the declarations and definitions to the init method,
   * which is run inside a Platform.runLater statement for increased thread safety while using javaFx.
   */

  public MainModelImpl() throws RemoteException
  {
    support = new PropertyChangeSupport(this);
    //activePlanningPokerGame = null;

    //Assign the network connection:
    clientConnection = (Client) ClientFactory.getInstance().getClient();

    Platform.runLater(this::init);
  }


  /*public PlanningPoker getActivePlanningPokerGame() {
    return this.activePlanningPokerGame;
  }*/

  @Override public void init()
  {
    //TODO Initialize relevant data that might affect the javaFx thread here.

    //Assign all PropertyChangeListeners:
    assignListeners();
  }

  @Override public void requestCreatePlanningPokerID() throws RemoteException
  {
    ModelFactory.getInstance().getPlanningPokerModel().setActivePlanningPokerGame(clientConnection.createPlanningPoker());
  }

  @Override public void requestConnectPlanningPoker(String planningPokerID) throws RemoteException
  {
    if(clientConnection.validatePlanningPokerID(planningPokerID)) {
      ModelFactory.getInstance().getPlanningPokerModel().setActivePlanningPokerGame(clientConnection.loadPlanningPoker(planningPokerID));
    }
  }



  /** Assigns all the required listeners to the clientConnection allowing for Observable behavior betweeen these classes. */
  private void assignListeners()
  {
    clientConnection.addPropertyChangeListener("planningPokerIDValidatedSuccess", evt -> {
      Platform.runLater(() -> {
        support.firePropertyChange("planningPokerIDValidatedSuccess", null, evt.getNewValue());
      });
    });

    clientConnection.addPropertyChangeListener("planningPokerCreatedSuccess", evt -> {
      Platform.runLater(() -> {
        support.firePropertyChange("planningPokerIDCreatedSuccess", null, evt.getNewValue());
      });
    });
  }

  @Override public void addPropertyChangeListener(
      PropertyChangeListener listener)
  {
    support.addPropertyChangeListener(listener);
  }

  @Override public void addPropertyChangeListener(String name,
      PropertyChangeListener listener)
  {
    support.addPropertyChangeListener(name, listener);
  }

  @Override public void removePropertyChangeListener(
      PropertyChangeListener listener)
  {
    support.removePropertyChangeListener(listener);
  }

  @Override public void removePropertyChangeListener(String name,
      PropertyChangeListener listener)
  {
    support.removePropertyChangeListener(name, listener);
  }
}
