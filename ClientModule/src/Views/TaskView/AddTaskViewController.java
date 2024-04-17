package Views.TaskView;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AddTaskViewController implements Initializable
{
  @FXML private Button buttonSave;
  @FXML private TextField textFieldTaskHeader;
  @FXML private TextArea textAreaTaskDescription;
  private AddTaskViewModel viewModel;

  public AddTaskViewController()
  {

  }

  @FXML public void validateData()
  {
    viewModel.validateData();
  }

  @FXML public void onSavePressed(ActionEvent event)
  {
    viewModel.save(event);
  }

  @FXML public void onCancelPressed(ActionEvent event)
  {
    viewModel.cancel(event);
  }

  @Override public void initialize(URL location, ResourceBundle resources)
  {
    textAreaTaskDescription.setStyle("-fx-background-color:  #E5FAE4");

    viewModel = new AddTaskViewModel(buttonSave);

    //Create bindings
    viewModel.textFieldTaskHeaderProperty().bindBidirectional(textFieldTaskHeader.textProperty());
    viewModel.textAreaTaskDescriptionProperty().bindBidirectional(textAreaTaskDescription.textProperty());
  }
}
