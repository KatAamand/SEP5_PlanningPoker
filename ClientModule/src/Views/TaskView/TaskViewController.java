package Views.TaskView;

import Application.ViewModelFactory;
import Views.PlanningPokerView.PlanningPokerViewController;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
    @FXML private Button btnRuleSet;
    @FXML private Button btnExportTaskList;
    @FXML private Button btnImportTaskList;
    private TaskViewModel taskViewModel;
    private PlanningPokerViewController parentController;

    public TaskViewController() throws RemoteException {
        this.taskViewModel = ViewModelFactory.getInstance().getTaskViewModel();
    }

    public void setParentController(PlanningPokerViewController parentController) {
        this.parentController = parentController;
    }

    private void applyBindings()
    {
        sessionId.textProperty().bindBidirectional(taskViewModel.sessionIdProperty());
        labelUserId.textProperty().bindBidirectional(taskViewModel.labelUserIdProperty());
    }


    @Override public void initialize(URL location, ResourceBundle resources)
    {
        Platform.runLater(() -> {
            taskViewModel.initialize(btnCreateTask, btnEditTask,
                btnExportTaskList, btnRuleSet, btnImportTaskList, taskWrapper);

            //Apply Property Bindings:
            applyBindings();

            //Refresh onScreen elements:
            this.taskViewModel.refresh();

            btnImportTaskList.setOnAction(event -> {
                Stage stage = (Stage) btnImportTaskList.getScene().getWindow();
                taskViewModel.importTaskList();
            });
        });
    }



}
