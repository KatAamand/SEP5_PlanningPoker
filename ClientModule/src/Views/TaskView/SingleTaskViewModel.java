package Views.TaskView;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.VBox;

public class SingleTaskViewModel
{
  private Property<String> taskHeaderLabel;
  private Property<String> taskDescLabel;
  private TaskViewModel parentViewModel; //This field variable will become useful when task selection (clicking on tasks) becomes relevant!


  public SingleTaskViewModel(TaskViewModel parentViewModel) {
    taskHeaderLabel = new SimpleStringProperty("UNDEFINED");
    taskDescLabel = new SimpleStringProperty("UNDEFINED");
    this.parentViewModel = parentViewModel;
  }


  public void setTaskHeaderLabel(String taskHeader)
  {
    this.taskHeaderLabel.setValue(taskHeader);
  }


  public Property<String> getTaskHeaderLabelProperty()
  {
    return taskHeaderLabel;
  }


  public void setTaskDesc(String taskDesc)
  {
    this.taskDescLabel.setValue(taskDesc);
  }


  public Property<String> getTaskDescProperty()
  {
    return taskDescLabel;
  }


  public void selectTask(VBox source) {
    System.out.println("TODO: Task selection stuff HAS NOT been implemented yet. ");
    source.setStyle("-fx-background-color:  #82B366; -fx-border-color:  #82B366;");
  }


  public void onHoverEntryTask(VBox source) {
    source.setStyle("-fx-background-color:  #C2D8C8; -fx-border-color:  #82B366;");
    source.setCursor(javafx.scene.Cursor.HAND);
  }


  public void onHoverExitTask(VBox source) {
    source.setStyle("-fx-background-color:   #D5E8D4; -fx-border-color:  #82B366;");
    source.setCursor(javafx.scene.Cursor.DEFAULT);
  }
}
