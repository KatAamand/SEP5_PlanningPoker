package Views.LobbyView;

import Application.ModelFactory;
import Application.ViewModelFactory;
import Model.Lobby.LobbyModel;
import Util.PropertyChangeSubject;
import Views.TaskView.TaskViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.rmi.RemoteException;

public class LobbyViewModel implements PropertyChangeSubject
{
  private final LobbyModel lobbyModel;
  private BooleanProperty isTaskListEmpty;
  private TaskViewModel taskViewModel; // This attribute is used for tracking if the TaskList is empty
  private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

  public LobbyViewModel() throws RemoteException
  {
    this.lobbyModel = ModelFactory.getInstance().getLobbyModel();
    this.taskViewModel = ViewModelFactory.getInstance().getTaskViewModel();
    this.isTaskListEmpty = new SimpleBooleanProperty();

    bindTaskListEmptyProperty();

    lobbyModel.addPropertyChangeListener("PlanningPokerObjUpdated", evt ->
        propertyChangeSupport.firePropertyChange("PlanningPokerObjUpdated", null, null));
  }

  public BooleanProperty isTaskListEmptyProperty() {
    return isTaskListEmpty;
  }

  // Binding taskViewModel.isTaskListEmptyProperty() to isTaskListEmpty
  private void bindTaskListEmptyProperty() {
    isTaskListEmpty.bind(taskViewModel.isTaskListEmptyProperty());
  }

  @Override public void addPropertyChangeListener(PropertyChangeListener listener) {
    propertyChangeSupport.addPropertyChangeListener(listener);
  }
  @Override public void addPropertyChangeListener(String name, PropertyChangeListener listener) {
    propertyChangeSupport.addPropertyChangeListener(name, listener);
  }
  @Override public void removePropertyChangeListener(PropertyChangeListener listener) {
    propertyChangeSupport.removePropertyChangeListener(listener);
  }
  @Override public void removePropertyChangeListener(String name, PropertyChangeListener listener) {
    propertyChangeSupport.removePropertyChangeListener(name, listener);
  }
}
