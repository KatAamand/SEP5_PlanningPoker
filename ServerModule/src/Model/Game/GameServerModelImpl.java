package Model.Game;

import DataTypes.Effort;
import Database.Effort.EffortDAO;
import Database.Effort.EffortDAOImpl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.SQLException;
import java.util.ArrayList;
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
}
