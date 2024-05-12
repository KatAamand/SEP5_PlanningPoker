package Views.TaskView;

import Application.ModelFactory;
import Application.ViewModelFactory;
import DataTypes.Task;
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
    private ArrayList<SingleTaskListViewController> taskControllerList;
    private ArrayList<SingleTaskListViewModel> singleTaskListViewModelList;
    private BooleanProperty isTaskListEmpty;
    private Task selectedTask; // Holds the currently selected task, or null if no task is selected.


    public TaskViewModel() throws RemoteException {
        setSingleTaskViewModelList(new ArrayList<>());
        taskControllerList = new ArrayList<>();
        sessionId = new SimpleStringProperty("UNDEFINED");
        labelUserId = new SimpleStringProperty("UNDEFINED");
        this.taskModel = ModelFactory.getInstance().getTaskModel();
        this.selectedTask = null;

        this.isTaskListEmpty = new SimpleBooleanProperty(true);

        updateTaskListEmptyProperty(); // updates upon creation
    }
    

    public void initialize(Button btnCreateTask, Button btnEditTask, VBox taskWrapper) {
        setButtonReferences(btnCreateTask, btnEditTask);
        assignButtonMethods();
        this.taskWrapper = taskWrapper;
        disableEditButton();
        Platform.runLater(this::refresh);

        //Assign listeners:
        taskModel.addPropertyChangeListener("taskListUpdated", evt -> {
            Platform.runLater(this::refresh);
            // Add a listener to the taskModel to update the isTaskListEmptyProperty when the taskList is updated
            Platform.runLater(this::updateTaskListEmptyProperty);});
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
        //Refresh the ViewModels inside the singleTaskListViewModelList
        setSingleTaskViewModelList(new ArrayList<>());

        if(taskModel.getTaskList() != null)
        {
            for (int i = 0; i < taskModel.getTaskList().size(); i++)
            {
                this.singleTaskListViewModelList.add(new SingleTaskListViewModel(this));
                getSingleTaskViewModelList().get(i).setTaskHeaderLabel(i+1 + ": " + taskModel.getTaskList().get(i).getTaskHeader());
                getSingleTaskViewModelList().get(i).setTaskDesc(taskModel.getTaskList().get(i).getDescription());
                getSingleTaskViewModelList().get(i).setEstimationLabel("");
            }
        }

        //Refresh all the data in the nested viewControllers
        try {
            isTaskListEmptyProperty();
            taskWrapper.getChildren().clear();
            displayTaskData();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }


    private void displayTaskData() throws IOException {
        if(taskModel.getTaskList() != null) {
            int numberOfTasks = taskModel.getTaskList().size();
            taskControllerList.clear();

            for (int i = 0; i < numberOfTasks; i++) {
                if(i < this.getSingleTaskViewModelList().size())
                {
                    // Initialize a separate nested view and controller for each task:
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SingleTaskListView.fxml"));
                    VBox newTask = fxmlLoader.load();
                    ((SingleTaskListViewController) fxmlLoader.getController()).initialize(this.getSingleTaskViewModelList().get(i));
                    taskControllerList.add(fxmlLoader.getController());

                    // Assign the created controller to this class list of nested controllers:
                    taskWrapper.getChildren().add(newTask);

                    // Assign the necessary property bindings:
                    taskControllerList.get(i).getTaskHeaderLabel().textProperty().bindBidirectional(this.getSingleTaskViewModelList().get(i).getTaskHeaderLabelProperty());
                    taskControllerList.get(i).getTaskDescLabel().textProperty().bindBidirectional(this.getSingleTaskViewModelList().get(i).getTaskDescProperty());
                    taskControllerList.get(i).getIsBeingEstimatedLabel().textProperty().bindBidirectional(this.getSingleTaskViewModelList().get(i).getEstimationLabel());

                    // Apply any previous formatting, in the case where we are refreshing a previously loaded list:
                    this.getSingleTaskViewModelList().get(i).reApplyApplicableStyle();

                    // If this task is currently being estimated on, apply a marker, so it is visually identified that this task is being estimated on:
                    if (ViewModelFactory.getInstance().getGameViewModel().taskHeaderPropertyProperty().getValue().equals(taskModel.getTaskList().get(i).getTaskHeader())
                        && ViewModelFactory.getInstance().getGameViewModel().taskDescPropertyProperty().getValue().equals(taskModel.getTaskList().get(i).getDescription()))
                    {
                        this.getSingleTaskViewModelList().get(i).getEstimationLabel().setValue("-> ");
                    }
                }
            }
        }
    }


    public Property<String> sessionIdProperty() {
        return sessionId;
    }


    public Property<String> labelUserIdProperty() {
        return labelUserId;
    }


    public ArrayList<SingleTaskListViewModel> getSingleTaskViewModelList() {
        return this.singleTaskListViewModelList;
    }


    public void setSingleTaskViewModelList(ArrayList<SingleTaskListViewModel> singleTaskListViewModelList) {
        this.singleTaskListViewModelList = singleTaskListViewModelList;
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


    public void setSelectedTask(String taskHeader, String taskDescription) {
        // Check if task exists in list, before setting:
        for (Task task : taskModel.getTaskList()) {
            if(task.getTaskHeader().equals(taskHeader) && task.getDescription().equals(taskDescription)) {
                this.selectedTask = task;
                this.enableEditButton();
                return;
            }
        }
        this.disableEditButton();
        this.selectedTask = null;
    }

    public Task getSelectedTask() {
        return this.selectedTask;
    }

    public void resetTaskStyles() {
        for (SingleTaskListViewModel singleTaskListViewModel : singleTaskListViewModelList) {
            if(selectedTask != null) {
                int index_of_added_char = singleTaskListViewModel.getTaskHeaderLabelProperty().getValue().indexOf(':');
                String taskHeader = singleTaskListViewModel.getTaskHeaderLabelProperty().getValue().substring(index_of_added_char+2);

                if(!taskHeader.equals(selectedTask.getTaskHeader())) {
                    singleTaskListViewModel.resetStyle();
                }
            } else {
                singleTaskListViewModel.resetStyle();
            }
        }
    }


    /** Creates a pop-up window where the user can enter a new task into.*/
    public void createTask() {
        //Create the viewController
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ManageSingleTaskView.fxml"));

        //Create the popup screen as a new stage, and show it!
        Stage stage = new Stage();
        stage.setTitle("Opret Task");
        try {
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);

            // Notify the controller that it is in "add task" mode:
            ((ManageSingleTaskViewController) fxmlLoader.getController()).isEditModeActive(false);

            stage.show();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    /** Creates a pop-up window where the user can edit the task information.*/
    public void editTask() {
        //Create the viewController
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ManageSingleTaskView.fxml"));

        //Create the popup screen as a new stage, and show it!
        Stage stage = new Stage();
        try {
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);

            // Load task data into view:
            ((ManageSingleTaskViewController) fxmlLoader.getController()).textFieldTaskHeader.setText(this.selectedTask.getTaskHeader());
            ((ManageSingleTaskViewController) fxmlLoader.getController()).textAreaTaskDescription.setText(this.selectedTask.getDescription());

            // Notify the controller that it is in "edit task" mode, and send along the current task which is requested to be edited:
            ((ManageSingleTaskViewController) fxmlLoader.getController()).isEditModeActive(true, this.getSelectedTask());

            // Validate data:
            ((ManageSingleTaskViewController) fxmlLoader.getController()).validateData();

            stage.setTitle("Edit task '" + this.selectedTask.getTaskHeader() + "'");
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
