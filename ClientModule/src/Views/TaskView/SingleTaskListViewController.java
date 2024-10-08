package Views.TaskView;

import Application.ViewFactory;
import Views.PlanningPokerView.PlanningPokerViewController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class SingleTaskListViewController
{
  @FXML private Label isBeingEstimatedLabel; // Value is set in the TaskViewModel after this controller is created.
  @FXML private Label taskHeaderLabel;
  @FXML private Label taskDescLabel;
  @FXML private Label finalEffortLabel;
  @FXML private VBox sourceVBox;
  private SingleTaskListViewModel viewModel;


  public SingleTaskListViewController() {
    //Does nothing at the moment
  }


  public void initialize(SingleTaskListViewModel viewModel) {
    this.viewModel = viewModel;
    Platform.runLater(() -> this.viewModel.setCurrentSource(sourceVBox));

    //Assign the necessary property bindings:
    assignBindings();
  }

  private void assignBindings() {
    this.getTaskHeaderLabel().textProperty().bindBidirectional(viewModel.getTaskHeaderLabelProperty());
    this.getTaskDescLabel().textProperty().bindBidirectional(viewModel.getTaskDescProperty());
    this.getIsBeingEstimatedLabel().textProperty().bindBidirectional(viewModel.getEstimationLabel());
    this.getFinalEffortLabel().textProperty().bindBidirectional(viewModel.getFinalEffortLabelProperty());
  }


  public void onTaskSelected(MouseEvent evt)
  {
    viewModel.selectTask((VBox) evt.getSource());

    // Cause connected GameView to refresh the displayed task information:
    ViewFactory.getInstance().getPlanningPokerViewController().refreshDisplayedTaskInfo();
  }


  public void onMouseEntered(MouseEvent evt)
  {
    viewModel.onHoverEntryTask((VBox) evt.getSource());
  }


  public void onMouseExited(MouseEvent evt)
  {
    viewModel.onHoverExitTask((VBox) evt.getSource());
  }


  public Label getTaskHeaderLabel() {
    return this.taskHeaderLabel;
  }


  public Label getTaskDescLabel() {
    return this.taskDescLabel;
  }

  public Label getIsBeingEstimatedLabel() {
    return this.isBeingEstimatedLabel;
  }

  public Label getFinalEffortLabel()
  {
    return finalEffortLabel;
  }


}
