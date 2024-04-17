package Networking.ClientInterfaces;

import Util.PropertyChangeSubject;

public interface LoginClientInterface extends PropertyChangeSubject {
    void validateUser(String username, String password);
    void createUser(String username, String password);
}
