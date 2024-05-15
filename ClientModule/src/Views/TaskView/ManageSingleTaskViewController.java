package Views.TaskView;

import DataTypes.Task;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

public class ManageSingleTaskViewController implements Initializable
{
  @FXML private Button buttonCancel;
  @FXML private Button buttonSave;
  @FXML private Button buttonDelete;
  @FXML public TextField textFieldTaskHeader;
  @FXML public TextArea textAreaTaskDescription;
  @FXML public Label taskHeaderErrorLabel;
  private ManageSingleTaskViewModel manageSingleTaskViewModel;
  private boolean isEmbedded; //Used to distinguish between whether this controller is initialized in a popup window, or embedded into the main view.
  private boolean isUserEditing; //Used to distinguish between whether the user is editing an already existing task, or adding a new one.

  public ManageSingleTaskViewController() {
    isEmbedded = false;
    isUserEditing = false;
  }

  @FXML public boolean validateData() {
    return manageSingleTaskViewModel.validateData(isUserEditing);
  }

  @FXML public void onSavePressed(ActionEvent event) {
   validateData();
    if (isEmbedded) {
      manageSingleTaskViewModel.saveEmbedded(event, isUserEditing);
    } else {
      manageSingleTaskViewModel.saveStandalone(event, isUserEditing);
    }
  }

  @FXML public void onCancelPressed(ActionEvent event) {
  if (isEmbedded) {
    manageSingleTaskViewModel.cancelEmbedded(event, isUserEditing);
  } else {
    manageSingleTaskViewModel.cancelStandalone(event);
  }
}


  @FXML public void onDeletePressed(ActionEvent event) {
    if (!isEmbedded) {
      if(manageSingleTaskViewModel.delete(isUserEditing)) {
        this.onCancelPressed(event);
      }
    }
  }


  /** Used to control the state of this controller. True, this controller will act as if embedded inside another view. False, this controller will act as if it is its own window. */
  public void isEmbedded(boolean state) {
    this.isEmbedded = state;
    manageSingleTaskViewModel.setEmbedded(state);
  }


  /** Used to control whether the user is currently editing an already existing task, or adding a new one.<br>True = the user is currently editing a task */
  public void isEditModeActive(boolean state) {
    this.isUserEditing = state;
    manageSingleTaskViewModel.setUneditedTask(null);
  }


  /** Used to control whether the user is currently editing an already existing task, or adding a new one.<br>True = the user is currently editing a task */
  public void isEditModeActive(boolean state, Task uneditedTask) {
    this.isUserEditing = state;
    manageSingleTaskViewModel.setUneditedTask(uneditedTask);
  }


  @Override public void initialize(URL location, ResourceBundle resources) {
    textAreaTaskDescription.setStyle("-fx-background-color:  #E5FAE4");

    try {
      manageSingleTaskViewModel = new ManageSingleTaskViewModel(buttonSave, buttonDelete, isUserEditing, buttonCancel, textFieldTaskHeader, textAreaTaskDescription);

      Platform.runLater(() -> {
        if(this.isEmbedded || !this.isUserEditing) {
          manageSingleTaskViewModel.hideDeleteButton();
        }
      });

      //Create bindings
      manageSingleTaskViewModel.textFieldTaskHeaderProperty().bindBidirectional(textFieldTaskHeader.textProperty());
      manageSingleTaskViewModel.textAreaTaskDescriptionProperty().bindBidirectional(textAreaTaskDescription.textProperty());
      taskHeaderErrorLabel.textProperty().bind(manageSingleTaskViewModel.taskHeaderErrorMessageProperty());

      //Refresh view model:
      manageSingleTaskViewModel.validateData(isUserEditing);
      manageSingleTaskViewModel.refresh();

    } catch (RemoteException e) {
      System.out.println("ERROR: Occurred in ManageSingleTaskViewController.java while attempting to initialize its assigned ViewModel");
      e.printStackTrace();
    }
  }
}
