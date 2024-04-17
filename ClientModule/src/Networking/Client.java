package Networking;

import Util.PropertyChangeSubject;
import DataTypes.User;

public interface Client extends PropertyChangeSubject {
    void validateUser(String username, String password);
    void createUser(String username, String password);
    void userCreatedSuccessfully();
    void updateUser(User user);
    void sendMessage(String message, User sender);
    void receiveMessage(String message);
    void validatePlanningPokerID(String planningPokerID);
    void createPlanningPoker();
}
