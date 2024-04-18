package Views.PlanningPokerView;

import Application.Session;
import Application.ViewModelFactory;
import Views.ChatView.ChatViewController;
import Views.GameView.GameViewController;
import Views.LobbyView.LobbyViewController;
import Views.TaskView.TaskViewController;
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
    @FXML private HBox lobbyView;
    @FXML private Button startGameButton;
    @FXML private TaskViewController taskViewController;
    @FXML private GameViewController gameViewController;
    @FXML private ChatViewController chatViewController;
    @FXML private LobbyViewController lobbyViewController;

    public PlanningPokerViewController() {
    }

    public void initialize()
    {
        gameView.setVisible(false);
        gameView.setManaged(false);
        lobbyView.setManaged(true);
        lobbyView.setVisible(true);
        setSessionData();
    }

    public void onStartGameButtonPressed()
    {
        lobbyView.setVisible(false);
        lobbyView.setManaged(false);
        gameView.setManaged(true);
        gameView.setVisible(true);
        startGameButton.setVisible(false);
        startGameButton.setManaged(false);
    }

    private void setSessionData()
    {
        try {
            //Set the user label in the TaskView:
            String user = "UNDEFINED";
            if(Session.getCurrentUser() != null)
            {
                user = Session.getCurrentUser().getUsername();
            }
            ViewModelFactory.getInstance().getTaskViewModel().labelUserIdProperty().setValue(user);
        }
        catch (RemoteException e)
        {
            //TODO: Add proper exception handling
            e.printStackTrace();
        }
    }
}
