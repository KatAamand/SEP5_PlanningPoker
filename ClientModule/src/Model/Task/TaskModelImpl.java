package Model.Task;

import Application.ClientFactory;
import DataTypes.Task;
import Networking.ClientInterfaces.TaskClientInterface;
import javafx.application.Platform;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class TaskModelImpl implements TaskModel
{
  private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
  private TaskClientInterface clientConnection;
  private ArrayList<Task> taskList; //TODO: Should be refactored to be session dependant, instead of being filled with all tasks in existence!.



  /** Primary constructor. Defers most of the declarations and definitions to the init method,
   * which is run inside a Platform.runLater statement for increased thread safety while using javaFx. */
  public TaskModelImpl() {
    //Assign the network connection:
    try
    {
      clientConnection = ClientFactory.getInstance().getClient();
    }
    catch (RemoteException e)
    {
      //TODO: Properly handle this error!
      e.printStackTrace();
    }

    //Initialize remaining data:
    Platform.runLater(this::init);
  }



  @Override public void init()
  {
    //Assign all PropertyChangeListeners:
    this.assignListeners();


    //Load data from server:
    try
    {
      clientConnection.loadTaskList();
    }
    catch (RemoteException e)
    {
      //TODO: Add proper exception handling.
      e.printStackTrace();
    }
  }



  /** Assigns all the required listeners to the clientConnection allowing for Observable behavior betweeen these classes. */
  private void assignListeners()
  {
    try
    {
      clientConnection.addPropertyChangeListener("receivedUpdatedTaskList", evt -> {
        setTaskList((ArrayList<Task>) evt.getNewValue());
        propertyChangeSupport.firePropertyChange("taskListUpdated", null, null);
        });
    }
    catch (RemoteException e)
    {
      //TODO: Proper Exception handling
      e.printStackTrace();
    }
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
    try
    {
      clientConnection.addTask(task);
    }
    catch (RemoteException e)
    {
      //TODO: Add Proper exception handling.
      e.printStackTrace();
    }

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
