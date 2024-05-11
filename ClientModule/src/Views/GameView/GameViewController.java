package Views.GameView;

import Application.ViewModelFactory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.rmi.RemoteException;

public class GameViewController {
    @FXML
    public StackPane effortWrapper;
    @FXML
    public HBox placedCardsWrapper;
    @FXML
    private Label taskHeaderLabel;
    @FXML
    private Label taskDescLabel;
    @FXML
    private Button setEffortButton;
    @FXML
    private TextField insertEffortField;
    private GameViewModel gameViewModel;

    public GameViewController() throws RemoteException {
        this.gameViewModel = ViewModelFactory.getInstance().getGameViewModel();
        Platform.runLater(this::initialize);
    }

    public void initialize() {
        applyBindings();
        gameViewModel.setPlacedCardsWrapper(placedCardsWrapper);
        gameViewModel.refresh();
        showPlayingCards();
    }

    private void applyBindings() {
        taskHeaderLabel.textProperty().bindBidirectional(gameViewModel.taskHeaderPropertyProperty());
        taskDescLabel.textProperty().bindBidirectional(gameViewModel.taskDescPropertyProperty());
    }

    public void onsetEffortButtonPressed() {
        gameViewModel.refresh();
        //TODO Functionality should be added
    }

    public void onSkipEffortButtonPressed() {
        gameViewModel.skipTask();
    }

    public void showPlayingCards() {
        gameViewModel.getPossiblePlayingCards(effortWrapper);
    }

    public void onClearCardsButtonPressed() {
        gameViewModel.requestClearPlacedCards();
    }
}
