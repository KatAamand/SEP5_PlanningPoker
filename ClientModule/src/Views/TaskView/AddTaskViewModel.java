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


  public AddTaskViewModel(Button saveButtonRef) throws RemoteException {
    taskModel = ModelFactory.getInstance().getTaskModel();
    textFieldTaskHeader = new SimpleStringProperty();
    textAreaTaskDescription = new SimpleStringProperty();

    this.saveButtonRef = saveButtonRef;
    disableSaveButton();
  }


  private void enableSaveButton() {
    saveButtonRef.setDisable(false);
  }


  private void disableSaveButton() {
    saveButtonRef.setDisable(true);
  }


  public Property<String> textFieldTaskHeaderProperty() {
    return textFieldTaskHeader;
  }


  public Property<String> textAreaTaskDescriptionProperty() {
    return textAreaTaskDescription;
  }


  public void validateData() {
    if(textFieldTaskHeader.getValue().isEmpty() || textAreaTaskDescription.getValue().isEmpty())
    {
      disableSaveButton();
    }
    else
    {
      enableSaveButton();
    }
  }


  public void saveStandalone(ActionEvent event) {
    taskModel.addTask(new Task(textFieldTaskHeaderProperty().getValue(), textAreaTaskDescriptionProperty().getValue()));

    //Close the popup window after adding the task to the system:
    cancelStandalone(event);
  }

  public void saveEmbedded(ActionEvent event) {
    taskModel.addTask(new Task(textFieldTaskHeaderProperty().getValue(), textAreaTaskDescriptionProperty().getValue()));

    //Reset the data entry fields:
    cancelEmbedded(event);
  }


  public void cancelStandalone(ActionEvent event) {
    ((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
  }

  public void cancelEmbedded(ActionEvent event) {
    this.textFieldTaskHeaderProperty().setValue("");
    this.textAreaTaskDescriptionProperty().setValue("");
    this.validateData();
  }
}
