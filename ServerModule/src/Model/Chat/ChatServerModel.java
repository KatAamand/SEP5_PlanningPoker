package Model.Chat;

import DataTypes.User;
import Networking.ClientConnection_RMI;
import Util.PropertyChangeSubject;

import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ChatServerModel extends PropertyChangeSubject
{
    void receiveAndBroadcastMessage(String message, User sender, ArrayList<ClientConnection_RMI> connectedClients) throws RemoteException;
}
