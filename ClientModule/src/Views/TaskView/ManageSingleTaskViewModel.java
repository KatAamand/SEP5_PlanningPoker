package Views.TaskView;

import Application.ModelFactory;
import Application.Session;
import DataTypes.Task;
import DataTypes.User;
import DataTypes.UserRoles.UserPermission;
import Model.Task.TaskModel;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class ManageSingleTaskViewModel
{
  private Button saveButtonRef;
  private Button deleteButtonRef;
  private Button cancelButtonRef;
  private Property<String> textFieldTaskHeaderProperty;
  private Property<String> textAreaTaskDescriptionProperty;
  private TaskModel taskModel;
  private StringProperty taskHeaderErrorMessage = new SimpleStringProperty();
  private TextField textFieldTaskHeader;
  private TextArea textAreaTaskDescription;
  private Task uneditedTask; // Used to contain a reference to the unedited task, when doing edit / deletion operations.
  private boolean isEmbedded; // Used to determine proper actions whether this model is linked to an embedded view, or a standalone view in a popup-window.

  public ManageSingleTaskViewModel(Button saveButtonRef, Button deleteButtonRef, boolean isUserEditing, Button cancelButtonRef, TextField textFieldTaskHeader, TextArea textAreaTaskDescription) throws RemoteException
  {
    taskModel = ModelFactory.getInstance().getTaskModel();
    textFieldTaskHeaderProperty = new SimpleStringProperty("");
    textAreaTaskDescriptionProperty = new SimpleStringProperty("");
    isEmbedded = false;
    uneditedTask = null;

    this.saveButtonRef = saveButtonRef;
    this.deleteButtonRef = deleteButtonRef;
    this.cancelButtonRef = cancelButtonRef;
    this.setTextInputReferences(textFieldTaskHeader, textAreaTaskDescription);
    disableSaveButton();
    validateData(isUserEditing);
    refresh();
  }

  public void refresh() {
    disableAllPermissionBasedUIInteractionElements();
    enableSpecificUIPermissionBasedElements(Session.getCurrentUser());
  }

  public void disableAllPermissionBasedUIInteractionElements() {
    // Disable all Interaction based UI elements, in order to later re-enable them for each specific role:
    this.hideSaveButton();
    this.hideDeleteButton();
    this.hideCancelButton();
    this.disableHeaderInputField();
    this.disableDescriptionInputArea();
  }

  /** Used for enabling specific UI elements and interactions based on the users permissions */
  private void enableSpecificUIPermissionBasedElements(User user)
  {
    // Enable permission to SAVE tasks, if proper user permission exists:
    if (user.getRole().getPermissions().contains(UserPermission.CREATE_TASK) && user.getRole().getPermissions().contains(UserPermission.EDIT_TASK))
    {
      this.showSaveButton();
      this.showCancelButton();
      this.enableCancelButton();
      this.enableHeaderInputField();
      this.textFieldTaskHeaderProperty().setValue("");
      this.enableDescriptionInputArea();
      this.textAreaTaskDescriptionProperty().setValue("");
    }
    else if (user.getRole().getPermissions().contains(UserPermission.EDIT_TASK) && this.isEmbedded())
    {
      // If the user only has edit permission, we ensure to disable the ability to add new tasks in the embedded mode:
      this.disableSaveButton();
      this.disableCancelButton();
      this.hideCancelButton();
      this.hideSaveButton();

      // Also change the text to reflect that the current user cannot and should not be adding tasks in the lobby view:
      this.textFieldTaskHeaderProperty().setValue("Please stand by while the Product Owner adds Tasks to this game");
      this.textAreaTaskDescriptionProperty().setValue("Please stand by\n\nDid you know that TYPEWRITER is the longest word you can type using only the letters on one row of the keyboard. (And no … QWERTYUIOP is not a word!)");

    }
    else if (user.getRole().getPermissions().contains(UserPermission.EDIT_TASK))
    {
      // If the user only has edit permission, and the task is not embedded, enable the cancel and save buttons:
      this.showSaveButton();
      this.enableSaveButton();
      this.showCancelButton();
      this.enableCancelButton();
      this.enableHeaderInputField();
      this.enableDescriptionInputArea();

    }
    else if (!this.isEmbedded())
    {
      // If user does not have any of the above permissions, user is only allowed to view task details:
      this.showCancelButton();
      this.enableCancelButton();

    }
    else
    {
      // Also change the text to reflect that the current user cannot and should not be adding tasks in the lobby view:
      this.textFieldTaskHeaderProperty().setValue("Please stand by while the Product Owner adds Tasks to this game");
      this.textAreaTaskDescriptionProperty().setValue("Please stand by\n\nDid you know that TYPEWRITER is the longest word you can type using only the letters on one row of the keyboard. (And no … QWERTYUIOP is not a word!)");
    }


  // Enable permission to DELETE tasks, if proper user permission exists:
    if(user.getRole().getPermissions().contains(UserPermission.DELETE_TASK)) {
      showDeleteButton();
    }
  }

  public void setTextInputReferences(TextField textFieldTaskHeader, TextArea textAreaTaskDescription) {
    this.textFieldTaskHeader = textFieldTaskHeader;
    this.textAreaTaskDescription = textAreaTaskDescription;
  }

  private void hideSaveButton() {
    this.saveButtonRef.setVisible(false);
  }

  private void showSaveButton() {
    this.saveButtonRef.setVisible(true);
  }

  private void enableCancelButton()
  {
    cancelButtonRef.setDisable(false);
  }

  private void disableCancelButton()
  {
    cancelButtonRef.setDisable(true);
  }

  private void hideCancelButton() {
    this.cancelButtonRef.setVisible(false);
  }

  private void showCancelButton() {
    this.cancelButtonRef.setVisible(true);
  }

  private void enableSaveButton()
  {
    saveButtonRef.setDisable(false);
  }

  private void showDeleteButton() {
    this.deleteButtonRef.setVisible(true);
  }

  private void disableHeaderInputField() {
    this.textFieldTaskHeader.setEditable(false);
  }

  private void enableHeaderInputField() {
    this.textFieldTaskHeader.setEditable(true);
  }

  private void disableDescriptionInputArea() {
    this.textAreaTaskDescription.setEditable(false);
  }

  private void enableDescriptionInputArea() {
    this.textAreaTaskDescription.setEditable(true);
  }

  public void setEmbedded(boolean state) {
    this.isEmbedded = state;
    this.refresh();
  }

  public boolean isEmbedded() {
    return this.isEmbedded;
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
    return textFieldTaskHeaderProperty;
  }

  public Property<String> textAreaTaskDescriptionProperty()
  {
    return textAreaTaskDescriptionProperty;
  }

  public StringProperty taskHeaderErrorMessageProperty()
  {
    return taskHeaderErrorMessage;
  }


  public boolean validateData(boolean isUserEditing) {
    ArrayList<Task> taskList = taskModel.getTaskList();
    String headerValue = textFieldTaskHeaderProperty.getValue();
    boolean isValid = true;

    for (Task task : taskList) {
      if (task.getTaskHeader().equalsIgnoreCase(textFieldTaskHeaderProperty.getValue())) {

        if(!isUserEditing) {
          // If we are creating an entirely new task, this is run.
          taskHeaderErrorMessage.set("Task already exists");
          hideDeleteButton();
          disableSaveButton();
          isValid = false;
        } else {
          // If we are editing an existing task, this is run.
          if(uneditedTask.getTaskHeader().equals(textFieldTaskHeaderProperty.getValue())) {
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
