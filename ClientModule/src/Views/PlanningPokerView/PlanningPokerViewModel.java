package Views.PlanningPokerView;

import Application.ModelFactory;
import Application.Session;
import Application.ViewModelFactory;
import Model.PlanningPoker.PlanningPokerModel;
import Views.TaskView.TaskViewModel;

import java.rmi.RemoteException;

public class PlanningPokerViewModel
{
  private TaskViewModel taskViewModel;
  private PlanningPokerModel planningPokerModel;


  public PlanningPokerViewModel() throws RemoteException {
    taskViewModel = ViewModelFactory.getInstance().getTaskViewModel();
    planningPokerModel = ModelFactory.getInstance().getPlanningPokerModel();
  }

  public void init() {
    setTaskViewData();
  }

  /** Initializes the initial data that is shown inside the TaskView pane upon first loading the PlanningPokerView*/
  private void setTaskViewData()
  {
    //Set the user label in the TaskView:
    String user = "ERROR";
    if(Session.getCurrentUser() != null) {
      user = Session.getCurrentUser().getUsername();
    }
    taskViewModel.labelUserIdProperty().setValue(user);

    //Set the GameId in the TaskView:
    String gameId = "ERROR";
    if(Session.getConnectedGameId() != null) {
      gameId = planningPokerModel.getActivePlanningPokerGame().getPlanningPokerID();
    }
    taskViewModel.sessionIdProperty().setValue(gameId);
  }

}
