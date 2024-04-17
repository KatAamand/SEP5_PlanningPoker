package Networking.ClientInterfaces;

import DataTypes.User;
import Util.PropertyChangeSubject;

public interface LoginClientInterface extends PropertyChangeSubject {
    void validateUser(String username, String password);
    void createUser(String username, String password);
    void userCreatedSuccessfully();
    void updateUser(User user);
}
