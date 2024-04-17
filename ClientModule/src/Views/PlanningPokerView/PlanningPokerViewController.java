package Views.PlanningPokerView;

import Application.ViewFactory;
import Views.ChatView.ChatViewController;
import Views.GameView.GameViewController;
import Views.LobbyView.LobbyViewController;
import Views.TaskView.TaskViewController;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import java.io.IOException;

public class PlanningPokerViewController
{
    @FXML private Parent taskView;
    @FXML private TaskViewController taskViewController;
    /** Bemærk: Tilføjede de 2 ovenstående linjer for at vise hvordan nested controllers kan indarbejdes i .fxml filen. Se også selve .fxml filen! mvh. Kristian*/

    @FXML private VBox gameViewWrapper;
    @FXML private VBox chatViewWrapper;
    @FXML private VBox lobbyViewWrapper;
    private GameViewController gameViewController;
    private ChatViewController chatViewController;
    private LobbyViewController lobbyViewController;


    public PlanningPokerViewController() {

    }


    public void initialize()
    {
        try {
            /** Her kører jeg en hurtig test for at vise at taskViewets controller rigtig nok er blevet injected og initialiseret! Bør slettes :) */
            System.out.println(taskViewController + " has succesfully been injected into PlanningPokerViewControlleren as a nested controller!");

            Parent gameView = ViewFactory.getInstance().loadGameView();
            gameViewWrapper.getChildren().add(gameView);

            Parent chatView = ViewFactory.getInstance().loadChatView();
            chatViewWrapper.getChildren().add(chatView);

            Parent lobbyView = ViewFactory.getInstance().loadLobbyView();
            lobbyViewWrapper.getChildren().add(lobbyView);

            gameViewController = ViewFactory.getInstance().loadGameViewController();
            //taskViewController = ViewFactory.getInstance().loadTaskViewController();
            chatViewController = ViewFactory.getInstance().loadChatViewController();
            lobbyViewController = ViewFactory.getInstance().loadLobbyViewController();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        catch (NullPointerException e)
        {
            //Do nothing. Simple disabled for testing individual elements.
        }


    }

    //Game View Buttons
    public void onsetEffortButtonPressed() {
        gameViewController.onsetEffortButtonPressed();
    }

    //Chat View Buttons
    public void onMessageSendButtonPressed()
    {
        chatViewController.onMessageSendButtonPressed();
    }
}
