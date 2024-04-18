package Views.TaskView;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class SingleTaskViewController
{
  @FXML private Label taskHeaderLabel;
  @FXML private Label taskDescLabel;
  private int rowId;
  private SingleTaskViewModel viewModel;


  public SingleTaskViewController() {
    //Does nothing at the moment
  }


  public void initialize(int rowId, SingleTaskViewModel viewModel) {
    setRowId(rowId);
    this.viewModel = viewModel;

    //Assign the necessary property bindings:
    assignBindings();
  }


  private void assignBindings() {
    this.getTaskHeaderLabel().textProperty().bindBidirectional(viewModel.getTaskHeaderLabelProperty());
    this.getTaskDescLabel().textProperty().bindBidirectional(viewModel.getTaskDescProperty());
  }


  public void onTaskSelected(MouseEvent evt)
  {
    viewModel.selectTask((VBox) evt.getSource());
  }


  public void onMouseEntered(MouseEvent evt)
  {
    viewModel.onHoverEntryTask((VBox) evt.getSource());
  }


  public void onMouseExited(MouseEvent evt)
  {
    viewModel.onHoverExitTask((VBox) evt.getSource());
  }


  public int getRowId()
  {
    return this.rowId;
  }


  public void setRowId(int rowId)
  {
    this.rowId = rowId;
  }


  public Label getTaskHeaderLabel()
  {
    return this.taskHeaderLabel;
  }


  public Label getTaskDescLabel()
  {
    return this.taskDescLabel;
  }
}
