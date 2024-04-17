package Views.PlanningPokerView;

import Application.ViewFactory;
import Views.ChatView.ChatViewController;
import Views.GameView.GameViewController;
import Views.TaskView.TaskViewController;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class PlanningPokerViewController
{
    public PlanningPokerViewController() {

    }
    @FXML VBox taskViewWrapper;
    @FXML VBox gameViewWrapper;
    @FXML VBox chatViewWrapper;
    private GameViewController gameViewController;
    private TaskViewController taskViewController;
    private ChatViewController chatViewController;

    public void initialize()
    {
        try {
            VBox taskView = ViewFactory.getInstance().loadTaskView();
            taskViewWrapper.getChildren().add(taskView);

            Parent gameView = ViewFactory.getInstance().loadGameView();
            gameViewWrapper.getChildren().add(gameView);

            Parent chatView = ViewFactory.getInstance().loadChatView();
            chatViewWrapper.getChildren().add(chatView);

            gameViewController = ViewFactory.getInstance().loadGameViewController();
            taskViewController = ViewFactory.getInstance().loadTaskViewController();
            chatViewController = ViewFactory.getInstance().loadChatViewController();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

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
