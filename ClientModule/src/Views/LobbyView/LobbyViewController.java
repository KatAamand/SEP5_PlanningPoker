package Views.LobbyView;

import Application.Session;
import Application.ViewModelFactory;
import DataTypes.UserRoles.UserPermission;
import Views.PlanningPokerView.PlanningPokerViewController;
import Views.TaskView.ManageSingleTaskViewController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

public class LobbyViewController implements Initializable
{
  @FXML private VBox addTaskWrapper;
  @FXML private Label titleLabel;
  @FXML private VBox manageTask;
  @FXML private ManageSingleTaskViewController manageTaskController;
  @FXML private Button startGameButton;

  private LobbyViewModel lobbyViewModel;
  private PlanningPokerViewController parentController;


  public LobbyViewController() throws RemoteException
  {
      this.lobbyViewModel = ViewModelFactory.getInstance().getLobbyViewModel();
  }

  public void setParentController(PlanningPokerViewController parentController) {
    this.parentController = parentController;
  }

  @Override public void initialize(URL location, ResourceBundle resources)
  {
    manageTaskController.isEmbedded(true);
    startGameButton.disableProperty().bind(lobbyViewModel.isTaskListEmptyProperty());

    lobbyViewModel.addPropertyChangeListener("PlanningPokerObjUpdated", evt -> Platform.runLater(() -> manageTaskController.refresh()));
  }

  public ManageSingleTaskViewController getEmbeddedManageTaskViewController() {
    return this.manageTaskController;
  }

  public void onStartGameButtonPressed() {
    if(parentController != null) {
        parentController.onStartGameButtonPressed();
    }
  }
}
