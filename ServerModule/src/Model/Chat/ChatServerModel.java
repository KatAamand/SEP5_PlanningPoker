package Model.Chat;

import DataTypes.Message;
import DataTypes.User;
import Networking.ClientConnection_RMI;
import Util.PropertyChangeSubject;

import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ChatServerModel extends PropertyChangeSubject
{
    void receiveAndBroadcastMessage(Message message, User sender, ArrayList<ClientConnection_RMI> connectedClients) throws RemoteException;
}
