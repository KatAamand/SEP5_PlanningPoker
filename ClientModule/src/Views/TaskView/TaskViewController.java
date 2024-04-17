package Views.TaskView;

import Application.ClientFactory;
import Application.ModelFactory;
import Application.ViewFactory;
import Application.ViewModelFactory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class TaskViewController {
    @FXML private Label sessionId;
    @FXML private Label labelUserId;
    @FXML private VBox taskWrapper;
    @FXML private Button btnCreateTask;
    @FXML private Button btnEditTask;
    private TaskViewModel taskViewModel;

    public TaskViewController() {
        try
        {
            this.taskViewModel = ViewModelFactory.getInstance().getTaskViewModel();
        }
        catch (RemoteException e)
        {
            //TODO: Add proper exception handling
            e.printStackTrace();
        }
    }

    public void initialize() {
        Platform.runLater(() -> {
            taskViewModel.initialize(btnEditTask, taskWrapper);
            ViewFactory.getInstance().setTaskViewController(this);
            //TODO: Implement a listener for any changes in the task lists!
            refresh();
        });
    }

    public void refresh()
    {

    }

    public void onCreateTaskButtonPressed() {
        taskViewModel.createTask();
    }

    public void onEditTaskButtonPressed() {
        taskViewModel.editTask();
    }
}
