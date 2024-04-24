package Views.TaskView;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

public class AddTaskViewController implements Initializable
{
  @FXML private Button buttonSave;
  @FXML private TextField textFieldTaskHeader;
  @FXML private TextArea textAreaTaskDescription;
  private AddTaskViewModel viewModel;
  private boolean isEmbedded; //Used to distinguish between whether this controller is initialized in a popup window, or embedded into the main view.


  public AddTaskViewController() {
    isEmbedded = false;
  }


  @FXML public void validateData() {
    viewModel.validateData();
  }


  @FXML public void onSavePressed(ActionEvent event) {
    if(isEmbedded) {
      viewModel.saveEmbedded(event);
    } else {
      viewModel.saveStandalone(event);
    }
  }


  @FXML public void onCancelPressed(ActionEvent event) {
    if(isEmbedded) {
      viewModel.cancelEmbedded(event);
    } else {
      viewModel.cancelStandalone(event);
    }
  }

  /** Used to control the state of this controller. True, this controller will act as if embedded inside another view. False, this controller will act as if it is its own window.*/
  public void isEmbedded(boolean state) {
    this.isEmbedded = state;
  }


  @Override public void initialize(URL location, ResourceBundle resources) {
    textAreaTaskDescription.setStyle("-fx-background-color:  #E5FAE4");
    try
    {
      viewModel = new AddTaskViewModel(buttonSave);

      //Create bindings
      viewModel.textFieldTaskHeaderProperty().bindBidirectional(textFieldTaskHeader.textProperty());
      viewModel.textAreaTaskDescriptionProperty().bindBidirectional(textAreaTaskDescription.textProperty());
    }
    catch (RemoteException e)
    {
      System.out.println("ERROR: Occurred in AddTaskViewController.java while attempting to initialize its assigned ViewModel");
      e.printStackTrace();
    }
  }
}
