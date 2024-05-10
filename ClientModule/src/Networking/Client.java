package Networking;

import DataTypes.*;
import Util.PropertyChangeSubject;

import java.util.ArrayList;

public interface Client extends PropertyChangeSubject {
    void validateUser(String username, String password);
    void createUser(String username, String password);
    void logoutUser(String username, String password);
    void disconnectLocalClient();
    void userValidationFailed(String errorMessage);
    void userCreatedSuccessfully();
    void updateUser(User user);
    void sendMessage(Message message, User sender);
    void receiveMessage(Message message);
    boolean validatePlanningPokerID(String planningPokerID);
    PlanningPoker createPlanningPoker();
    PlanningPoker loadPlanningPoker(String planningPokerId);
    void loadTaskList(String gameId);
    void addTask(Task task, String gameId);
    boolean removeTask(Task task, String gameId);
    boolean editTask(Task oldTask, Task newTask, String gameId);
    void skipTasks(ArrayList<Task> skippedTasksList, String gameId);
    void sendUser();
    void removeUserFromSession();
    ArrayList<Effort> getEffortList();
}
