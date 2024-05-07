package Views.LobbyView;

import Application.ModelFactory;
import Application.ViewModelFactory;
import Model.Lobby.LobbyModel;
import Views.TaskView.TaskViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.rmi.RemoteException;

public class LobbyViewModel
{
  private final LobbyModel lobbyModel;
  private BooleanProperty isTaskListEmpty;
  private TaskViewModel taskViewModel; // This attribute is used for tracking if the TaskList is empty

  public LobbyViewModel() throws RemoteException
  {
    this.lobbyModel = ModelFactory.getInstance().getLobbyModel();
    this.taskViewModel = ViewModelFactory.getInstance().getTaskViewModel();
    this.isTaskListEmpty = new SimpleBooleanProperty();

    bindTaskListEmptyProperty();
  }

  public BooleanProperty isTaskListEmptyProperty() {
    return isTaskListEmpty;
  }

  // Binding taskViewModel.isTaskListEmptyProperty() to isTaskListEmpty
  private void bindTaskListEmptyProperty() {
    isTaskListEmpty.bind(taskViewModel.isTaskListEmptyProperty());
  }
}
