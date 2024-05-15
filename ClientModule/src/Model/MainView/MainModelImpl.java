package Model.MainView;

import Application.ClientFactory;
import Application.ModelFactory;
import Application.Session;
import DataTypes.PlanningPoker;
import DataTypes.UserRoles.UserRole;
import Networking.Client;

import javafx.application.Platform;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.rmi.RemoteException;

public class MainModelImpl implements MainModel
{
  private PropertyChangeSupport support;
  private Client clientConnection;

  /**
   * Primary constructor. Defers most of the declarations and definitions to the init method,
   * which is run inside a Platform.runLater statement for increased thread safety while using javaFx.
   */

  public MainModelImpl() throws RemoteException
  {
    support = new PropertyChangeSupport(this);

    //Assign the network connection:
    clientConnection = (Client) ClientFactory.getInstance().getClient();

    Platform.runLater(this::init);
  }

  @Override public void init()
  {
    //Assign all PropertyChangeListeners:
    assignListeners();
  }

  @Override public void requestCreatePlanningPokerID() throws RemoteException
  {
    // Attempt to create the planning poker instance
    ModelFactory.getInstance().getPlanningPokerModel().setActivePlanningPokerGame(clientConnection.createPlanningPoker());
    Session.getCurrentUser().setPlanningPoker(ModelFactory.getInstance().getPlanningPokerModel().getActivePlanningPokerGame());

    // Attempt to set the creating user as the Scrum Master for the created game
    clientConnection.setRoleInGame(UserRole.SCRUM_MASTER, Session.getConnectedGameId(), Session.getCurrentUser());
  }

  @Override public void requestConnectPlanningPoker(int planningPokerID) throws RemoteException
  {
    if(clientConnection.validatePlanningPokerID(planningPokerID))
    {
      ModelFactory.getInstance().getPlanningPokerModel().setActivePlanningPokerGame(clientConnection.loadPlanningPoker(planningPokerID));
      Session.getCurrentUser().setPlanningPoker(ModelFactory.getInstance().getPlanningPokerModel().getActivePlanningPokerGame());

      // Check if there is already a Scrum Master in the game:
      if (ModelFactory.getInstance().getPlanningPokerModel().getActivePlanningPokerGame().getScrumMaster() == null) {
        // There is no Scrum Master. Set this client/User as the Scrum Master:
        clientConnection.setRoleInGame(UserRole.SCRUM_MASTER, Session.getConnectedGameId(), Session.getCurrentUser());
      }
      // Check if there is a Product Owner in the game already:
      else if (ModelFactory.getInstance().getPlanningPokerModel().getActivePlanningPokerGame().getProductOwner() == null) {
        // There is already no product owner. Set this client to initially be the Product Owner.
        clientConnection.setRoleInGame(UserRole.PRODUCT_OWNER, Session.getConnectedGameId(), Session.getCurrentUser());
      }
      else {
        // Both Scrum Master and Product Owner is already assigned. Set this client as a Developer, initially, and let the Scrum Master manually assign further roles inside the game:
        clientConnection.setRoleInGame(UserRole.DEVELOPER, Session.getConnectedGameId(), Session.getCurrentUser());
      }
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
