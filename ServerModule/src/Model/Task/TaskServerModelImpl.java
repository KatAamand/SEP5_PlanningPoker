package Model.Task;

import DataTypes.Task;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TaskServerModelImpl implements TaskServerModel, Runnable{

  private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
  private static TaskServerModel instance;
  private static final Lock lock = new ReentrantLock();
  private ArrayList<Task> taskList;

  private TaskServerModelImpl()
  {
    //TODO: Refactor so that it in the future loads the list from a database.
    this.taskList = new ArrayList<>();
  }

  @Override public void addTask(Task task)
  {
    if(taskList.add(task))
    {
      System.out.println("Server: Added a task.");
      propertyChangeSupport.firePropertyChange("TaskDataChanged", null, null);
    }
    else
    {
      System.out.println("Server: FAILED to add a task.");
    }
  }

  @Override public void removeTask(Task task)
  {
    if(taskList.remove(task))
    {
      System.out.println("Server: Removed a task.");
      propertyChangeSupport.firePropertyChange("TaskDataChanged", null, null);
    }
    else
    {
      System.out.println("Server: FAILED to a remove task.");
    }
  }

  @Override public Task getTask(int index)
  {
    return taskList.get(index);
  }

  //TODO: Should be refactored and become session dependent, instead of just returning all tasks in the entire database...
  @Override public ArrayList<Task> getTaskList()
  {
    return this.taskList;
  }

  public static TaskServerModel getInstance()
  {
    //Here we use the "Double-checked lock" principle to ensure proper synchronization.
    if(instance == null)
    {
      synchronized (lock)
      {
        if(instance == null)
        {
          instance = new TaskServerModelImpl();
        }
      }
    }
    return instance;
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

  @Override public void run()
  {
    //TODO
  }
}
