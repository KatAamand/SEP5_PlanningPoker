package Model.Task;

import DataTypes.PlanningPoker;
import DataTypes.Task;
import Database.PlanningPoker.PlanningPokerDAO;
import Database.PlanningPoker.PlanningPokerDAOImpl;
import Database.Task.TaskDAO;
import Database.Task.TaskDAOImpl;
import Networking.ClientConnection_RMI;
import Networking.ServerConnection_RMI;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TaskServerModelImpl implements TaskServerModel {

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private volatile static TaskServerModel instance;
    private static final Lock lock = new ReentrantLock();
    private Map<Integer, List<Task>> tasklistMap;
    private TaskDAO taskDAO;
    private PlanningPokerDAO planningPokerDAO;


    private TaskServerModelImpl() {
        this.tasklistMap = new HashMap<>();
        try {
            this.taskDAO = TaskDAOImpl.getInstance();
            this.planningPokerDAO = PlanningPokerDAOImpl.getInstance();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize DAO", e);
        }

        getAllTasksFromDB();
    }

    private void getAllTasksFromDB() {
        ArrayList<PlanningPoker> allPlanningPokers;

        try {
            allPlanningPokers = planningPokerDAO.getAllPlanningPoker();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        for (PlanningPoker planningPoker : allPlanningPokers) {
            ArrayList<Task> allTasksFromPlanningPoker = getTaskListFromDB(planningPoker.getPlanningPokerID());
            tasklistMap.put(planningPoker.getPlanningPokerID(), planningPoker.getTaskList());
        }
    }

    private void fireTaskListDataUpdatedEvent(int planningPokerId) {
        // Fires a PropertyChange event with a Null value to absorb any similarities to the contents of the value in the previously fired PropertyChange event:
        propertyChangeSupport.firePropertyChange("TaskDataChanged", null, null);

        // Fires the proper PropertyChange event, with the taskList attached as the newValue():
        propertyChangeSupport.firePropertyChange("TaskDataChanged", null, planningPokerId);
    }


    public void addTask(Task task, int planningPokerId) {
        List<Task> taskList;

        if (tasklistMap.get(planningPokerId) != null) {
            taskList = tasklistMap.get(planningPokerId);
        } else {
            taskList = new ArrayList<>();
        }

        if (taskList.contains(task)) {
            taskList.remove(task);
        }

        taskList.add(task);

        tasklistMap.put(planningPokerId, taskList);

        Task newTask = createTaskInDB(task, planningPokerId);
        task.setTaskID(newTask.getTaskID());

        fireTaskListDataUpdatedEvent(planningPokerId);
    }

    private Task createTaskInDB(Task task, int planningPokerId) {
        try {
            Task newTask = taskDAO.create(task.getTaskHeader(), task.getDescription(), planningPokerId);
            task.setTaskID(newTask.getTaskID());
            System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", Task created in DB with ID: " + newTask.getTaskID());
            return newTask;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public boolean removeTask(Task task, int planningPokerId) {
        List<Task> taskList;

        if (tasklistMap.get(planningPokerId) != null) {
            taskList = tasklistMap.get(planningPokerId);
            if (taskList.remove(task)) {
                System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", TaskServerModelImpl: Removed a task.");
                try {
                    taskDAO.delete(task);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                fireTaskListDataUpdatedEvent(planningPokerId);
                return true;
            }
        }
        System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", TaskServerModelImpl: FAILED to remove task.");
        return false;
    }


    @Override
    public boolean editTask(Task task, Task updatedTask, int planningPokerId) {
        List<Task> taskList = tasklistMap.get(planningPokerId);
        if (taskList != null) {
            int taskIndex = taskList.indexOf(task);
            if (taskIndex != -1) {
                taskList.get(taskIndex).copyAttributesFromTask(updatedTask);
                System.out.println("TaskServerModelImpl: Edited a task.");

                updateTaskInDB(updatedTask);
                fireTaskListDataUpdatedEvent(planningPokerId);
                return true;
            } else {
                System.out.println("TaskServerModelImpl: Task not found for editing.");
            }
        }
        return false;
    }

    private void updateTaskInDB(Task task) {
        try {
            Task existingTask = taskDAO.readByTaskId(task.getTaskID());
            if (existingTask == null) {
                throw new SQLException("Task not found with ID: " + task.getTaskID());
            }
            taskDAO.update(task);
            System.out.println("TaskServerModelImpl: Task updated in DB.");
        } catch (SQLException e) {
            System.err.println("Error updating task in database: " + e.getMessage());
            throw new RuntimeException("Error updating task in database", e);
        }
    }



    @Override
    public ArrayList<Task> getTaskList(int planningPokerId) {
        ArrayList<Task> taskList;

        if (tasklistMap.get(planningPokerId) != null) {
            return (ArrayList<Task>) tasklistMap.get(planningPokerId);
        } else {
            taskList = new ArrayList<>();
        }

        return taskList;
    }

    @Override
    public ArrayList<Task> getTaskListFromDB(int planningPokerId) {
        ArrayList<Task> allTasksFromPlanningPoker;

        try {
            allTasksFromPlanningPoker = taskDAO.readByPlanningPokerId(planningPokerId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return allTasksFromPlanningPoker;
    }


    @Override public synchronized void broadcastTaskListUpdate(Map<Integer, ArrayList<ClientConnection_RMI>> clientList, ServerConnection_RMI server, int planningPokerId) {
        // Experienced concurrency issues here:
        //ArrayList<ClientConnection_RMI> receivingClients = clientList.get(planningPokerId);
        ArrayList<ClientConnection_RMI> receivingClients = new ArrayList<>(clientList.get(planningPokerId));
        if (receivingClients != null) {
            for (int i = 0; i < receivingClients.size(); i++) {
                if (receivingClients.get(i) == null) {
                    receivingClients.remove(i);
                    i--;
                }
            }

            System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", TaskServerModelImpl: Broadcasting changes to the task list to clients in game [" + planningPokerId + "]");
            for (ClientConnection_RMI client : receivingClients) {
                //Create a new thread for each connected client, and then call the desired broadcast operation. This minimizes server lag/hanging due to clients who have connection issues.
                Thread transmitThread = new Thread(() -> {
                    try {
                        client.loadTaskListFromServer(planningPokerId);
                    } catch (RemoteException e) {
                        if (String.valueOf(e.getCause()).equals("java.net.ConnectException: Connection refused: connect")) {
                            //Unregisters clients from the Game Server, who have lost connection in order to avoid further server errors.
                            try {
                                server.unRegisterClientFromGame(client, planningPokerId);
                            } catch (RemoteException ex) {
                                throw new RuntimeException();
                            }
                        } else {
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
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new TaskServerModelImpl();
                }
            }
        }
        return instance;
    }


    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }


    @Override
    public void addPropertyChangeListener(String name, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(name, listener);
    }


    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }


    @Override
    public void removePropertyChangeListener(String name, PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(name, listener);
    }
}
