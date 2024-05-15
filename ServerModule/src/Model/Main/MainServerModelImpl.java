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
import Networking.Server_RMI;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.SQLException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import static DataTypes.UserRoles.UserRole.DEVELOPER;
import static DataTypes.UserRoles.UserRole.PRODUCT_OWNER;

public class MainServerModelImpl implements MainServerModel, Runnable {

    private final PropertyChangeSupport support;
    private volatile static MainServerModel instance;
    private static final Lock lock = new ReentrantLock();
    private ArrayList<PlanningPoker> planningPokerGames;

    private MainServerModelImpl() {
        //TODO: Refactor so that it in the future loads the games from a database.
        support = new PropertyChangeSupport(this);
        planningPokerGames = new ArrayList<>();
        getAllPlanningPokersFromDb();
    }

    @Override
    public boolean validatePlanningPoker(int planningPokerID) {
        for (PlanningPoker planningPoker : planningPokerGames) {
            if (planningPoker.getPlanningPokerID() == planningPokerID) {
                return true;
            }
        }
        return false;
    }

    @Override
    public PlanningPoker createPlanningPoker() {
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
    public PlanningPoker getPlanningPokerGame(int planningPokerId) {
        if (validatePlanningPoker(planningPokerId)) {
            for (PlanningPoker planningPoker : planningPokerGames) {
                if (planningPoker.getPlanningPokerID() == planningPokerId) {
                    return planningPoker;
                }
            }
        }
        return null;
    }

    @Override
    public void getAllPlanningPokersFromDb() {
        try {
            PlanningPokerDAO planningPokerDAO = PlanningPokerDAOImpl.getInstance();
            planningPokerGames = planningPokerDAO.getAllPlanningPoker();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Add new Planning Poker Game roles to this list when they are implemented. */
    @Override public User applyPlanningPokerRole(UserRole role, User user, int planningPokerId) {
        User returnUser;

        switch(role) {
            case DEVELOPER:
                returnUser = this.setDeveloper(user, planningPokerId);
                break;

            case PRODUCT_OWNER:
                returnUser = this.setProductOwner(user, planningPokerId);
                break;

            case SCRUM_MASTER:
                returnUser = this.setScrumMaster(user, planningPokerId);
                break;

            default:
                returnUser = null;
                break;
        }

        if(returnUser != null) {
            // Ensure that no other instances of this user is still in the planning poker game:
            // Find the Planning Poker game in the Array:
            for (int i = 0; i < planningPokerGames.size(); i++) {
                if(planningPokerGames.get(i).getPlanningPokerID() == (planningPokerId)) {
                    boolean userReplaced = false;
                    for (int j = 0; j < planningPokerGames.get(i).getConnectedUsers().size(); j++) {
                        // replace the first instance of this username, and remove all others
                        if(!userReplaced && planningPokerGames.get(i).getConnectedUsers().get(j).getUsername().equals(returnUser.getUsername())) {
                            planningPokerGames.get(i).getConnectedUsers().set(j, returnUser);
                            userReplaced = true;
                        } else if (planningPokerGames.get(i).getConnectedUsers().get(j).getUsername().equals(returnUser.getUsername())) {
                            planningPokerGames.get(i).getConnectedUsers().remove(j);
                            j--;
                        }
                    }
                }
            }
        }
        return returnUser;
    }

    private User setDeveloper(User user, int planningPokerId) {
        // Find the Planning Poker game in the Array:
        for (int i = 0; i < planningPokerGames.size(); i++) {
            if(planningPokerGames.get(i).getPlanningPokerID() == (planningPokerId)) {

                // Check if user was previously assigned to a key role, such as Scrum Master or Product Owner. If so, unassign them first.
                checkIfUserAlreadyIsAssignedToKeyRole(user, planningPokerGames.get(i));

                // Assign the new role to the user
                user.setRole(new Developer());
                System.out.println("MainServerModelImpl: [" + user.getUsername() + "] is now a Developer in game [" + planningPokerId + "]");
                return user;
            }
        }
        return null;
    }

    private User setScrumMaster(User user, int planningPokerId) {
        // Find the Planning Poker game in the Array:
        for (int i = 0; i < planningPokerGames.size(); i++) {
            if(planningPokerGames.get(i).getPlanningPokerID() == (planningPokerId)) {

                // Check if user was previously assigned to a key role, such as Scrum Master or Product Owner. If so, unassign them first.
                checkIfUserAlreadyIsAssignedToKeyRole(user, planningPokerGames.get(i));

                // Assign the new role to the user
                planningPokerGames.get(i).setScrumMaster(user);
                user.setRole(new ScrumMaster());
                System.out.println("MainServerModelImpl: ScrumMaster is now [" + user.getUsername() + "] in game [" + planningPokerId + "]");
                return user;
            }
        }
        return null;
    }

    private User setProductOwner(User user, int planningPokerId) {
        // Find the Planning Poker game in the Array:
        for (int i = 0; i < planningPokerGames.size(); i++) {
            if(planningPokerGames.get(i).getPlanningPokerID() == (planningPokerId)) {

                // Check if user was previously assigned to a key role, such as Scrum Master or Product Owner. If so, unassign them first.
                checkIfUserAlreadyIsAssignedToKeyRole(user, planningPokerGames.get(i));

                // Assign the new role to the user
                planningPokerGames.get(i).setProductOwner(user);
                user.setRole(new ProductOwner());
                System.out.println("MainServerModelImpl: ProductOwner is now [" + user.getUsername() + "] in game [" + planningPokerId + "]");
                return user;
            }
        }
        return null;
    }

    private void checkIfUserAlreadyIsAssignedToKeyRole(User user, PlanningPoker game) {
        if(game.getProductOwner() != null && game.getProductOwner().equals(user)) {
            // User is already assigned as the Product Owner. Unassign them in the Planning Poker Object.
            game.setProductOwner(null);
            System.out.println("MainServerModelImpl: [" + user.getUsername() + "] was removed as Product Owner in game [" + game.getPlanningPokerID() + "]");
        } else if (game.getScrumMaster() != null && game.getScrumMaster().equals(user)) {
            // User is already assigned as the Scrum Master. Unassign them in the Planning Poker Object.
            game.setScrumMaster(null);
            System.out.println("MainServerModelImpl: [" + user.getUsername() + "] was removed as Scrum Master in game [" + game.getPlanningPokerID() + "]");
        }
    }

    @Override public void broadcastPlanningPokerObjUpdate(Map<Integer, ArrayList<ClientConnection_RMI>> clientList, ServerConnection_RMI server, int planningPokerId) {
        ArrayList<ClientConnection_RMI> receivingClients = clientList.get(planningPokerId);
        if(receivingClients != null) {
            for (int i = 0; i < receivingClients.size(); i++) {
                if(receivingClients.get(i) == null) {
                    receivingClients.remove(i);
                    i--;
                }
            }

            System.out.println("Server: Broadcasting changes to the Planning Poker Object to clients in game [" + planningPokerId + "]");
            for (ClientConnection_RMI client : receivingClients) {
                //Create a new thread for each connected client, and then call the desired broadcast operation. This minimizes server lag/hanging due to clients who have connection issues.
                Thread transmitThread = new Thread(() -> {
                    try {
                        client.updatePlanningPokerObj(planningPokerId);
                    }
                    catch (RemoteException e) {
                        if(String.valueOf(e.getCause()).equals("java.net.ConnectException: Connection refused: connect")) {
                            //Unregisters clients from the Game Server, who have lost connection in order to avoid further server errors.
                            try {
                                server.unRegisterClientFromGame(client, planningPokerId);
                            } catch (RemoteException ex) {
                                throw new RuntimeException();
                            }
                        }
                        else {
                            //Error is something else:
                            e.printStackTrace();
                            throw new RuntimeException();
                        }
                    }
                });
                transmitThread.setDaemon(true);
                transmitThread.start();
            }
        }
    }

    @Override
    public void setProductOwner(User user, ArrayList<ClientConnection_RMI> connectedClients, Server_RMI serverRmi) {
        for(ClientConnection_RMI client : connectedClients) {
            try {
                if (client.getCurrentUser().equals(user))
                {
                    client.setRoleInGameFromServer(PRODUCT_OWNER, user.getPlanningPoker().getPlanningPokerID(), user);
                    break;
                }
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override public boolean removeUserFromGame(User user, int planningPokerId) {
        // Find the Planning Poker game in the Array:
        for (int i = 0; i < planningPokerGames.size(); i++) {
            if(planningPokerGames.get(i).getPlanningPokerID() == (planningPokerId)) {

                // Check if user was previously assigned to a key role, such as Scrum Master or Product Owner. If so, unassign them first.
                checkIfUserAlreadyIsAssignedToKeyRole(user, planningPokerGames.get(i));

                // remove the user from the connectedUsers array
                boolean userRemoved = false;
                for (int j = 0; j < planningPokerGames.get(i).getConnectedUsers().size(); j++) {
                    if(planningPokerGames.get(i).getConnectedUsers().get(j).getUsername().equals(user.getUsername())) {
                        planningPokerGames.get(i).getConnectedUsers().remove(j);
                        userRemoved = true;
                        j--;
                    }
                }
                if(userRemoved) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
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
