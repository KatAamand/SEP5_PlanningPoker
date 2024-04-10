package Views.TaskView;

import Views.MainView.MainViewModel;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class TaskViewController {
    public VBox taskWrapper;
    public VBox taskContainer;
    public Label taskHeaderLabel;
    public Label taskDescLabel;
    private MainViewModel mainViewModel;
    public TaskViewController(MainViewModel mainViewModel) {
        this.mainViewModel = mainViewModel;
    }

    public void onCreateTaskButtonPressed(ActionEvent actionEvent) {
    }

    public void onEditTaskButtonPressed(ActionEvent actionEvent) {
    }
}
