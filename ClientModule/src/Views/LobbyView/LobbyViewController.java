package Views.LobbyView;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class LobbyViewController
{
  private LobbyViewModel lobbyViewModel;
  @FXML public Button StartGameButton;
  @FXML public Button SaveTaskButton;
  @FXML public TextArea taskTextArea;
  @FXML public VBox chatViewWrapper;
  @FXML public VBox taskViewWrapper;

  public LobbyViewController(LobbyViewModel lobbyViewModel)
  {
    this.lobbyViewModel = lobbyViewModel;
  }

  public void initialize() {

  }

  public void onSaveTaskButtonPressed() {
// tries to save or create new a task
  }

  public void onStartGameButtonPressed() {
// tries to start the game
  }
}
