package Views.TaskView;

import Application.ModelFactory;
import Application.Session;
import Application.ViewFactory;
import Application.ViewModelFactory;
import DataTypes.RuleSet;
import DataTypes.Task;
import DataTypes.User;
import DataTypes.UserRoles.UserPermission;
import Model.Task.TaskModel;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class TaskViewModel
{
  private TaskModel taskModel;
  private Button btnEditTask;
  private Button btnCreateTask;
  private Button btnExportTaskList;
  private Button btnImportTaskList;
  private Button btnRuleSet;
  private VBox taskWrapper;
  private Property<String> sessionId;
  private Property<String> labelUserId;
  private ArrayList<SingleTaskListViewController> taskControllerList;
  private ArrayList<SingleTaskListViewModel> singleTaskListViewModelList;
  private BooleanProperty isTaskListEmpty;
  private Task selectedTask; // Holds the currently selected task, or null if no task is selected.

  public TaskViewModel() throws RemoteException
  {
    setSingleTaskViewModelList(new ArrayList<>());
    taskControllerList = new ArrayList<>();
    sessionId = new SimpleStringProperty("UNDEFINED");
    labelUserId = new SimpleStringProperty("UNDEFINED");
    this.taskModel = ModelFactory.getInstance().getTaskModel();
    this.selectedTask = null;

    this.isTaskListEmpty = new SimpleBooleanProperty(true);

    updateTaskListEmptyProperty(); // updates upon creation
  }

  public void initialize(Button btnCreateTask, Button btnEditTask,
      Button btnExportTaskList, Button btnRuleSet, Button btnImportTaskList, VBox taskWrapper
      )
  {
    setButtonReferences(btnCreateTask, btnEditTask, btnExportTaskList, btnRuleSet, btnImportTaskList);
    assignButtonMethods();
    this.taskWrapper = taskWrapper;
    disableEditButton();
    Platform.runLater(this::refresh);

    //Assign listeners:
    taskModel.addPropertyChangeListener("taskListUpdated", evt -> {
      Platform.runLater(this::refresh);
      // Add a listener to the taskModel to update the isTaskListEmptyProperty when the taskList is updated
      Platform.runLater(this::updateTaskListEmptyProperty);
    });

    taskModel.addPropertyChangeListener("PlanningPokerObjUpdated", evt -> {
      Platform.runLater(() -> {
        this.disableAllPermissionBasedUIInteractionElements();
        this.enableSpecificUIPermissionBasedElements(Session.getCurrentUser());
      });
    });

    taskModel.addPropertyChangeListener("TaskRemoved", evt -> {
      Platform.runLater(() -> {
        this.disableAllPermissionBasedUIInteractionElements();
        if(this.getSelectedTask() != null && this.getSelectedTask().getTaskHeader().equals(((Task) evt.getNewValue()).getTaskHeader())) {
          // Removed task was the currently selected task. Reset selectedTask to null:
          this.setSelectedTask(null,null);
        }
        this.enableSpecificUIPermissionBasedElements(Session.getCurrentUser());
        this.refresh();
        this.updateTaskListEmptyProperty();
      });
    });
  }

  public void disableAllPermissionBasedUIInteractionElements()
  {
    // Disable all Interaction based UI elements, in order to later re-enable them for each specific role:
    this.disableAndHideCreateButton();
  }

  /** Used for enabling specific UI elements and interactions based on the users permissions */
  private void enableSpecificUIPermissionBasedElements(User user)
  {
    // Enable permission to CREATE tasks, if proper user permission exists:
    if (user.getRole().getPermissions().contains(UserPermission.CREATE_TASK))
    {
      this.enableAndShowCreateButton();
    }

    // Enable permission to EDIT tasks, if proper user permission exists:
    if (user.getRole().getPermissions().contains(UserPermission.EDIT_TASK))
    {
      this.btnEditTask.setText("Edit Task");
    }
    else
    {
      //Change the EDIT button into a SHOW DETAILS button, so that connected users can read project descriptions, etc. for tasks.
      this.btnEditTask.setText("Show Details");
    }
  }

  // Makes sure to update isTaskListEmptyProperty when the taskList is updated
  private void updateTaskListEmptyProperty()
  {
    isTaskListEmpty.set(taskModel.getTaskList().isEmpty());
  }

  public ReadOnlyBooleanProperty isTaskListEmptyProperty()
  {
    return isTaskListEmpty;
  }

  public void assignButtonMethods()
  {
    btnEditTask.setOnAction(event -> this.editTask());
    btnCreateTask.setOnAction(event -> this.createTask());
    btnRuleSet.setOnAction(event -> this.showRuleSetBox());
    btnExportTaskList.setOnAction(event -> this.exportTaskList());
    btnImportTaskList.setOnAction(event -> this.importTaskList());
    btnExportTaskList.setOnAction(event -> {
      // Check that the local user has the proper permissions to export task list.
      if (Session.getCurrentUser().getRole().getPermissions()
          .contains(UserPermission.EXPORT_TASKLIST))
      {
        // Export the task list.
        this.exportTaskList();
      }
    });
  }

  public void refresh()
  {
    // Disable UI elements that are not available to the user, based on the users role / permissions:
    this.disableAllPermissionBasedUIInteractionElements();

    // Enable specific UI elements based on user role:
    this.enableSpecificUIPermissionBasedElements(Session.getCurrentUser());

    //Refresh the ViewModels inside the singleTaskListViewModelList
    setSingleTaskViewModelList(new ArrayList<>());

    if (taskModel.getTaskList() != null)
    {
      for (int i = 0; i < taskModel.getTaskList().size(); i++)
      {
        this.singleTaskListViewModelList.add(new SingleTaskListViewModel(this));
        getSingleTaskViewModelList().get(i).setTaskHeaderLabel(
            i + 1 + ": " + taskModel.getTaskList().get(i).getTaskHeader());
        getSingleTaskViewModelList().get(i)
            .setTaskDesc(taskModel.getTaskList().get(i).getDescription());
        getSingleTaskViewModelList().get(i).setEstimationLabel("");
        getSingleTaskViewModelList().get(i).setFinalEffortLabel(
            taskModel.getTaskList().get(i).getFinalEffort());

        // Check if this task is already selected. If yes, update the selected task:
        if (this.getSelectedTask() != null && this.getSelectedTask()
            .getTaskHeader()
            .equals(taskModel.getTaskList().get(i).getTaskHeader()))
        {
          this.setSelectedTask(taskModel.getTaskList().get(i).getTaskHeader(),
              taskModel.getTaskList().get(i).getDescription());
        }
      }
    }

    // Refresh all the data in the nested viewControllers
    try
    {
      isTaskListEmptyProperty();
      taskWrapper.getChildren().clear();
      displayTaskData();
    }
    catch (IOException e)
    {
      throw new RuntimeException();
    }

    // Refresh the game view, in the case where the user has manually selected another task from the list to view:
    ViewFactory.getInstance().getPlanningPokerViewController()
        .refreshDisplayedTaskInfo();
  }

  private synchronized void displayTaskData() throws IOException
  {
    if (taskModel.getTaskList() != null)
    {
      int numberOfTasks = taskModel.getTaskList().size();
      taskControllerList.clear();

      for (int i = 0; i < numberOfTasks; i++)
      {
        if (i < this.getSingleTaskViewModelList().size())
        {
          // Initialize a separate nested view and controller for each task:
          FXMLLoader fxmlLoader = new FXMLLoader(
              getClass().getResource("SingleTaskListView.fxml"));
          VBox newTask = fxmlLoader.load();
          ((SingleTaskListViewController) fxmlLoader.getController()).initialize(
              this.getSingleTaskViewModelList().get(i));
          taskControllerList.add(fxmlLoader.getController());

          // Assign the created controller to this class list of nested controllers:
          taskWrapper.getChildren().add(newTask);

          // Assign the necessary property bindings:
          taskControllerList.get(i).getTaskHeaderLabel().textProperty()
              .bindBidirectional(this.getSingleTaskViewModelList().get(i)
                  .getTaskHeaderLabelProperty());
          taskControllerList.get(i).getTaskDescLabel().textProperty()
              .bindBidirectional(this.getSingleTaskViewModelList().get(i)
                  .getTaskDescProperty());
          taskControllerList.get(i).getIsBeingEstimatedLabel().textProperty()
              .bindBidirectional(this.getSingleTaskViewModelList().get(i)
                  .getEstimationLabel());
          taskControllerList.get(i).getFinalEffortLabel().textProperty()
              .bindBidirectional(this.getSingleTaskViewModelList().get(i)
                  .getFinalEffortLabelProperty());

          // Apply any previous formatting, in the case where we are refreshing a previously loaded list:
          this.getSingleTaskViewModelList().get(i).reApplyApplicableStyle();

          // If this task is currently being estimated on, apply a marker, so it is visually identified that this task is being estimated on:
          if ((ViewModelFactory.getInstance().getGameViewModel()
              .getTaskBeingEstimated() != null))
          {
            if (ViewModelFactory.getInstance().getGameViewModel()
                .getTaskBeingEstimated().getTaskHeader()
                .equals(taskModel.getTaskList().get(i).getTaskHeader()))
            {
              this.getSingleTaskViewModelList().get(i).getEstimationLabel()
                  .setValue("-> ");
            }
          }
        }
      }
    }
  }

  public Property<String> sessionIdProperty()
  {
    return sessionId;
  }

  public Property<String> labelUserIdProperty()
  {
    return labelUserId;
  }

  public ArrayList<SingleTaskListViewModel> getSingleTaskViewModelList()
  {
    return this.singleTaskListViewModelList;
  }

  public void setSingleTaskViewModelList(
      ArrayList<SingleTaskListViewModel> singleTaskListViewModelList)
  {
    this.singleTaskListViewModelList = singleTaskListViewModelList;
  }

  private void setButtonReferences(Button btnCreateTask, Button btnEditTask,
      Button btnExportTaskList, Button btnRuleSet, Button btnImportTaskList)
  {
    this.btnCreateTask = btnCreateTask;
    this.btnEditTask = btnEditTask;
    this.btnExportTaskList = btnExportTaskList;
    this.btnImportTaskList = btnImportTaskList;
    this.btnRuleSet = btnRuleSet;
  }

  private void disableEditButton()
  {
    btnEditTask.setDisable(true);
  }

  private void enableEditButton()
  {
    this.btnEditTask.setDisable(false);
  }

  private void disableCreateButton()
  {
    btnCreateTask.setDisable(true);
  }

  private void disableAndHideCreateButton()
  {
    this.disableCreateButton();
    this.btnCreateTask.setVisible(false);

  }

  private void enableAndShowCreateButton()
  {
    this.btnCreateTask.setDisable(false);
    this.btnCreateTask.setVisible(true);
  }

  public void setSelectedTask(String taskHeader, String taskDescription)
  {
    // Check if task exists in list, before setting:
    for (Task task : taskModel.getTaskList())
    {
      if (task.getTaskHeader().equals(taskHeader) && task.getDescription()
          .equals(taskDescription))
      {
        this.selectedTask = task.copy();
        this.enableEditButton();
        return;
      }
    }
    this.disableEditButton();
    this.selectedTask = null;
  }

  public Task getSelectedTask()
  {
    return this.selectedTask;
  }

  public void resetTaskStyles()
  {
    for (SingleTaskListViewModel singleTaskListViewModel : singleTaskListViewModelList)
    {
      if (this.getSelectedTask() != null)
      {
        int index_of_added_char = singleTaskListViewModel.getTaskHeaderLabelProperty()
            .getValue().indexOf(':');
        String taskHeader = singleTaskListViewModel.getTaskHeaderLabelProperty()
            .getValue().substring(index_of_added_char + 2);

        if (!taskHeader.equals(this.getSelectedTask().getTaskHeader()))
        {
          singleTaskListViewModel.resetStyle();
        }
      }
      else
      {
        singleTaskListViewModel.resetStyle();
      }
    }
  }

  /** Creates a pop-up window where the user can enter a new task into. */
  public void createTask()
  {
    //Create the viewController
    FXMLLoader fxmlLoader = new FXMLLoader(
        getClass().getResource("ManageSingleTaskView.fxml"));

    //Create the popup screen as a new stage, and show it!
    Stage stage = new Stage();
    stage.setTitle("Opret Task");
    try
    {
      Scene scene = new Scene(fxmlLoader.load());
      stage.setScene(scene);
      stage.setResizable(false);
      stage.initModality(Modality.APPLICATION_MODAL);

      // Notify the controller that it is in "add task" mode:
      ((ManageSingleTaskViewController) fxmlLoader.getController()).isEditModeActive(
          false);

      stage.show();
    }
    catch (IOException e)
    {
      throw new RuntimeException();
    }
  }

  /** Creates a pop-up window where the user can edit the task information. */
  public void editTask()
  {
    //Create the viewController
    FXMLLoader fxmlLoader = new FXMLLoader(
        getClass().getResource("ManageSingleTaskView.fxml"));

    //Create the popup screen as a new stage, and show it!
    Stage stage = new Stage();
    try
    {
      Scene scene = new Scene(fxmlLoader.load());
      stage.setScene(scene);
      stage.setResizable(false);
      stage.initModality(Modality.APPLICATION_MODAL);

      // Load task data into view:
      ((ManageSingleTaskViewController) fxmlLoader.getController()).textFieldTaskHeader.setText(
          this.getSelectedTask().getTaskHeader());
      ((ManageSingleTaskViewController) fxmlLoader.getController()).textAreaTaskDescription.setText(
          this.getSelectedTask().getDescription());

      // Notify the controller that it is in "edit task" mode, and send along the current task which is requested to be edited:
      ((ManageSingleTaskViewController) fxmlLoader.getController()).isEditModeActive(
          true, this.getSelectedTask());

      // Validate data:
      ((ManageSingleTaskViewController) fxmlLoader.getController()).validateData();

      stage.setTitle(
          "Edit task '" + this.getSelectedTask().getTaskHeader() + "'");
      stage.show();
    }
    catch (IOException e)
    {
      throw new RuntimeException();
    }
  }

  public void showRuleSetBox()
  {
    RuleSet rulesSet = new RuleSet();

    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(rulesSet.getHeader());
    alert.setHeaderText(null);
    alert.setContentText(rulesSet.getBody());
    alert.showAndWait();
  }

  /**
   * exportTaskList exports a .csv-file locally of the current tasklist. /
   */
  public void exportTaskList()
  {
    List<Task> tasks = taskModel.getTaskList();

    String fileName = "Tasklist.csv";

    try (PrintWriter writer = new PrintWriter(new FileWriter(fileName)))
    {
      writer.println("Header, Description, Final effort");

      for (Task task : tasks)
      {
        writer.println(task.getTaskHeader() + "," + task.getDescription() + ","
            + task.getFinalEffort());
      }
      Alert alert = new Alert((Alert.AlertType.INFORMATION));
      alert.setTitle("Export successful");
      alert.setHeaderText(null);
      alert.setContentText(
          "Tasklist has been exported succesfully with filename: " + fileName);
      alert.showAndWait();
    }
    catch (IOException e)
    {
      e.printStackTrace();
      Alert alert = new Alert((Alert.AlertType.INFORMATION));
      alert.setTitle("Export failed");
      alert.setHeaderText(null);
      alert.setContentText("Tasklist failed to export, please try again.");
      alert.showAndWait();
    }
  }

  /** allows for import of a csv-file containing a tasklist. Tasks will be inserted in current tasklist after current tasks, in tasklists. */
  public void importTaskList() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Please open tasklist file.");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("csv-files", "*.csv"));

    File selectedFile = fileChooser.showOpenDialog(null);
    if (selectedFile != null) {
      try (BufferedReader bufferedReader = new BufferedReader(new FileReader(selectedFile))) {
        String line;
        List<Task> importedTasks = new ArrayList<>();
        bufferedReader.readLine();

        while ((line = bufferedReader.readLine()) != null) {
          String[] values = line.split(",");
          if (values.length >= 2) {
            String header = values[0].trim();
            String description = values[1].trim();
            String finalEffort = (values.length > 2) ? values[2].trim() : "";

            try {

              Task newTask = new Task(header, description);

              if(finalEffort == null || finalEffort.isEmpty()) {
                newTask.setFinalEffort(null);
              } else {
                newTask.setFinalEffort(finalEffort);
              }

              importedTasks.add(newTask);
            } catch (Exception e) {
              System.err.println("Error parsing task: " + line + ", Exception: " + e.getMessage());
            }
          } else {
            System.err.println("Invalid line format in file: " + line);
          }
        }

        for (Task task : importedTasks) {
          try {
            taskModel.addTask(task);
            System.out.println("TaskViewModel: Adding task: " + task.getTaskHeader() + ", " + task.getDescription());
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        }

        refresh();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Import successful");
        alert.setHeaderText(null);
        alert.setContentText("Tasklist has been imported successfully.");
        alert.showAndWait();

      } catch (IOException e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Import failed");
        alert.setHeaderText(null);
        alert.setContentText("Tasklist failed to import, please try again.");
        alert.showAndWait();
      }
    }
  }

}
