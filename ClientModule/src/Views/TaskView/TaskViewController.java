package Views.TaskView;

import Application.ViewModelFactory;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.rmi.RemoteException;

public class TaskViewController {
    public VBox taskWrapper;
    public VBox taskContainer;
    public Label taskHeaderLabel;
    public Label taskDescLabel;
    private TaskViewModel taskViewModel;
    public TaskViewController() {
        try {
            this.taskViewModel = ViewModelFactory.getInstance().getTaskViewModel();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void initialize() {
    }

    public void onCreateTaskButtonPressed() {

    }

    public void onEditTaskButtonPressed() {

    }
}
