package Model.Task;

import DataTypes.Task;
import Networking.ClientConnection_RMI;
import Networking.ServerConnection_RMI;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.rmi.RemoteException;
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

  private void fireTaskListDataUpdatedEvent(String gameId) {
    // Fires a PropertyChange event with a Null value to absorb any similarities to the contents of the value in the previously fired PropertyChange event:
    propertyChangeSupport.firePropertyChange("TaskDataChanged", null, null);

    // Fires the proper PropertyChange event, with the taskList attached as the newValue():
    propertyChangeSupport.firePropertyChange("TaskDataChanged", null, gameId);
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
    fireTaskListDataUpdatedEvent(gameId);
  }


  @Override public boolean removeTask(Task task, String gameId) {
    List<Task> taskList;

    if (tasklistMap.get(gameId) != null) {
      taskList = tasklistMap.get(gameId);
      if (taskList.remove(task)) {
        System.out.println("TaskServerModelImpl: Removed a task.");
        fireTaskListDataUpdatedEvent(gameId);
        return true;
      }
    }
    System.out.println("TaskServerModelImpl: FAILED to remove task.");
    return false;
  }


  @Override public boolean editTask(Task oldTask, Task newTask, String gameId)
  {
    // Find appropriate taskList assigned to this game:
    List<Task> taskList;
    if (tasklistMap.get(gameId) != null)
    {
      // Set the taskList from the found game:
      taskList = tasklistMap.get(gameId);
      // Check if the oldTask even exists in the list:
      System.out.println("Oldtask: " + oldTask.getFinalEffort() + " / " + "NewTask: " + newTask.getFinalEffort());
      System.out.println("Tasklist: " + taskList);
      if(taskList.contains(oldTask))
      {
        // Find the task
        for (Task task : taskList) {
          if(task.equals(oldTask)) {
            task.copyAttributesFromTask(newTask);
            System.out.println("TaskServerModelImpl: Edited a task.");

            fireTaskListDataUpdatedEvent(gameId);
            return true;
          }
        }
      } else {
        return false;
      }
    }
    return false;
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

  @Override public void broadcastTaskListUpdate(Map<String, ArrayList<ClientConnection_RMI>> clientList, ServerConnection_RMI server, String gameId)
  {
    ArrayList<ClientConnection_RMI> receivingClients = clientList.get(gameId);
    if(receivingClients != null) {
      for (int i = 0; i < receivingClients.size(); i++) {
        if(receivingClients.get(i) == null) {
          receivingClients.remove(i);
          i--;
        }
      }

      System.out.println("Server: Broadcasting changes to the task list to clients in game [" + gameId + "]");
      for (ClientConnection_RMI client : receivingClients) {
        //Create a new thread for each connected client, and then call the desired broadcast operation. This minimizes server lag/hanging due to clients who have connection issues.
        Thread transmitThread = new Thread(() -> {
          try {
            client.loadTaskListFromServer(gameId);
          }
          catch (RemoteException e) {
            if(String.valueOf(e.getCause()).equals("java.net.ConnectException: Connection refused: connect")) {
              //Unregisters clients from the Game Server, who have lost connection in order to avoid further server errors.
              try {
                server.unRegisterClientFromGame(client, gameId);
              } catch (RemoteException ex) {
                throw new RuntimeException();
              }
            }
            else {
              //Error is something else:
              throw new RuntimeException();
            }
          }
        });
        transmitThread.setDaemon(true);
        transmitThread.start();
      }
    }
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
