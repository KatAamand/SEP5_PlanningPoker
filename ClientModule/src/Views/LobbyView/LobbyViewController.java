package Views.LobbyView;

import Application.ViewModelFactory;
import Views.PlanningPokerView.PlanningPokerViewController;
import Views.TaskView.AddTaskViewController;
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
  @FXML private VBox addTask;
  @FXML private AddTaskViewController addTaskController;
  @FXML private Button StartGameButton;

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
    addTaskController.isEmbedded(true);

  }

  public void onStartGameButtonPressed() {
    if(parentController != null) {
      parentController.onStartGameButtonPressed();
    }
  }


}
