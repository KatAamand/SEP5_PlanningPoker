package Model.PlanningPoker;

import Application.ClientFactory;
import Application.Session;
import DataTypes.PlanningPoker;
import Networking.Client;
import javafx.application.Platform;

import java.rmi.RemoteException;

public class PlanningPokerModelImpl implements PlanningPokerModel
{
  private PlanningPoker activePlanningPokerGame;
  private Client clientConnection;

  public PlanningPokerModelImpl() throws RemoteException
  {
    activePlanningPokerGame = null;

    //Assign the network connection:
    clientConnection = (Client) ClientFactory.getInstance().getClient();

    Platform.runLater(this::init);
  }

  public PlanningPoker getActivePlanningPokerGame() {
    return this.activePlanningPokerGame;
  }

  public void setActivePlanningPokerGame(PlanningPoker activeGame) {
    this.activePlanningPokerGame = activeGame;
    Session.setConnectedGameId(activePlanningPokerGame.getPlanningPokerID());
  }

  @Override public void init() {
    //Assign all PropertyChangeListeners:
    assignListeners();
  }



  /** Assigns all the required listeners to the clientConnection allowing for Observable behavior betweeen these classes. */
  private void assignListeners() {
    clientConnection.addPropertyChangeListener("planningPokerIDValidatedSuccess", evt ->
      activePlanningPokerGame = (PlanningPoker) evt.getNewValue());

    clientConnection.addPropertyChangeListener("planningPokerCreatedSuccess", evt ->
      activePlanningPokerGame = (PlanningPoker) evt.getNewValue());
  }
}
