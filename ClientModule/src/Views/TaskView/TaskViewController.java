package Views.TaskView;

import Application.ViewFactory;
import Application.ViewModelFactory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

public class TaskViewController implements Initializable
{
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
            System.out.println("created");
        }
        catch (RemoteException e)
        {
            //TODO: Add proper exception handling
            e.printStackTrace();
        }
    }


    @Override public void initialize(URL location, ResourceBundle resources)
    {
        Platform.runLater(() -> {
            taskViewModel.initialize(btnCreateTask, btnEditTask, taskWrapper);
            ViewFactory.getInstance().setTaskViewController(this);
        });
    }
}
