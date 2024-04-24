package Views.PlanningPokerView;

import Application.ModelFactory;
import Application.Session;
import Application.ViewModelFactory;
import Views.ChatView.ChatViewController;
import Views.GameView.GameViewController;
import Views.LobbyView.LobbyViewController;
import Views.TaskView.TaskViewController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
    }

    public void onStartGameButtonPressed()
    {
        lobbyView.setVisible(false);
        lobbyView.setManaged(false);
        gameView.setManaged(true);
        gameView.setVisible(true);
        Platform.runLater(() -> gameViewController.initialize());
    }
}
