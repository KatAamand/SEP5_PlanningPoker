package Views.PlanningPokerView;

import Application.ModelFactory;
import Application.Session;
import Application.ViewModelFactory;

import java.rmi.RemoteException;

public class PlanningPokerViewModel
{


  public void init() {
    try {
      setTaskViewData();
    } catch (RemoteException e) {
      throw new RuntimeException();
    }

  }

  /** Initializes the initial data that is shown inside the TaskView pane upon first loading the PlanningPokerView*/
  private void setTaskViewData() throws RemoteException
  {
    //Set the user label in the TaskView:
    String user = "ERROR";
    if(Session.getCurrentUser() != null) {
      user = Session.getCurrentUser().getUsername();
    }
    ViewModelFactory.getInstance().getTaskViewModel().labelUserIdProperty().setValue(user);

    //Set the GameId in the TaskView:
    String gameId = "ERROR";
    if(Session.getConnectedGameId() != null) {
      gameId = ModelFactory.getInstance().getMainViewModel().getActivePlanningPokerGame().getPlanningPokerID();
    }
    ViewModelFactory.getInstance().getTaskViewModel().sessionIdProperty().setValue(gameId);
  }

}
