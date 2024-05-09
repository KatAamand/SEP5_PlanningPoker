package Views.GameView;

import Application.ViewModelFactory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.rmi.RemoteException;
import java.util.Objects;

public class GameViewController
{
  @FXML public HBox effortWrapper;
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

    populateEfforts();
  }

  private void populateEfforts() {
    for (int i = 0; i < gameViewModel.getEfforts().size(); i++) {
      ImageView imageView = new ImageView();
      imageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(gameViewModel.getEfforts().get(i).getImgPath()))));
      effortWrapper.getChildren().add(imageView);
    }
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
