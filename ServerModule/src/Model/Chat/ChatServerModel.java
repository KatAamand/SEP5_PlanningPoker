package Model.Chat;

import DataTypes.Message;
import DataTypes.PlanningPoker;
import DataTypes.User;
import Networking.ClientConnection_RMI;
import Networking.ServerConnection_RMI;
import Util.PropertyChangeSubject;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * The ChatServerModel interface defines all the key methods relating to the server-side logic relating to the chat and users connected to each Planning Poker game.
 * It extends the PropertyChangeSubject interface and is thus prepared for use with Observer Pattern principles.
 */
public interface ChatServerModel extends PropertyChangeSubject
{
    /** Receives and broadcasts message to all the users in the senders session of planning poker
     * @param message the message that is received and broadcasted
     * @param sender the user which sends the message
     * @param connectedClients the list of connected clients on the server
     * @param server the server which receives the request*/
    void receiveAndBroadcastMessage(Message message, User sender, ArrayList<ClientConnection_RMI> connectedClients, ServerConnection_RMI server) throws RemoteException;

    /** Adds user to its planning poker session's list of connected user, then broadcasts userlist to all users in that session */
    void addUserToSession(User user, ArrayList<ClientConnection_RMI> connectedClients, ServerConnection_RMI server) throws RemoteException;

    /** Broadcasts userlist of senders planningpoker session to all users in that session*/
    void broadcastUsers(User user, ArrayList<ClientConnection_RMI> connectedClients, ServerConnection_RMI server, PlanningPoker planningPoker) throws RemoteException;

    /** Broadcasts userlist of senders planningpoker session to all users in that session, except to the sender who is being removed from that session*/
    void broadcastUsersWithException(User removedUser, ArrayList<ClientConnection_RMI> connectedClients, ServerConnection_RMI server, PlanningPoker planningPoker) throws RemoteException;

    /** Removes the user from the session, and broadcasts that update to the rest of the users in that session*/
    void removeUserFromSession(User user, ArrayList<ClientConnection_RMI> connectedClients, ServerConnection_RMI server) throws RemoteException;
}
