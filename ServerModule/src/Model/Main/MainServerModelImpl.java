package Model.Main;

import DataTypes.PlanningPoker;
import DataTypes.User;
import DataTypes.UserRoles.ConcreteRoles.Developer;
import DataTypes.UserRoles.ConcreteRoles.ProductOwner;
import DataTypes.UserRoles.ConcreteRoles.ScrumMaster;
import DataTypes.UserRoles.Role;
import DataTypes.UserRoles.UserRole;
import Database.PlanningPoker.PlanningPokerDAO;
import Database.PlanningPoker.PlanningPokerDAOImpl;
import Networking.ClientConnection_RMI;
import Networking.ServerConnection_RMI;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.SQLException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import static DataTypes.UserRoles.UserRole.DEVELOPER;

public class MainServerModelImpl implements MainServerModel, Runnable {

    private final PropertyChangeSupport support;
    private volatile static MainServerModel instance;
    private static final Lock lock = new ReentrantLock();
    private ArrayList<PlanningPoker> planningPokerGames;

    private MainServerModelImpl() {
        //TODO: Refactor so that it in the future loads the games from a database.
        support = new PropertyChangeSupport(this);
        planningPokerGames = new ArrayList<>();
    }

    @Override
    public boolean validatePlanningPoker(String planningPokerID) {
        for (PlanningPoker planningPoker : planningPokerGames) {
            if (planningPoker.getPlanningPokerID().equals(planningPokerID)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public PlanningPoker createPlanningPoker() {
    /* The old way of creating planning poker game.
    PlanningPoker planningPoker = new PlanningPoker();
    planningPoker.generatePlanningPokerID();
     */

        PlanningPoker planningPoker;
        try {
            PlanningPokerDAO planningPokerDAO = PlanningPokerDAOImpl.getInstance();
            planningPoker = planningPokerDAO.create();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Creating planningPokerID: " + planningPoker.getPlanningPokerID());

        planningPokerGames.add(planningPoker);
        return planningPoker;
    }

    @Override
    public PlanningPoker getPlanningPokerGame(String planningPokerId) {
        if (validatePlanningPoker(planningPokerId)) {
            for (PlanningPoker planningPoker : planningPokerGames) {
                if (planningPoker.getPlanningPokerID().equals(planningPokerId)) {
                    return planningPoker;
                }
            }
        }
        return null;
    }

    @Override
    public ArrayList<PlanningPoker> getAllPlanningPokersFromDb() {
        try {
            PlanningPokerDAO planningPokerDAO = PlanningPokerDAOImpl.getInstance();
            return planningPokerDAO.getAllPlanningPoker();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Add new Planning Poker Game roles to this list when they are implemented. */
    @Override public User applyPlanningPokerRole(UserRole role, User user, String gameId) {
        switch(role) {
            case DEVELOPER:
                return this.setDeveloper(user, gameId);

            case PRODUCT_OWNER:
                return this.setProductOwner(user, gameId);

            case SCRUM_MASTER:
                return this.setScrumMaster(user, gameId);

            default:
                return null;
        }
    }

    private User setDeveloper(User user, String gameId) {
        // Find the Planning Poker game in the Array:
        for (int i = 0; i < planningPokerGames.size(); i++) {
            if(planningPokerGames.get(i).getPlanningPokerID().equals(gameId)) {
                // TODO: Check if the user is either a ProductOwner or ScrumMaster in the current game, and remove them.

                user.setRole(new Developer());
                System.out.println("MainServerModelImpl: [" + user.getUsername() + "] is now a Developer in game [" + gameId + "]");
                return user;
            }
        }
        return null;
    }

    private User setScrumMaster(User user, String gameId) {
        // Find the Planning Poker game in the Array:
        for (int i = 0; i < planningPokerGames.size(); i++) {
            if(planningPokerGames.get(i).getPlanningPokerID().equals(gameId)) {
                planningPokerGames.get(i).setScrumMaster(user);
                user.setRole(new ScrumMaster());
                System.out.println("MainServerModelImpl: ScrumMaster is now [" + user.getUsername() + "] in game [" + gameId + "]");
                return user;
            }
        }
        return null;
    }

    private User setProductOwner(User user, String gameId) {
        // Find the Planning Poker game in the Array:
        for (int i = 0; i < planningPokerGames.size(); i++) {
            if(planningPokerGames.get(i).getPlanningPokerID().equals(gameId)) {
                planningPokerGames.get(i).setProductOwner(user);
                user.setRole(new ProductOwner());
                System.out.println("MainServerModelImpl: ProductOwner is now [" + user.getUsername() + "] in game [" + gameId + "]");
                return user;
            }
        }
        return null;
    }

    @Override public void broadcastPlanningPokerObjUpdate(Map<String, ArrayList<ClientConnection_RMI>> clientList, ServerConnection_RMI server, String gameId) {
        ArrayList<ClientConnection_RMI> receivingClients = clientList.get(gameId);
        if(receivingClients != null) {
            for (int i = 0; i < receivingClients.size(); i++) {
                if(receivingClients.get(i) == null) {
                    receivingClients.remove(i);
                    i--;
                }
            }

            System.out.println("Server: Broadcasting changes to the Planning Poker Object to clients in game [" + gameId + "]");
            for (ClientConnection_RMI client : receivingClients) {
                //Create a new thread for each connected client, and then call the desired broadcast operation. This minimizes server lag/hanging due to clients who have connection issues.
                Thread transmitThread = new Thread(() -> {
                    try {
                        client.updatePlanningPokerObj(gameId);
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


    public static MainServerModel getInstance() {
        //Here we use the "Double-checked lock" principle to ensure proper synchronization.
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new MainServerModelImpl();
                }
            }
        }
        return instance;
    }

    @Override
    public void addPropertyChangeListener(
            PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    @Override
    public void addPropertyChangeListener(String name,
                                          PropertyChangeListener listener) {
        support.addPropertyChangeListener(name, listener);
    }

    @Override
    public void removePropertyChangeListener(
            PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(String name,
                                             PropertyChangeListener listener) {
        support.removePropertyChangeListener(name, listener);
    }

    @Override
    public void run() {
        //TODO
    }
}
