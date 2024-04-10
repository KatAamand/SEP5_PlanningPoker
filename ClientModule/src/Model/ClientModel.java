package Model;

import Util.PropertyChangeSubject;

public interface ClientModel extends PropertyChangeSubject
{
    void sendMessage(String message);
}
