package Views.LobbyView;

import Application.ViewModelFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import java.rmi.RemoteException;

public class LobbyViewController
{
  private LobbyViewModel lobbyViewModel;
  @FXML public Button StartGameButton;
  @FXML public Button SaveTaskButton;
  @FXML public TextArea taskTextArea;
  @FXML public VBox chatViewWrapper;
  @FXML public VBox taskViewWrapper;

  public LobbyViewController()
  {
      try {
          this.lobbyViewModel = ViewModelFactory.getInstance().getLobbyViewModel();
      } catch (RemoteException e) {
          throw new RuntimeException(e);
      }
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
