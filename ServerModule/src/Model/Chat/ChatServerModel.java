package Model.Chat;

import DataTypes.Message;
import DataTypes.User;
import Networking.ClientConnection_RMI;
import Networking.ServerConnection_RMI;
import Util.PropertyChangeSubject;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ChatServerModel extends PropertyChangeSubject
{
    void receiveAndBroadcastMessage(Message message, User sender, ArrayList<ClientConnection_RMI> connectedClients, ServerConnection_RMI server) throws RemoteException;
    void addUserToSession(User user, ArrayList<ClientConnection_RMI> connectedClients, ServerConnection_RMI server) throws RemoteException;
    void broadcastUsers(User user, ArrayList<ClientConnection_RMI> connectedClients, ServerConnection_RMI server) throws RemoteException;
    void removeUserFromSession(User user, ArrayList<ClientConnection_RMI> connectedClients, ServerConnection_RMI server) throws RemoteException;
}
