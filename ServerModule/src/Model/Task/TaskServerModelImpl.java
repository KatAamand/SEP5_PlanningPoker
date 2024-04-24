package Model.Task;

import DataTypes.Task;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TaskServerModelImpl implements TaskServerModel, Runnable{

  private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
  private static TaskServerModel instance;
  private static final Lock lock = new ReentrantLock();
  private ArrayList<Task> taskList;
  private Map<String, Task> tasklistMap;

  private TaskServerModelImpl() {
    //TODO: Refactor so that it in the future loads the list from a database.
    this.taskList = new ArrayList<>();
    this.tasklistMap = new HashMap<>();

    //Load some test/Dummy data:
    generateDummyTaskData();
  }

  @Override public void addTask(Task task) {
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

  @Override public void removeTask(Task task) {
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

  @Override public Task getTask(int index) {
    return taskList.get(index);
  }

  //TODO: Should be refactored and become session dependent, instead of just returning all tasks in the entire database...
  @Override public ArrayList<Task> getTaskList() {
    return this.taskList;
  }


  public static TaskServerModel getInstance() {
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


  @Override public void run() {
    //TODO
  }

  private void generateDummyTaskData() {
    addTask(new Task("Implement User Authentication", "Develop a user authentication system using OAuth 2.0 to allow users to securely log in with their Google or GitHub accounts."));
    addTask(new Task("Optimize Database Queries", "Review and optimize database queries in the application's backend to improve performance and reduce response times."));
    addTask(new Task("Refactor UI Components", "Refactor the frontend UI components using a modern framework like React or Vue.js to enhance user experience and maintainability."));
    addTask(new Task("Implement RESTful API Endpoints", "Design and implement RESTful API endpoints for CRUD operations to enable seamless interaction between the frontend and backend components."));
    addTask(new Task("Enhance Error Handling", "Improve error handling mechanisms throughout the application to provide clear and informative error messages for users and developers alike."));
  }
}
