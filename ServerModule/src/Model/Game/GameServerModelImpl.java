package Model.Game;

import DataTypes.Effort;
import DataTypes.Task;
import DataTypes.UserCardData;
import Database.Effort.EffortDAO;
import Database.Effort.EffortDAOImpl;
import Networking.ClientConnection_RMI;
import Networking.ServerConnection_RMI;
import Networking.Server_RMI;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class GameServerModelImpl implements GameServerModel {

    private final PropertyChangeSupport propertyChangeSupport;
    private static volatile GameServerModel instance;
    private static final Lock lock = new ReentrantLock();
    private ArrayList<Effort> effortList;
    private Map<String, String> clientCardMap;

    private GameServerModelImpl() {
        effortList = new ArrayList<>();
        effortList = getEffortListFromDB();
        clientCardMap = new HashMap<>();
        propertyChangeSupport = new PropertyChangeSupport(this);
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

    @Override
    public void broadcastListOfSkippedTasksToClients(Map<Integer, ArrayList<ClientConnection_RMI>> clientList, ArrayList<Task> skippedTaskList, int planningPokerId, ServerConnection_RMI server) {
        // Create a list of all the clients, that should receive this updated list
        ArrayList<ClientConnection_RMI> receivingClients = clientList.get(planningPokerId);

        if (receivingClients != null) {
            // Remove any invalid / null clients from the list.
            for (int i = 0; i < receivingClients.size(); i++) {
                if (receivingClients.get(i) == null) {
                    receivingClients.remove(i);
                    i--;
                }
            }

            // Initiate broadcasting:
            for (ClientConnection_RMI client : receivingClients) {
                //Create a new thread for each connected client, and then call the desired broadcast operation. This minimizes server lag/hanging due to clients who have connection issues.
                Thread transmitThread = new Thread(() -> {
                    try {
                        client.updateSkippedTaskList(skippedTaskList);
                    } catch (RemoteException e) {
                        if (String.valueOf(e.getCause()).equals("java.net.ConnectException: Connection refused: connect")) {
                            //Unregisters clients from the Game Server, who have lost connection in order to avoid further server errors.
                            try {
                                server.unRegisterClientFromGame(client, planningPokerId);
                            } catch (RemoteException ex) {
                                throw new RuntimeException();
                            }
                        } else {
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

    @Override
    public void placeCard(UserCardData userCardData, ArrayList<ClientConnection_RMI> connectedClients, ServerConnection_RMI server) {
        clientCardMap.remove(userCardData.getUsername());
        clientCardMap.put(userCardData.getUsername(), userCardData.getPlacedCard());

        broadcastNewCard(userCardData, connectedClients, server);

        if (allCardsPlaced(connectedClients)) {
            broadcastStartTimer(connectedClients, server);
        }
    }

    private boolean allCardsPlaced(ArrayList<ClientConnection_RMI> connectedClients) {
        return clientCardMap.size() == connectedClients.size();
    }

    private void broadcastStartTimer(ArrayList<ClientConnection_RMI> connectedClients, ServerConnection_RMI server) {
        for (ClientConnection_RMI client : connectedClients) {
            Thread startTimerThread = new Thread(() -> {
                try {
                    client.startTimer();
                    System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", GameServerModelImpl: Starting timer");
                } catch (Exception e) {
                    if (String.valueOf(e.getCause()).equals("java.net.ConnectException: Connection refused: connect")) {
                        //Unregisters clients from the Server, who have lost connection in order to avoid further server errors.
                        try {
                            server.unRegisterClient(client);
                        } catch (RemoteException ex) {
                            throw new RuntimeException();
                        }
                    } else {
                        //Error is something else:
                        throw new RuntimeException();
                    }
                }
            });
            startTimerThread.setDaemon(true);
            startTimerThread.start();
        }
    }

    @Override
    public void clearPlacedCards(ArrayList<ClientConnection_RMI> connectedClients, Server_RMI serverRmi) {
        clientCardMap.clear();
        for (ClientConnection_RMI client : connectedClients) {
            Thread clearCardsThread = new Thread(() -> {
                try {
                    client.clearPlacedCards();
                } catch (Exception e) {
                    if (String.valueOf(e.getCause()).equals("java.net.ConnectException: Connection refused: connect")) {
                        //Unregisters clients from the Server, who have lost connection in order to avoid further server errors.
                        serverRmi.unRegisterClient(client);
                    } else {
                        //Error is something else:
                        throw new RuntimeException();
                    }
                }
            });
            clearCardsThread.setDaemon(true);
            clearCardsThread.start();
        }
    }

    @Override
    public void removeUser(String username) {
        clientCardMap.remove(username);
    }

    @Override
    public void showCards(ArrayList<ClientConnection_RMI> connectedClients, Server_RMI serverRmi) {
        for (ClientConnection_RMI client : connectedClients) {
            Thread showCardsThread = new Thread(() -> {
                try {
                    client.showCards();
                } catch (Exception e) {
                    if (String.valueOf(e.getCause()).equals("java.net.ConnectException: Connection refused: connect")) {
                        //Unregisters clients from the Server, who have lost connection in order to avoid further server errors.
                        serverRmi.unRegisterClient(client);
                    } else {
                        //Error is something else:
                        throw new RuntimeException();
                    }
                }
            });
            showCardsThread.setDaemon(true);
            showCardsThread.start();
        }
    }

    @Override
    public void requestStartGame(int connectedGameId, ArrayList<ClientConnection_RMI> connectedClients, Server_RMI serverRmi) {
        System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", GameServerModelImpl: Starting game");
        for (ClientConnection_RMI client : connectedClients) {
            Thread startGameThread = new Thread(() -> {
                try {
                    client.startGame(connectedGameId);
                } catch (Exception e) {
                    if (String.valueOf(e.getCause()).equals("java.net.ConnectException: Connection refused: connect")) {
                        //Unregisters clients from the Server, who have lost connection in order to avoid further server errors.
                        serverRmi.unRegisterClient(client);
                    } else {
                        //Error is something else:
                        throw new RuntimeException();
                    }
                }
            });
            startGameThread.setDaemon(true);
            startGameThread.start();
        }
    }

    private String getMaxEffort(List<String> placedCards) {
        List<Double> numericValues = new ArrayList<>();
        for (String card : placedCards) {
            if (card.equals("½")) {
                numericValues.add(0.5);
            } else {
                numericValues.add(Double.parseDouble(card));
            }
        }

        if (!numericValues.isEmpty()) {
            Double max = Collections.max(numericValues);

            if (max == 0.5) {
                return "½";
            } else if (max == 0.0) {
                return "0";
            } else {
                int maxInt = max.intValue();
                return String.valueOf(maxInt);
            }
        }

        return null;
    }

    @Override
    public void getRecommendedEffort(ArrayList<ClientConnection_RMI> connectedClients, Server_RMI serverRmi) {
        List<String> placedCards = new ArrayList<>();
        String recommendedEffort;

        for (Map.Entry<String, String> entry : clientCardMap.entrySet()) {
            placedCards.add(entry.getValue());
        }

        if (placedCards.contains("?")) {
            recommendedEffort = "?";
        } else if (placedCards.contains("∞")) {
            recommendedEffort = "∞";
        } else if (placedCards.contains("0")) {
            recommendedEffort = "0";
        } else {
            recommendedEffort = getMaxEffort(placedCards);
        }

        for (ClientConnection_RMI client : connectedClients) {
            String finalRecommendedEffort = recommendedEffort;
            Thread predictEffortThread = new Thread(() -> {
                try {
                    client.receiveRecommendedEffort(finalRecommendedEffort);
                } catch (Exception e) {
                    if (String.valueOf(e.getCause()).equals("java.net.ConnectException: Connection refused: connect")) {
                        //Unregisters clients from the Server, who have lost connection in order to avoid further server errors.
                        serverRmi.unRegisterClient(client);
                    } else {
                        //Error is something else:
                        throw new RuntimeException();
                    }
                }
            });
            predictEffortThread.setDaemon(true);
            predictEffortThread.start();
        }
    }

    @Override
    public void clearCardMap() {
        clientCardMap.clear();
    }

    public void broadcastNewCard(UserCardData userCardData, ArrayList<ClientConnection_RMI> connectedClients, ServerConnection_RMI server) {
        for (ClientConnection_RMI client : connectedClients) {
            Thread sendCardsThread = new Thread(() -> {
                try {
                    client.receivePlacedCard(userCardData);
                } catch (Exception e) {
                    if (String.valueOf(e.getCause()).equals("java.net.ConnectException: Connection refused: connect")) {
                        //Unregisters clients from the Server, who have lost connection in order to avoid further server errors.
                        try {
                            server.unRegisterClient(client);
                        } catch (RemoteException ex) {
                            throw new RuntimeException();
                        }
                    } else {
                        //Error is something else:
                        throw new RuntimeException();
                    }
                }
            });
            sendCardsThread.setDaemon(true);
            sendCardsThread.start();
        }
    }

    public Map<String, String> getClientCardMap() {
        return clientCardMap;
    } //TODO: Method is never used. Consider removing.

}
