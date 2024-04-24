package Networking;

import DataTypes.PlanningPoker;
import DataTypes.Task;
import Util.PropertyChangeSubject;
import DataTypes.User;

public interface Client extends PropertyChangeSubject {
    void validateUser(String username, String password);
    void createUser(String username, String password);
    void userCreatedSuccessfully();
    void updateUser(User user);
    void sendMessage(String message, User sender);
    void receiveMessage(String message);
    boolean validatePlanningPokerID(String planningPokerID);
    PlanningPoker createPlanningPoker();
    PlanningPoker loadPlanningPoker(String planningPokerId);
    void loadTaskList(String gameId);
    void addTask(Task task, String gameId);
}
