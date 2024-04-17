package Views.PlanningPokerView;

import Views.ChatView.ChatViewController;
import Views.GameView.GameViewController;
import Views.TaskView.TaskViewController;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class PlanningPokerViewController
{
    public PlanningPokerViewController(GameViewController gameViewController, TaskViewController taskViewController, ChatViewController chatViewController) {
        this.gameViewController = gameViewController;
        this.taskViewController = taskViewController;
        this.chatViewController = chatViewController;
    }

    @FXML VBox taskView;
    @FXML VBox gameView;
    @FXML VBox chatView;
    @FXML GameViewController gameViewController;
    @FXML TaskViewController taskViewController;
    @FXML ChatViewController chatViewController;

    //Game View Buttons
    public void onsetEffortButtonPressed() {
        gameViewController.onsetEffortButtonPressed();
    }

    //Task View Buttons
    public void onCreateTaskButtonPressed() {
        taskViewController.onCreateTaskButtonPressed();
    }

    public void onEditTaskButtonPressed() {
        taskViewController.onEditTaskButtonPressed();
    }

    //Chat View Buttons
    public void onMessageSendButtonPressed()
    {
        chatViewController.onMessageSendButtonPressed();
    }
}
