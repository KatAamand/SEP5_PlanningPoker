package Views.PlanningPokerView;

import Application.ViewModelFactory;
import Views.ChatView.ChatViewController;
import Views.GameView.GameViewController;
import Views.LobbyView.LobbyViewController;
import Views.TaskView.TaskViewController;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.rmi.RemoteException;

public class PlanningPokerViewController
{
    @FXML private VBox taskView;
    @FXML private HBox gameView;
    @FXML private VBox chatView;
    @FXML private VBox lobbyView;
    @FXML private TaskViewController taskViewController;
    @FXML private GameViewController gameViewController;
    @FXML private ChatViewController chatViewController;
    @FXML private LobbyViewController lobbyViewController;

    private BooleanProperty isTaskListEmpty = new SimpleBooleanProperty(); // This is a property that is used to bind the startGameButton to the taskList being empty

    private PlanningPokerViewModel planningPokerViewModel;

    public PlanningPokerViewController() throws RemoteException {
        planningPokerViewModel = ViewModelFactory.getInstance().getPlanningPokerViewModel();
    }

    public void initialize()
    {
        gameView.setVisible(false);
        gameView.setManaged(false);
        lobbyView.setManaged(true);
        lobbyView.setVisible(true);
        Platform.runLater(() -> lobbyViewController.setParentController(this));
        Platform.runLater(() -> planningPokerViewModel.init());
        Platform.runLater(this::catchCloseRequests);
    }

    public void onStartGameButtonPressed()
    {
        lobbyView.setVisible(false);
        lobbyView.setManaged(false);
        gameView.setManaged(true);
        gameView.setVisible(true);
        Platform.runLater(() -> gameViewController.initialize());
        gameViewController.onGameStart();
    }

    public void catchCloseRequests()
    {
        Stage thisStage = (Stage) taskView.getScene().getWindow();
        planningPokerViewModel.closePlanningPoker(thisStage);
    }

    public TaskViewController getTaskViewController() {
        return this.taskViewController;
    }

    public GameViewController getGameViewController() {
        return this.gameViewController;
    }

    public LobbyViewController getLobbyViewController() {
        return this.lobbyViewController;
    }

    public ChatViewController getChatViewController() {
        return this.chatViewController;
    }

    public BooleanProperty isTaskListEmptyProperty() {
        return isTaskListEmpty;
    }
}
