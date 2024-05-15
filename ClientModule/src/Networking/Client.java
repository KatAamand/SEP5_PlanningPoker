package Networking;

import DataTypes.*;
import Util.PropertyChangeSubject;

import java.rmi.RemoteException;
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
    boolean validatePlanningPokerID(int planningPokerID);
    PlanningPoker createPlanningPoker();
    PlanningPoker loadPlanningPoker(int planningPokerId);
    void loadTaskList(int planningPokerId);
    void addTask(Task task, int planningPokerId);
    boolean removeTask(Task task, int planningPokerId);
    boolean editTask(Task oldTask, Task newTask, int planningPokerId);
    void skipTasks(ArrayList<Task> skippedTasksList, int planningPokerId);
    void sendUser();
    void removeUserFromSession();
    ArrayList<Effort> getEffortList();
    void placeCard(UserCardData userCardData);
    void requestClearPlacedCards();
    void clearPlacedCards() throws RemoteException;
    void setRoleInGame(DataTypes.UserRoles.UserRole userRole, int s, DataTypes.User user);

    void requestShowCards();
}
