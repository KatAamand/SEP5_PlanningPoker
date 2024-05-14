package Views.PlanningPokerView;

import Application.ModelFactory;
import Application.Session;
import Application.ViewFactory;
import Application.ViewModelFactory;
import Model.PlanningPoker.PlanningPokerModel;
import Views.TaskView.TaskViewModel;
import javafx.application.Platform;
import javafx.stage.Stage;

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

  public void closePlanningPoker(Stage currentStage)
  {
    currentStage.setOnCloseRequest(event -> {
      // Consume the event to prevent the default close behavior:
      event.consume();

      // Reset the UI elements:
      try {
        // This list must be reset upon exiting the shown Planning Poker view in order to avoid tasks from the previous game mistakenly being added when using the skip functionality:
        ModelFactory.getInstance().getGameModel().getSkippedTaskList().clear();
      } catch (RemoteException e) {
        throw new RuntimeException();
      }

      // TODO: Implement the functionality to delete the currently active PlanningPoker game, as described in Use Case #2, ALT0 sequence.

      // TODO: Implement at network call to tell all connected users that the current game has been closed, as described in Use Case #2, ALT0 Sequence.

      // TODO: Implement user leaving connected list User story #11
      planningPokerModel.removeUserFromSession();

      // Redirect the local user to the main view:
      Platform.runLater(() -> {
          ViewFactory.getInstance().getMainViewStage().show();
          ViewFactory.getInstance().resetPlanningPokerViewController();
          currentStage.close();
      });
    });
  }
}
