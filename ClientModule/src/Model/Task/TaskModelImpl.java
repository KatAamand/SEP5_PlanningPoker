package Model.Task;

import Application.ClientFactory;
import DataTypes.Task;
import Model.PlanningPoker.PlanningPokerModelImpl;
import Networking.Client;
import javafx.application.Platform;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class TaskModelImpl extends PlanningPokerModelImpl implements TaskModel
{
  private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
  private Client clientConnection;


  /** Primary constructor. Defers most of the declarations and definitions to the init method,
   * which is run inside a Platform.runLater statement for increased thread safety while using javaFx. */
  public TaskModelImpl() throws RemoteException {
    super();
    super.initialize();

    //Assign the network connection:
    clientConnection = (Client) ClientFactory.getInstance().getClient();

    //Initialize remaining data:
    Platform.runLater(this::init);
  }


  @Override public void init() {
      //Assign all PropertyChangeListeners:
      this.assignListeners();
  }


  /** Assigns all the required listeners to the clientConnection allowing for Observable behavior betweeen these classes. */
  private void assignListeners() {
    clientConnection.addPropertyChangeListener("receivedUpdatedTaskList", evt -> {
      try {
        if(evt.getNewValue() != null) {
          setTaskList((ArrayList<Task>) evt.getNewValue());
        }
      } catch (RemoteException e) {
        throw new RuntimeException();
      }
      propertyChangeSupport.firePropertyChange("taskListUpdated", null, null);
      });

    super.addPropertyChangeListener("PlanningPokerObjUpdated", evt -> {
      propertyChangeSupport.firePropertyChange("PlanningPokerObjUpdated", null, null);
    });
  }


  private void setTaskList(ArrayList<Task> taskList) throws RemoteException {
    super.getActivePlanningPokerGame().setTaskList(taskList);
  }


  @Override public ArrayList<Task> getTaskList() {
    return (ArrayList<Task>) super.getActivePlanningPokerGame().getTaskList();
  }


  @Override public void addTask(Task task) {
    clientConnection.addTask(task, super.getActivePlanningPokerGame().getPlanningPokerID());
  }

  @Override public boolean removeTask(Task task) {
    return clientConnection.removeTask(task, super.getActivePlanningPokerGame().getPlanningPokerID());
  }

  @Override public boolean editTask(Task uneditedTask, Task editedTask) {
    return clientConnection.editTask(uneditedTask, editedTask, super.getActivePlanningPokerGame().getPlanningPokerID());
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
