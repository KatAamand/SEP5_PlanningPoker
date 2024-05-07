package Views.TaskView;

import Application.ModelFactory;
import Model.Task.TaskModel;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class TaskViewModel {
    private TaskModel taskModel;
    private Button btnEditTask;
    private Button btnCreateTask;
    private VBox taskWrapper;
    private Property<String> sessionId;
    private Property<String> labelUserId;
    private ArrayList<SingleTaskViewController> taskControllerList;
    private ArrayList<SingleTaskViewModel> singleTaskViewModelList;
    private BooleanProperty isTaskListEmpty;


    public TaskViewModel() throws RemoteException {
        setSingleTaskViewModelList(new ArrayList<>());
        taskControllerList = new ArrayList<>();
        sessionId = new SimpleStringProperty("UNDEFINED");
        labelUserId = new SimpleStringProperty("UNDEFINED");
        this.taskModel = ModelFactory.getInstance().getTaskModel();

        this.isTaskListEmpty = new SimpleBooleanProperty(true);

        updateTaskListEmptyProperty(); // updates upon creation

        // Add a listener to the taskModel to update the isTaskListEmptyProperty when the taskList is updated
        taskModel.addPropertyChangeListener("taskListUpdated", evt -> {
            Platform.runLater(this::updateTaskListEmptyProperty);
        });
    }
    

    public void initialize(Button btnCreateTask, Button btnEditTask, VBox taskWrapper) {
        setButtonReferences(btnCreateTask, btnEditTask);
        assignButtonMethods();
        this.taskWrapper = taskWrapper;
        disableEditButton();
        Platform.runLater(this::refresh);

        //Assign listeners:
        taskModel.addPropertyChangeListener("taskListUpdated", evt -> {Platform.runLater(this::refresh);});
    }


    // Makes sure to update isTaskListEmptyProperty when the taskList is updated
    private void updateTaskListEmptyProperty() {
        isTaskListEmpty.set(taskModel.getTaskList().isEmpty());
    }

    public ReadOnlyBooleanProperty isTaskListEmptyProperty() {
        return isTaskListEmpty;
    }


    public void assignButtonMethods() {
        btnEditTask.setOnAction(event -> {this.editTask();});
        btnCreateTask.setOnAction(event -> {this.createTask();});
    }


    public void refresh() {
        //Refresh the ViewModels inside the singleTaskViewModelList
        setSingleTaskViewModelList(new ArrayList<>());

        if(taskModel.getTaskList() != null)
        {
            for (int i = 0; i < taskModel.getTaskList().size(); i++)
            {
                this.singleTaskViewModelList.add(new SingleTaskViewModel(this));
                getSingleTaskViewModelList().get(i).setTaskHeaderLabel(i+1 + ": " + taskModel.getTaskList().get(i).getTaskName());
                getSingleTaskViewModelList().get(i).setTaskDesc(taskModel.getTaskList().get(i).getDescription());
            }
        }

        //Refresh all the data in the nested viewControllers
        try {
            isTaskListEmptyProperty();
            taskWrapper.getChildren().clear();
            displayTaskData();
        } catch (IOException e) {
            //TODO: Implement proper exception handling.
            e.printStackTrace();
        }
    }


    private void displayTaskData() throws IOException {
        if(taskModel.getTaskList() != null) {
            int numberOfTasks = taskModel.getTaskList().size();
            taskControllerList.clear();

            for (int i = 0; i < numberOfTasks; i++) {
                //Initialize a separate nested view and controller for each task:
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SingleTaskView.fxml"));
                VBox newTask = fxmlLoader.load();
                ((SingleTaskViewController) fxmlLoader.getController()).initialize(i, this.getSingleTaskViewModelList().get(i));
                taskControllerList.add(fxmlLoader.getController());

                //Assign the created controller to this class list of nested controllers:
                taskWrapper.getChildren().add(newTask);

                //Assign the necessary property bindings:
                taskControllerList.get(i).getTaskHeaderLabel().textProperty().bindBidirectional(this.getSingleTaskViewModelList().get(i).getTaskHeaderLabelProperty());
                taskControllerList.get(i).getTaskDescLabel().textProperty().bindBidirectional(this.getSingleTaskViewModelList().get(i).getTaskDescProperty());
            }
        }
    }


    public Property<String> sessionIdProperty() {
        return sessionId;
    }


    public Property<String> labelUserIdProperty() {
        return labelUserId;
    }


    public ArrayList<SingleTaskViewModel> getSingleTaskViewModelList() {
        return this.singleTaskViewModelList;
    }


    public void setSingleTaskViewModelList(ArrayList<SingleTaskViewModel> singleTaskViewModelList) {
        this.singleTaskViewModelList = singleTaskViewModelList;
    }


    private void setButtonReferences(Button btnCreateTask, Button btnEditTask) {
        this.btnCreateTask = btnCreateTask;
        this.btnEditTask = btnEditTask;
    }


    private void disableEditButton() {
        btnEditTask.setDisable(true);
    }


    private void enableEditButton() {
        btnEditTask.setDisable(false);
    }


    /** Creates a pop-up window where the user can enter a new task into.*/
    public void createTask() {
        //Create the viewController
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AddTaskView.fxml"));

        //Create the popup screen as a new stage, and show it!
        Stage stage = new Stage();
        stage.setTitle("Opret Task");
        try {
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            //TODO: Add proper exception handling.
            e.printStackTrace();
        }
    }


    public void editTask() {
        //TODO: Missing implementation
    }
}
