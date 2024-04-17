package Model.Login;

import Util.PropertyChangeSubject;

public interface LoginServerModel extends PropertyChangeSubject
{
    boolean validateUser(String username, String password);
    void createUser(String username, String password);
}
