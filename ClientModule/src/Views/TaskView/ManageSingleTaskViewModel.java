package Views.TaskView;

import Application.ModelFactory;
import DataTypes.Task;
import DataTypes.User;
import DataTypes.UserRoles.UserPermission;
import Model.Task.TaskModel;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class ManageSingleTaskViewModel
{
  private Button saveButtonRef;
  private Button deleteButtonRef;
  private Property<String> textFieldTaskHeader;
  private Property<String> textAreaTaskDescription;
  private TaskModel taskModel;
  private StringProperty taskHeaderErrorMessage = new SimpleStringProperty();
  private Task uneditedTask; // Used to contain a reference to the unedited task, when doing edit / deletion operations.

  public ManageSingleTaskViewModel(Button saveButtonRef, Button deleteButtonRef, boolean isUserEditing) throws RemoteException
  {
    taskModel = ModelFactory.getInstance().getTaskModel();
    textFieldTaskHeader = new SimpleStringProperty("");
    textAreaTaskDescription = new SimpleStringProperty("");
    uneditedTask = null;

    this.saveButtonRef = saveButtonRef;
    this.deleteButtonRef = deleteButtonRef;
    disableSaveButton();
    validateData(isUserEditing);
  }

  /** Used for enabling specific UI elements and interactions based on the users permissions */
  private void enableSpecificUIPermissionBasedElements(User user)
  {
    // Enable permission to CREATE tasks, if proper user permission exists:
    if(user.getRole().getPermissions().contains(UserPermission.CREATE_TASK)) {
      // TODO: Not implemented yet.
    }

    // Enable permission to EDIT tasks, if proper user permission exists:
    if(user.getRole().getPermissions().contains(UserPermission.EDIT_TASK)) {
      // TODO: Not implemented yet.
    }

    // Enable permission to DELETE tasks, if proper user permission exists:
    if(user.getRole().getPermissions().contains(UserPermission.DELETE_TASK)) {
      // TODO: Not implemented yet.
    }
  }

  private void enableSaveButton()
  {
    saveButtonRef.setDisable(false);
  }

  private void disableSaveButton()
  {
    saveButtonRef.setDisable(true);
  }

  public void hideDeleteButton() {
    deleteButtonRef.setVisible(false);
  }

  public Property<String> textFieldTaskHeaderProperty()
  {
    return textFieldTaskHeader;
  }

  public Property<String> textAreaTaskDescriptionProperty()
  {
    return textAreaTaskDescription;
  }

  public StringProperty taskHeaderErrorMessageProperty()
  {
    return taskHeaderErrorMessage;
  }


  public boolean validateData(boolean isUserEditing) {
    ArrayList<Task> taskList = taskModel.getTaskList();
    String headerValue = textFieldTaskHeader.getValue();
    boolean isValid = true;

    for (Task task : taskList) {
      if (task.getTaskHeader().equalsIgnoreCase(textFieldTaskHeader.getValue())) {

        if(!isUserEditing) {
          // If we are creating an entirely new task, this is run.
          taskHeaderErrorMessage.set("Task already exists");
          hideDeleteButton();
          disableSaveButton();
          isValid = false;
        } else {
          // If we are editing an existing task, this is run.
          if(uneditedTask.getTaskHeader().equals(textFieldTaskHeader.getValue())) {
            enableSaveButton();
          } else {
            taskHeaderErrorMessage.set("Task already exists");
            disableSaveButton();
            isValid = false;
          }
        }
        break;
      }
    }

    if (headerValue.isEmpty()) {
      disableSaveButton();
      isValid = false;
      taskHeaderErrorMessage.set("Header cannot be empty");
    }

    if (isValid) {
      taskHeaderErrorMessage.set("");
      enableSaveButton();
    }

    return isValid;
  }


  public void setUneditedTask(Task task) {
    this.uneditedTask = task;
  }


  public Task getUneditedTask() {
    return this.uneditedTask;
  }


  public void saveStandalone(ActionEvent event, boolean isUserEditing) {
    boolean saveSuccessful;

    if(!isUserEditing) {
      taskModel.addTask(new Task(textFieldTaskHeaderProperty().getValue(), textAreaTaskDescriptionProperty().getValue()));
      saveSuccessful = true;
    } else {
      Task newTask = new Task(textFieldTaskHeaderProperty().getValue(), textAreaTaskDescriptionProperty().getValue());
      saveSuccessful = taskModel.editTask(uneditedTask, newTask);
    }

    //Close the popup window after adding the task to the system:
    if(saveSuccessful) {
      cancelStandalone(event);
    } else {
      this.taskHeaderErrorMessage.setValue("ERROR: Failed to perform action");
    }
  }


  public void saveEmbedded(ActionEvent event, boolean isUserEditing) {
    boolean saveSuccessful;

    if(!isUserEditing) {
      taskModel.addTask(new Task(textFieldTaskHeaderProperty().getValue(), textAreaTaskDescriptionProperty().getValue()));
      saveSuccessful = true;
    } else {
      Task newTask = new Task(textFieldTaskHeaderProperty().getValue(), textAreaTaskDescriptionProperty().getValue());
      saveSuccessful = taskModel.editTask(uneditedTask, newTask);
    }

    //Reset the data entry fields:
    if(saveSuccessful) {
      cancelEmbedded(event, isUserEditing);
    } else {
      this.taskHeaderErrorMessage.setValue("ERROR: Failed to perform action");
    }
  }


  public boolean delete(boolean isUserEditing) {
    if(isUserEditing) {
      if(!taskModel.removeTask(this.getUneditedTask())) {
        taskHeaderErrorMessage.setValue("ERROR: Unable to remove selected task");
      } else {
        return true;
      }
    }
    return false;
  }


  public void cancelStandalone(ActionEvent event)
  {
    ((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
  }


  public void cancelEmbedded(ActionEvent event, boolean isUserEditing)
  {
    this.textFieldTaskHeaderProperty().setValue("");
    this.textAreaTaskDescriptionProperty().setValue("");
    this.validateData(isUserEditing);
  }
}
