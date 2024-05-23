package Networking;

import Application.Session;
import DataTypes.*;
import DataTypes.UserRoles.UserRole;
import javafx.application.Platform;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Client_RMI_Impl implements Client, ClientConnection_RMI, Serializable {

    private ServerConnection_RMI server;
    private final PropertyChangeSupport propertyChangeSupport;

    public Client_RMI_Impl() {
        propertyChangeSupport = new PropertyChangeSupport(this);
        try {
            UnicastRemoteObject.exportObject(this, 0);
            // Registry registry = LocateRegistry.getRegistry("192.168.1.166", 1099);
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            server = (ServerConnection_RMI) registry.lookup("Model");
            server.registerClient(this);
            server.registerClientListener(this);
            System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", Client_RMI: Connection to server established");

        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    // Requests for Login
    @Override
    public void validateUser(String username, String password) {
        try {
            server.validateUser(username, password, this);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", Client_RMI: User trying to validate");
    }

    @Override
    public void createUser(String username, String password) throws RuntimeException {
        try {
            server.createUser(username, password, this);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", Client_RMI: User trying to create user");
        // Requests for Login
    }

    @Override
    public boolean validatePlanningPokerID(int planningPokerID) {
        try {
            System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", Client_RMI: Trying to validate planningPokerID");
            boolean serverAnswer = server.validatePlanningPokerID(planningPokerID);
            if (serverAnswer) {
                System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", Client_RMI: PlanningPokerID is validated");
                propertyChangeSupport.firePropertyChange("planningPokerIDValidatedSuccess", null, planningPokerID);
            } else {
                System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", Client_RMI: planningPokerID validation failed");
            }
            return serverAnswer;
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override public void updatePlanningPokerObj(int planningPokerId) throws RemoteException {
        PlanningPoker serverAnswer = server.loadPlanningPokerGame(planningPokerId, this, false);
        System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", Client_RMI: Received updated Planning Poker Object from server");
        if (serverAnswer != null) {
            Platform.runLater(() -> propertyChangeSupport.firePropertyChange("PlanningPokerObjUpdated", null, serverAnswer));
        }
    }

    @Override
    public PlanningPoker loadPlanningPoker(int planningPokerId) throws RuntimeException {
        try {
            System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", Client_RMI: Trying to load PlanningPoker Game with ID " + planningPokerId);
            PlanningPoker serverAnswer = server.loadPlanningPokerGame(planningPokerId, this, true);

            if (serverAnswer != null) {
                //PlanningPoker loaded successfully
                Session.getCurrentUser().setPlanningPoker(serverAnswer);
                System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", Client_RMI: PlanningPoker game has been loaded successfully");
                return serverAnswer;
            }
        } catch (RemoteException e) {
            throw new RuntimeException();
        }
        return null;
    }

    @Override
    public void loadTaskList(int planningPokerId) throws RuntimeException {
        try {
            this.loadTaskListFromServer(planningPokerId);
        } catch (RemoteException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public User setRoleOnServer(UserRole roleToApply, int s, User userToReceiveRole) throws RemoteException {
        return server.setRoleInPlanningPokerGame(roleToApply, userToReceiveRole, s);
    }

    @Override
    public void setRoleInGameFromServer(UserRole userRole, int s, User user) throws RemoteException {
        setRoleInGame(userRole, s, user);
    }

    @Override
    public void loadTaskListFromServer(int planningPokerId) throws RemoteException {
        ArrayList<Task> taskList = server.getTaskList(planningPokerId);
        if (taskList != null) {
            // Fires a PropertyChange event with a Null value to absorb any similarities to the contents inside any of the already loaded taskList:
            propertyChangeSupport.firePropertyChange("receivedUpdatedTaskList", null, null);

            // Fires the proper PropertyChange event, with the taskList attached as the newValue():
            propertyChangeSupport.firePropertyChange("receivedUpdatedTaskList", null, taskList);
        }
    }

    @Override
    public void addTask(Task task, int planningPokerId) throws RuntimeException {
        try {
            this.addTaskToServer(task, planningPokerId);
        } catch (RemoteException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public boolean removeTask(Task task, int planningPokerId) throws RuntimeException {
        try {
            return this.removeTaskFromServer(task, planningPokerId);
        } catch (RemoteException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public boolean editTask(Task oldTask, Task newTask, int planningPokerId) throws RuntimeException {
        try {
            return this.editTaskOnServer(oldTask, newTask, planningPokerId);
        } catch (RemoteException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public void skipTasks(ArrayList<Task> skippedTasksList, int planningPokerId) throws RuntimeException {
        try {
            this.broadcastSkipTasksOnServer(skippedTasksList, planningPokerId);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendUser() {
        Platform.runLater(() -> {
            try {
                server.addConnectedUserToSession(Session.getCurrentUser());
            } catch (RemoteException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
            catch (NullPointerException e)
            {
            }
        });
    }

    @Override
    public void logoutUser(String username, String password) throws RuntimeException {
        //Check that the requested user is the local user before logging out (prevent local user from logging out other remote users):
        if (Session.getCurrentUser() != null
                && Session.getCurrentUser().getUsername().equals(username)
                && Session.getCurrentUser().getUsername().equals(password)) {
            try {
                this.logoutUserFromServer(username, password);
            } catch (RemoteException e) {
                throw new RuntimeException();
            }
        }

        //Disconnect the client:
        this.disconnectLocalClient();
    }


    private void disconnectLocalClient() {
        try {
            // Unregister the client from the server:
            server.unRegisterClient(this);

            // Unexport the client from the RMI:
            UnicastRemoteObject.unexportObject(this, true);

            System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", Client_RMI: Local Client is disconnected");
        } catch (RemoteException e) {
            throw new RuntimeException();
        }

        // Shut down the client thread:
        System.exit(0);
    }


    @Override
    public void logoutUserFromServer(String username, String password) throws RemoteException {
        server.logoutUser(username, password);
    }

    @Override
    public void userValidationFailed(String errorMessage) {
        propertyChangeSupport.firePropertyChange("userValidationFailed", null, errorMessage);
    }

    @Override
    public void userCreatedSuccessfully() {
        System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", Client_RMI: User is created successfully");
        propertyChangeSupport.firePropertyChange("userCreatedSuccess", null, null);
    }

    @Override
    public void updateUser(User user) {
        System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", Client_RMI: User is logged in successfully");
        propertyChangeSupport.firePropertyChange("userLoginSuccess", null, user);
    }

    @Override
    public PlanningPoker createPlanningPoker() throws RuntimeException {
        try {
            System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", Client_RMI: User trying to create planningPoker");
            PlanningPoker serverAnswer = server.createPlanningPoker(this);

            if (serverAnswer != null) {
                //PlanningPoker created successfully
                System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", Client_RMI: PlanningPokerID is created successfully");
                Session.getCurrentUser().setPlanningPoker(serverAnswer);
                propertyChangeSupport.firePropertyChange("planningPokerCreatedSuccess", null, serverAnswer);
                return serverAnswer;
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    @Override public void addPropertyChangeListener(PropertyChangeListener listener) {propertyChangeSupport.addPropertyChangeListener(listener);
    }

    @Override public void addPropertyChangeListener(String name, PropertyChangeListener listener) {propertyChangeSupport.addPropertyChangeListener(name, listener);
    }

    @Override public void removePropertyChangeListener(PropertyChangeListener listener) {propertyChangeSupport.removePropertyChangeListener(listener);
    }

    @Override public void removePropertyChangeListener(String name, PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(name, listener);
    }

    @Override
    public void updateSkippedTaskList(ArrayList<Task> skippedTasksList) throws RemoteException {
        propertyChangeSupport.firePropertyChange("receivedListOfTasksToSkip", null, skippedTasksList);
    }

    @Override
    public void removeUserFromSession() {
        try {
            server.removeUserFromSession(Session.getCurrentUser());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override public void removeUserFromGame(int planningPokerId) throws RuntimeException {
        try {
            server.removeUserFromGame(this, Session.getCurrentUser(), planningPokerId);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<Effort> getEffortList() {
        try {
            return server.getEffortList();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void placeCard(UserCardData userCardData) {
        try {
            System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", Client_RMI: Requesting placed card");
            server.placeCard(userCardData, Session.getConnectedGameId());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void requestClearPlacedCards() {
        try {
            server.requestClearPlacedCards(Session.getConnectedGameId());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addTaskToServer(Task task, int planningPokerId) throws RemoteException {
        server.addTask(task, planningPokerId);
    }

    @Override
    public boolean removeTaskFromServer(Task task, int planningPokerId) throws RemoteException {
        return server.removeTask(task, planningPokerId);
    }

    @Override
    public boolean editTaskOnServer(Task oldTask, Task newTask, int planningPokerId) throws RemoteException {
        return server.editTask(oldTask, newTask, planningPokerId);
    }

    @Override
    public void broadcastSkipTasksOnServer(ArrayList<Task> skippedTasksList, int planningPokerId) throws RemoteException {
        server.broadcastSkipTasks(skippedTasksList, planningPokerId);
    }

    @Override
    public void sendMessage(Message message, User sender) {
        try {
            server.sendMessage(message, sender);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void receiveMessage(Message message) {
        propertyChangeSupport.firePropertyChange("messageReceived", null, message);
        System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", Client_RMI: " + message.getMessage());
    }

    @Override
    public User getCurrentUser() throws RemoteException {
        return Session.getCurrentUser();
    }

    @Override
    public void receiveUser(ArrayList<User> users) throws RemoteException {
        Platform.runLater(() -> {
            //System.out.println("Client receiving user" + users);
            propertyChangeSupport.firePropertyChange("userReceived", null, users);
            propertyChangeSupport.firePropertyChange("UpdatedLocalUser", null, null);
        });

    }


    @Override
    public void receivePlacedCard(UserCardData userCardData) {
        System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", Client_RMI: Received placed card " + userCardData.getUsername() + " " + userCardData.getPlacedCard());
        propertyChangeSupport.firePropertyChange("placedCardReceived", null, userCardData);
    }

    @Override
    public void clearPlacedCards() {
        propertyChangeSupport.firePropertyChange("clearPlacedCards", null, null);
    }

    @Override
    public void showCards() {
        propertyChangeSupport.firePropertyChange("showCards", null, null);
    }

    @Override
    public void receiveRecommendedEffort(String recommendedEffort) {
        propertyChangeSupport.firePropertyChange("recommendedEffortReceived", null, recommendedEffort);
    }

    @Override
    public void startTimer() {
        propertyChangeSupport.firePropertyChange("startTimer", null, null);
    }

    @Override public void setRoleInGame(UserRole role, int planningPokerId, User user) throws RuntimeException {
        // Check if the user already has this role, if yes - do not trouble the server:
        if(user.getRole() != null && user.getRole().getUserRole() == role) {
            // User already has this role assigned. Do nothing.
            return;
        }

        User serverAnswer;
        try {
            serverAnswer = this.setRoleOnServer(role, planningPokerId, user);
        } catch (RemoteException e) {
            throw new RuntimeException();
        }

        // Update the local user in the local Session with returned modified user including the added roles - if the returned user is also the local user:
        if (serverAnswer != null && Session.getCurrentUser().getUsername().equals(serverAnswer.getUsername())) {
            Session.setCurrentUser(serverAnswer);
            System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", Client_RMI: Local user [" + Session.getCurrentUser().getUsername() + "] has become '" + Session.getCurrentUser().getRole().getRoleAsString() + "' in game [" + planningPokerId + "]");
            propertyChangeSupport.firePropertyChange("UpdatedLocalUser", null, null);

        }
    }

    @Override
    public void requestShowCards() {
        try {
            server.requestShowCards(Session.getConnectedGameId());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void setProductOwner(User user) {
        try {
            server.setProductOwner(user);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void requestUserList() {
        ArrayList<User> userList = null;
        try {
            userList = server.requestUserList();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        if (userList != null) {
            // Fires the proper PropertyChange event, with the taskList attached as the newValue():
            propertyChangeSupport.firePropertyChange("userListRecieved", null, userList);
        }
    }

    @Override
    public void requestStartGame(int connectedGameId) {
        try {
            server.requestStartGame(connectedGameId);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void requestRecommendedEffort() {
        try {
            server.requestRecommendedEffort(Session.getConnectedGameId());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void startGame(int connectedGameId) {
        System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", Client_RMI: Game started");
        propertyChangeSupport.firePropertyChange("gameStarted", null, connectedGameId);
    }
}

