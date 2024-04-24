package Model.Task;

import DataTypes.Task;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TaskServerModelImpl implements TaskServerModel, Runnable{

  private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
  private volatile static TaskServerModel instance;
  private static final Lock lock = new ReentrantLock();
  private Map<String, List<Task>> tasklistMap;

  private TaskServerModelImpl() {
    //TODO: Refactor so that it in the future loads the list from a database.
    this.tasklistMap = new HashMap<>();

  }

  @Override public void addTask(Task task, String gameId) {
    List<Task> taskList;

    if(tasklistMap.get(gameId) != null) {
      taskList = tasklistMap.get(gameId);
    } else {
      taskList = new ArrayList<>();
    }

    if(taskList.contains(task)) {
      taskList.remove(task);
      taskList.add(task);
    } else {
      taskList.add(task);
    }

    tasklistMap.put(gameId, taskList);

    System.out.println("Server: Added a task.");
    propertyChangeSupport.firePropertyChange("TaskDataChanged", null, gameId);
  }

  @Override public void removeTask(Task task, String gameId) {
    List<Task> taskList;

    if(tasklistMap.get(gameId) != null) {
      taskList = tasklistMap.get(gameId);
      if(taskList.remove(task)) {
        System.out.println("TaskServerModelImpl: Removed a task.");
      } else {
        System.out.println("TaskServerModelImpl: FAILED to a remove task.");
      }
      propertyChangeSupport.firePropertyChange("TaskDataChanged", null, null);
    } else {
      //Do nothing. The passed task does not exist in the mapping.
    }
  }


  @Override public ArrayList<Task> getTaskList(String gameId) {
    ArrayList<Task> taskList;

    if(tasklistMap.get(gameId) != null) {
      return (ArrayList<Task>) tasklistMap.get(gameId);
    } else {
      taskList = new ArrayList<>();
    }

    return taskList;
  }


  public static TaskServerModel getInstance() {
    //Here we use the "Double-checked lock" principle to ensure proper synchronization.
    if(instance == null) {
      synchronized (lock) {
        if(instance == null) {
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


  @Override public void run() {
    //TODO
  }
}
