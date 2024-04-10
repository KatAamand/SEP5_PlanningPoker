package Views.TaskView;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class TaskViewController {
    public VBox taskWrapper;
    public VBox taskContainer;
    public Label taskHeaderLabel;
    public Label taskDescLabel;
    private TaskViewModel taskViewModel;
    public TaskViewController(TaskViewModel taskViewModel) {
        this.taskViewModel = taskViewModel;
    }

    public void initialize() {

    }

    public void onCreateTaskButtonPressed() {

    }

    public void onEditTaskButtonPressed() {

    }
}
