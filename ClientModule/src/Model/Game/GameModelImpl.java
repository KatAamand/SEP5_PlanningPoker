package Model.Game;

import Application.ClientFactory;
import Application.Session;
import DataTypes.Effort;
import DataTypes.Task;
import Model.PlanningPoker.PlanningPokerModelImpl;
import Networking.Client;
import Util.PropertyChangeSubject;
import javafx.application.Platform;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class GameModelImpl extends PlanningPokerModelImpl implements GameModel
{
  private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
  private Client clientConnection;
  private ArrayList<Task> skippedTaskList;


  /** Primary constructor. Defers most of the declarations and definitions to the init method,
   * which is run inside a Platform.runLater statement for increased thread safety while using javaFx. */
  public GameModelImpl() throws RemoteException {
    super();
    super.init();
    skippedTaskList = new ArrayList<>();

    //Assign the network connection:
    clientConnection = (Client) ClientFactory.getInstance().getClient();

    //Initialize remaining data:
    Platform.runLater(this::init);
  }


  @Override public void init()
  {
    //Assign all PropertyChangeListeners:
    this.assignListeners();
  }

  @Override public Task nextTaskToEvaluate() {
    ArrayList<Task> taskList = (ArrayList<Task>) super.getActivePlanningPokerGame().getTaskList();

    // First check if there are tasks that haven't been estimated on, and that haven't already been skipped:
    for (Task task : taskList) {
      // If the tasks do not already have a final effort assigned, and the task has not been skipped, we display it.
      if (task.getFinalEffort() != null && !skippedTaskList.contains(task)) {
        return task;
      }
    }

    // If we reach here, all tasks have either been estimated on - or have been skipped. Check the skipped list now:
    for (Task task : skippedTaskList) {
      // If the tasks do not already have a final effort assigned.
      if (task.getFinalEffort() != null) {
        return task;
      }
    }
    return null;
  }


  @Override public void skipTask(Task task) {
    ArrayList<Task> taskList = (ArrayList<Task>) super.getActivePlanningPokerGame().getTaskList();
    // First check if there are tasks that haven't been estimated on, and that haven't already been skipped:
    for (Task taskFromList : taskList) {
      // If the tasks do not already have a final effort assigned, and the task has not been skipped, we display it.
      if (taskFromList.getFinalEffort() != null && !skippedTaskList.contains(task)) {
        // Add the skipped task to a list of skipped tasks:
        skippedTaskList.add(task);

        // Transfer the skipped taskList to connected clients, so all clients have the same experience.
        clientConnection.skipTasks(skippedTaskList, super.getActivePlanningPokerGame().getPlanningPokerID());
        return;
      }
    }

    // If we reach here, we know all tasks have either been estimated on, or skipped. Clear the skippedTaskList, so can be re-populated:
    skippedTaskList.clear();
    skippedTaskList.add(task);

    // Transfer the skipped taskList to connected clients, so all clients have the same experience.
    clientConnection.skipTasks(skippedTaskList, super.getActivePlanningPokerGame().getPlanningPokerID());
  }

  @Override public void refreshTaskList() {
    clientConnection.loadTaskList(Session.getConnectedGameId());
  }

  @Override
  public ArrayList<Effort> getEffortList() {
      return clientConnection.getEffortList();
  }

  /** Assigns all the required listeners to the clientConnection allowing for Observable behavior between these classes. */
  private void assignListeners()
  {
    clientConnection.addPropertyChangeListener("receivedListOfTasksToSkip", evt -> {
      if(evt.getNewValue() != null) {
        skippedTaskList = ((ArrayList<Task>) evt.getNewValue());
      }
      propertyChangeSupport.firePropertyChange("receivedListOfTasksToSkip", null, null);
    });
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
