package Views.GameView;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class GameViewController
{
  private GameViewModel gameViewModel;

  public Label taskHeaderLabel;
  public Label taskDescLabel;
  @FXML public Button setEffortButton;
  @FXML public TextField insertEffortField;
  @FXML public VBox chatViewWrapper;
  @FXML public VBox taskViewWrapper;

  public GameViewController(GameViewModel gameViewModel)
  {
    this.gameViewModel = gameViewModel;
  }

  public void initialize() {

  }

  public void onsetEffortButtonPressed() {
// sets effort
  }
}
