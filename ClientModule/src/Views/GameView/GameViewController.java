package Views.GameView;

import Application.ViewModelFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.rmi.RemoteException;

public class GameViewController
{
  private GameViewModel gameViewModel;

  public Label taskHeaderLabel;
  public Label taskDescLabel;
  @FXML public Button setEffortButton;
  @FXML public TextField insertEffortField;
  @FXML public VBox chatViewWrapper;
  @FXML public VBox taskViewWrapper;

  public GameViewController()
  {
      try {
          this.gameViewModel = ViewModelFactory.getInstance().getGameViewModel();
      } catch (RemoteException e) {
          throw new RuntimeException(e);
      }
  }

  public void initialize() {

  }

  public void onsetEffortButtonPressed() {
// sets effort
  }
}
