package Model.PlanningPoker;

import Application.ClientFactory;
import Application.ModelFactory;
import Application.Session;
import DataTypes.PlanningPoker;
import DataTypes.User;
import Networking.Client;
import javafx.application.Platform;
import java.rmi.RemoteException;

public class PlanningPokerModelImpl implements PlanningPokerModel
{
  private PlanningPoker activePlanningPokerGame;


  public PlanningPokerModelImpl()
  {
    activePlanningPokerGame = null;

    Platform.runLater(() -> {
      try {
        init();
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

  public void setActivePlanningPokerGame(PlanningPoker activeGame) {
    this.activePlanningPokerGame = activeGame;
    Session.setConnectedGameId(activePlanningPokerGame.getPlanningPokerID());
  }

  @Override public void init() throws RemoteException
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

    ((Client) ClientFactory.getInstance().getClient()).addPropertyChangeListener("planningPokerCreatedSuccess", evt ->
      activePlanningPokerGame = (PlanningPoker) evt.getNewValue());
  }
}
