package Model.Game;

import DataTypes.Effort;
import DataTypes.Task;
import Database.Effort.EffortDAO;
import Database.Effort.EffortDAOImpl;
import Networking.ClientConnection_RMI;
import Networking.ServerConnection_RMI;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class GameServerModelImpl implements GameServerModel, Runnable {

    private PropertyChangeSupport propertyChangeSupport;
    private static GameServerModel instance;
    private static final Lock lock = new ReentrantLock();
    private ArrayList<Effort> effortList;

    private GameServerModelImpl() {
        effortList = new ArrayList<>();
        effortList = getEffortListFromDB();
    }

    public static GameServerModel getInstance() {
        //Here we use the "Double-checked lock" principle to ensure proper synchronization.
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new GameServerModelImpl();
                }
            }
        }
        return instance;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void addPropertyChangeListener(String name, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(name, listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(String name, PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(name, listener);
    }

    @Override
    public void run() {
        //TODO
    }

    @Override
    public ArrayList<Effort> getEffortList() {
        return effortList;
    }

    @Override
    public ArrayList<Effort> getEffortListFromDB() {
        try {
            EffortDAO effortDAO = EffortDAOImpl.getInstance();
            effortList = effortDAO.getEffortList();
            return effortList;
        } catch (SQLException e) {
            throw new RuntimeException("GameServerModel: Error while trying to get EffortList from EffortDAOImpl.");
        }
    }

    @Override public void broadcastListOfSkippedTasksToClients(Map<String, ArrayList<ClientConnection_RMI>> clientList, ArrayList<Task> skippedTaskList, String gameId, ServerConnection_RMI server) {
        ArrayList<ClientConnection_RMI> receivingClients = clientList.get(gameId);
        if(receivingClients != null) {
            for (int i = 0; i < receivingClients.size(); i++) {
                if(receivingClients.get(i) == null) {
                    receivingClients.remove(i);
                    i--;
                }
            }

            System.out.println("Server: Broadcasting list of skipped tasks to players in game [" + gameId + "]");
            for (ClientConnection_RMI client : receivingClients) {
                //Create a new thread for each connected client, and then call the desired broadcast operation. This minimizes server lag/hanging due to clients who have connection issues.
                Thread transmitThread = new Thread(() -> {
                    try {
                        client.updateSkippedTaskList(skippedTaskList);
                    }
                    catch (RemoteException e) {
                        if(String.valueOf(e.getCause()).equals("java.net.ConnectException: Connection refused: connect")) {
                            //Unregisters clients from the Game Server, who have lost connection in order to avoid further server errors.
                            try {
                                server.unRegisterClientFromGame(client, gameId);
                            } catch (RemoteException ex) {
                                throw new RuntimeException();
                            }
                        }
                        else {
                            //Error is something else:
                            throw new RuntimeException();
                        }
                    }
                });
                transmitThread.setDaemon(true);
                transmitThread.start();
            }
        }
    }
}
