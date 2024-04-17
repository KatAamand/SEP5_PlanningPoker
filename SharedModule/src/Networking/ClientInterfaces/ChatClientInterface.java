package Networking.ClientInterfaces;

import DataTypes.User;
import Util.PropertyChangeSubject;

public interface ChatClientInterface extends PropertyChangeSubject {
    void sendMessage(String message, User sender);
    void receiveMessage(String message);
}
