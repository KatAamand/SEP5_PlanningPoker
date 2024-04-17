package Views.TaskView;

import Application.ModelFactory;
import DataTypes.Task;
import Model.Task.TaskModel;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.rmi.RemoteException;

public class AddTaskViewModel
{
  private Button saveButtonRef;
  private Property<String> textFieldTaskHeader;
  private Property<String> textAreaTaskDescription;
  private TaskModel taskModel;

  public AddTaskViewModel(Button saveButtonRef)
  {
    try
    {
      taskModel = ModelFactory.getInstance().getTaskModel();
    }
    catch (RemoteException e)
    {
      //TODO: Add proper exception handling.
      e.printStackTrace();
    }



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

  public void validateData()
  {
    if(textFieldTaskHeader.getValue().isEmpty() || textAreaTaskDescription.getValue().isEmpty())
    {
      disableSaveButton();
    }
    else
    {
      enableSaveButton();
    }
  }


  public void save(ActionEvent event)
  {
    taskModel.addTask(new Task(textFieldTaskHeaderProperty().getValue(), textAreaTaskDescriptionProperty().getValue()));

    //Close the popup window after adding the task to the system:
    cancel(event);
  }

  public void cancel(ActionEvent event)
  {
    ((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
  }
}
