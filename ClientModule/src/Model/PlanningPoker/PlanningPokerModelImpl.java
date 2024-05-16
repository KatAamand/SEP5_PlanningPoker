package Model.PlanningPoker;

import Application.ClientFactory;
import Application.ModelFactory;
import Application.Session;
import DataTypes.PlanningPoker;
import DataTypes.User;
import DataTypes.UserRoles.UserRole;
import Networking.Client;
import javafx.application.Platform;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.rmi.RemoteException;

public class PlanningPokerModelImpl implements PlanningPokerModel
{
  private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
  private PlanningPoker activePlanningPokerGame;
  private Client clientConnection;



  public PlanningPokerModelImpl() throws RemoteException{
    activePlanningPokerGame = null;

    //Assign the network connection:
    clientConnection = (Client) ClientFactory.getInstance().getClient();

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

  /** Resets the local User to the base level developer permission set, when leaving a planning poker game */
  @Override public void resetUserPermissionUponLeavingGame() {
    // Reset user permission
    clientConnection.setRoleInGame(UserRole.DEVELOPER, Session.getConnectedGameId(), Session.getCurrentUser());

    // Remove the user from the session and the game
    this.removeUserFromSession();
    clientConnection.removeUserFromGame(Session.getConnectedGameId());
  }

  @Override
  public void requestStartGame() {
    // Request the game to start
    clientConnection.requestStartGame(Session.getConnectedGameId());
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
    clientConnection.addPropertyChangeListener("gameStarted", evt -> {
      propertyChangeSupport.firePropertyChange("gameStarted", null, null);
    });

    ((Client) ClientFactory.getInstance().getClient()).addPropertyChangeListener("planningPokerIDValidatedSuccess", evt -> {
      //Does nothing at the moment.
    });


    ((Client) ClientFactory.getInstance().getClient()).addPropertyChangeListener("planningPokerCreatedSuccess", evt -> {
      setActivePlanningPokerGame((PlanningPoker) evt.getNewValue());
      propertyChangeSupport.firePropertyChange("PlanningPokerObjUpdated", null, null);
    });


    ((Client) ClientFactory.getInstance().getClient()).addPropertyChangeListener("PlanningPokerObjUpdated", evt -> {
      // Update the active Planning Poker game object
      setActivePlanningPokerGame((PlanningPoker) evt.getNewValue());

      // Update the Planning Poker object embedded inside the user:
      Session.getCurrentUser().setPlanningPoker(this.getActivePlanningPokerGame());

      // Ensure local user has proper role and permissions:
      try {
        ModelFactory.getInstance().getPlanningPokerModel().confirmLocalUserHasProperRoleAndPermissions(this.getActivePlanningPokerGame());
      } catch (RemoteException e) {
        throw new RuntimeException();
      }

      // Notify listeners that Planning Poker object was updated, so listeners can refresh the UI:
      propertyChangeSupport.firePropertyChange("PlanningPokerObjUpdated", null, null);
    });


    ((Client) ClientFactory.getInstance().getClient()).addPropertyChangeListener("UpdatedLocalUser", evt -> {
      propertyChangeSupport.firePropertyChange("UpdatedLocalUser", null, null);
    });

  }

  public void confirmLocalUserHasProperRoleAndPermissions(PlanningPoker game) {
    if(game != null) {
      // Check if the local user was assigned a role inside the Planning Poker Object. If so, ensure that the local session Object is updated:
      UserRole localUserRole = this.getLocalUser().getRole().getUserRole();
      String localUserName = this.getLocalUser().getUsername();

      String gameProductOwnerName = "";
      if(game.getProductOwner() != null) {
        gameProductOwnerName = game.getProductOwner().getUsername();
      }
      String gameScrumMasterName = "";
      if(game.getScrumMaster() != null) {
        gameScrumMasterName = game.getScrumMaster().getUsername();
      }

      // Compare local user to the received games Product Owner:
      if (localUserName.equals(gameProductOwnerName)) {
        // Local user should be the Product Owner. Check if the local user already has the proper role assigned, corresponding to a Product Owner:
        if(!localUserRole.equals(game.getProductOwner().getRole().getUserRole())) {
          // Local users has not been updated. Update it:
          Session.setCurrentUser(game.getProductOwner());
          System.out.println("Local user [" + this.getLocalUser().getUsername() + "] has become '" + Session.getCurrentUser().getRole().getRoleAsString() + "' in game [" + game.getPlanningPokerID() + "]");
        }
      }
      // Compare local user to the received games Scrum Master:
      else if (localUserName.equals(gameScrumMasterName)) {
        // Local user should be the Scrum Master. Check if the local user already has the proper role assigned, corresponding to a Scrum Master:
        if(!localUserRole.equals(game.getScrumMaster().getRole().getUserRole())) {
          // Local users has not been updated. Update it:
          Session.setCurrentUser(game.getScrumMaster());
          System.out.println("Local user [" + this.getLocalUser().getUsername() + "] has become '" + this.getLocalUser().getRole().getRoleAsString() + "' in game [" + game.getPlanningPokerID() + "]");
        }
      }
      // Local user is not Scrum Master nor Product Owner in most recent game. Ensure they are developers:
      else {
        if(localUserRole != UserRole.DEVELOPER) {
          // Local user is not properly assigned as developer. Query server to properly set role:
          clientConnection.setRoleInGame(UserRole.DEVELOPER, game.getPlanningPokerID(), this.getLocalUser());
        }
      }
    }
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
