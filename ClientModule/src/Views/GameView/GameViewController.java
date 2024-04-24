package Views.GameView;

import Application.ViewModelFactory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.rmi.RemoteException;

public class GameViewController
{
  @FXML private Label taskHeaderLabel;
  @FXML private Label taskDescLabel;
  @FXML private Button setEffortButton;
  @FXML private TextField insertEffortField;
  private GameViewModel gameViewModel;

  public GameViewController() throws RemoteException
  {
    this.gameViewModel = ViewModelFactory.getInstance().getGameViewModel();
    Platform.runLater(this::initialize);
  }

  public void initialize() {
    applyBindings();
    gameViewModel.refresh();
  }

  private void applyBindings()
  {
    taskHeaderLabel.textProperty().bindBidirectional(gameViewModel.taskHeaderPropertyProperty());
    taskDescLabel.textProperty().bindBidirectional(gameViewModel.taskDescPropertyProperty());
  }

  public void onsetEffortButtonPressed() {
    gameViewModel.refresh();
    //TODO Functionality should be added
  }
}
