package Views.TaskView;

import Application.ViewFactory;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.VBox;

public class SingleTaskListViewModel
{
  private final Property<String> taskHeaderLabel;
  private final Property<String> taskDescLabel;
  private final Property<String> isBeingEstimatedLabel; // Value is set in the TaskViewModel after this controller is created.
  private final Property<String> finalEffortLabel;
  private final TaskViewModel parentViewModel;
  private VBox currentSource;


  public SingleTaskListViewModel(TaskViewModel parentViewModel) {
    taskHeaderLabel = new SimpleStringProperty("");
    taskDescLabel = new SimpleStringProperty("");
    isBeingEstimatedLabel = new SimpleStringProperty("");
    finalEffortLabel = new SimpleStringProperty();
    this.parentViewModel = parentViewModel;
    this.currentSource = null;
  }


  public void setTaskHeaderLabel(String taskHeader)
  {
    this.taskHeaderLabel.setValue(taskHeader);
  }


  public Property<String> getTaskHeaderLabelProperty()
  {
    return taskHeaderLabel;
  }

  public Property<String> getFinalEffortLabelProperty()
  {
    return finalEffortLabel;
  }

  public void setFinalEffortLabel(String finalEffortLabel)
  {
    if(finalEffortLabel != null) {
      this.finalEffortLabel.setValue("effort: " + finalEffortLabel);
    } else {
      this.finalEffortLabel.setValue("");
    }
  }

  public void setTaskDesc(String taskDesc)
  {
    this.taskDescLabel.setValue(taskDesc);
  }


  public Property<String> getEstimationLabel() {
    return isBeingEstimatedLabel;
  }


  public void setEstimationLabel(String estimationLabel) {
    this.isBeingEstimatedLabel.setValue(estimationLabel);
  }


  public Property<String> getTaskDescProperty()
  {
    return taskDescLabel;
  }

  public void setCurrentSource(VBox source) {
    this.currentSource = source;
    this.reApplyApplicableStyle();
  }


  public void selectTask(VBox source) {
    currentSource = source;
    int index_of_added_char = this.getTaskHeaderLabelProperty().getValue().indexOf(':');
    String taskHeader = this.getTaskHeaderLabelProperty().getValue().substring(index_of_added_char+2);

    if (parentViewModel.getSelectedTask() != null && parentViewModel.getSelectedTask().getTaskHeader().equals(taskHeader)) {
      parentViewModel.setSelectedTask(null, null);
      source.setStyle("-fx-background-color:  #C2D8C8; -fx-border-color:  #82B366;");
      ViewFactory.getInstance().getPlanningPokerViewController().refreshDisplayedTaskInfo();
    } else {
      parentViewModel.setSelectedTask(taskHeader, taskDescLabel.getValue());
      source.setStyle("-fx-background-color:  #82B366; -fx-border-color:  #82B366;");
      parentViewModel.resetTaskStyles();
    }

  }


  public void onHoverEntryTask(VBox source) {
    currentSource = source;
    int index_of_added_char = this.getTaskHeaderLabelProperty().getValue().indexOf(':');
    String taskHeader = this.getTaskHeaderLabelProperty().getValue().substring(index_of_added_char+2);

    if (parentViewModel.getSelectedTask() != null && !parentViewModel.getSelectedTask().getTaskHeader().equals(taskHeader)) {
      source.setStyle("-fx-background-color:  #C2D8C8; -fx-border-color:  #82B366;");
      source.setCursor(javafx.scene.Cursor.HAND);
    } else if(parentViewModel.getSelectedTask() == null) {
      source.setStyle("-fx-background-color:  #C2D8C8; -fx-border-color:  #82B366;");
      source.setCursor(javafx.scene.Cursor.HAND);
    }
  }


  public void onHoverExitTask(VBox source) {
    currentSource = source;
    int index_of_added_char = this.getTaskHeaderLabelProperty().getValue().indexOf(':');
    String taskHeader = this.getTaskHeaderLabelProperty().getValue().substring(index_of_added_char+2);

    if (parentViewModel.getSelectedTask() != null && !parentViewModel.getSelectedTask().getTaskHeader().equals(taskHeader)) {
      this.resetStyle();
    } else if(parentViewModel.getSelectedTask() == null) {
      this.resetStyle();
    }
  }


  public void resetStyle() {
    if(currentSource != null) {
      currentSource.setStyle("-fx-background-color:   #D5E8D4; -fx-border-color:  #82B366;");
      currentSource.setCursor(javafx.scene.Cursor.DEFAULT);
    }
  }


  public void reApplyApplicableStyle() {
    if(currentSource != null) {
      int index_of_added_char = this.getTaskHeaderLabelProperty().getValue().indexOf(':');
      String taskHeader = this.getTaskHeaderLabelProperty().getValue().substring(index_of_added_char+2);

      if (parentViewModel.getSelectedTask() != null && !parentViewModel.getSelectedTask().getTaskHeader().equals(taskHeader)) {
        currentSource.setStyle("-fx-background-color:  #D5E8D4; -fx-border-color:  #82B366;");
        currentSource.setCursor(javafx.scene.Cursor.HAND);
      } else if(parentViewModel.getSelectedTask() != null  && parentViewModel.getSelectedTask().getTaskHeader().equals(taskHeader)) {
        currentSource.setStyle("-fx-background-color:  #82B366; -fx-border-color:  #82B366;");
        currentSource.setCursor(javafx.scene.Cursor.HAND);
      } else if(parentViewModel.getSelectedTask() == null) {
        currentSource.setStyle("-fx-background-color:  #D5E8D4; -fx-border-color:  #82B366;");
        currentSource.setCursor(javafx.scene.Cursor.HAND);
      }
    }
  }
}
