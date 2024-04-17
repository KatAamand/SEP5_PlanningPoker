package Views.TaskView;

import Application.ClientFactory;
import Application.ModelFactory;
import Application.ViewFactory;
import Application.ViewModelFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import java.rmi.RemoteException;

public class SingleTaskViewController
{
  @FXML private VBox taskContainer;
  @FXML private Label taskHeaderLabel;
  @FXML private Label taskDescLabel;
  private int rowId;
  private SingleTaskViewModel viewModel;
  private TaskViewController parentController;

  public SingleTaskViewController()
  {
    parentController = ViewFactory.getInstance().getTaskViewController();
  }

  public void initialize(int rowId)
  {
    setRowId(rowId);
    try
    {
      viewModel = ViewModelFactory.getInstance().getTaskViewModel().getSingleTaskViewModelList().get(this.getRowId());
    }
    catch (RemoteException e)
    {
      //TODO: Proper exception handling
      e.printStackTrace();
    }
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
