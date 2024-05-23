package Model.Chat;

import DataTypes.Message;
import DataTypes.PlanningPoker;
import DataTypes.User;
import Networking.ClientConnection_RMI;
import Networking.ServerConnection_RMI;
import Util.PropertyChangeSubject;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ChatServerModel extends PropertyChangeSubject
{
    /** Receives and broadcasts message to all the users in the senders session of planning poker
     * @param message the message that is received and broadcasted
     * @param sender the user which sends the message
     * @param connectedClients the list of connected clients on the server
     * @param server the server which receives the request*/
    void receiveAndBroadcastMessage(Message message, User sender, ArrayList<ClientConnection_RMI> connectedClients, ServerConnection_RMI server) throws RemoteException;

    /** Adds user to */
    void addUserToSession(User user, ArrayList<ClientConnection_RMI> connectedClients, ServerConnection_RMI server) throws RemoteException;
    void broadcastUsers(User user, ArrayList<ClientConnection_RMI> connectedClients, ServerConnection_RMI server, PlanningPoker planningPoker) throws RemoteException;
    void broadcastUsersWithException(User removedUser, ArrayList<ClientConnection_RMI> connectedClients, ServerConnection_RMI server, PlanningPoker planningPoker) throws RemoteException;
    void removeUserFromSession(User user, ArrayList<ClientConnection_RMI> connectedClients, ServerConnection_RMI server) throws RemoteException;
}
