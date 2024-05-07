package Views.TaskView;

import Application.ModelFactory;
import DataTypes.Task;
import Model.Task.TaskModel;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class AddTaskViewModel
{
  private Button saveButtonRef;
  private Property<String> textFieldTaskHeader;
  private Property<String> textAreaTaskDescription;
  private TaskModel taskModel;
  private StringProperty tasknameErrorMessage = new SimpleStringProperty();

  public AddTaskViewModel(Button saveButtonRef) throws RemoteException
  {
    taskModel = ModelFactory.getInstance().getTaskModel();
    textFieldTaskHeader = new SimpleStringProperty();
    textAreaTaskDescription = new SimpleStringProperty();

    this.saveButtonRef = saveButtonRef;
    disableSaveButton();
  }

  private void enableSaveButton()
  {
    saveButtonRef.setDisable(false);
  }

  private void disableSaveButton()
  {
    saveButtonRef.setDisable(true);
  }

  public Property<String> textFieldTaskHeaderProperty()
  {
    return textFieldTaskHeader;
  }

  public Property<String> textAreaTaskDescriptionProperty()
  {
    return textAreaTaskDescription;
  }

  public StringProperty tasknameErrorMessageProperty()
  {
    return tasknameErrorMessage;
  }


  public boolean validateData()
  {
    ArrayList<Task> taskList = taskModel.getTaskList();
    String headerValue = textFieldTaskHeader.getValue();
    boolean isValid = true;

    for (Task task : taskList)
    {
      if (task.getTaskName().equalsIgnoreCase(textFieldTaskHeader.getValue()))
      {
        disableSaveButton();
        isValid = false;
        tasknameErrorMessage.set("Task already exists");
        break;
      }
    }

    if (headerValue.isEmpty())
    {
      disableSaveButton();
      isValid = false;
      tasknameErrorMessage.set("Header cannot be empty");
    }


    if (isValid)
    {
      tasknameErrorMessage.set("");
      enableSaveButton();
    }

    return isValid;

  }


  public void saveStandalone(ActionEvent event)
  {
    taskModel.addTask(new Task(textFieldTaskHeaderProperty().getValue(),
        textAreaTaskDescriptionProperty().getValue()));

    //Close the popup window after adding the task to the system:
    cancelStandalone(event);
  }

  public void saveEmbedded(ActionEvent event)
  {
    taskModel.addTask(new Task(textFieldTaskHeaderProperty().getValue(),
        textAreaTaskDescriptionProperty().getValue()));

    //Reset the data entry fields:
    cancelEmbedded(event);
  }

  public void cancelStandalone(ActionEvent event)
  {
    ((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
  }

  public void cancelEmbedded(ActionEvent event)
  {
    this.textFieldTaskHeaderProperty().setValue("");
    this.textAreaTaskDescriptionProperty().setValue("");
    this.validateData();
  }
}
