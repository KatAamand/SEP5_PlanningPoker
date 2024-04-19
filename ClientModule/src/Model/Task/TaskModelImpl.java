package Model.Task;

import Application.ClientFactory;
import DataTypes.Task;
import Networking.Client;
import javafx.application.Platform;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class TaskModelImpl implements TaskModel
{
  private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
  private Client clientConnection;
  private ArrayList<Task> taskList; //TODO: Should be refactored to be session dependant, instead of being filled with all tasks in existence!.


  /** Primary constructor. Defers most of the declarations and definitions to the init method,
   * which is run inside a Platform.runLater statement for increased thread safety while using javaFx. */
  public TaskModelImpl() throws RemoteException {
    //Assign the network connection:
    clientConnection = (Client) ClientFactory.getInstance().getClient();

    //Initialize remaining data:
    Platform.runLater(this::init);
  }


  @Override public void init()
  {
      //Assign all PropertyChangeListeners:
      this.assignListeners();

      //Load data from server:
      clientConnection.loadTaskList();
  }


  /** Assigns all the required listeners to the clientConnection allowing for Observable behavior betweeen these classes. */
  private void assignListeners()
  {
    clientConnection.addPropertyChangeListener("receivedUpdatedTaskList", evt -> {
      setTaskList((ArrayList<Task>) evt.getNewValue());
      propertyChangeSupport.firePropertyChange("taskListUpdated", null, null);
      });
  }


  private void setTaskList(ArrayList<Task> taskList)
  {
    this.taskList = taskList;
  }


  @Override public ArrayList<Task> getTaskList()
  {
    return this.taskList;
  }


  @Override public void addTask(Task task)
  {
    clientConnection.addTask(task);
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
