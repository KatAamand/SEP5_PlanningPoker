package Views.GameView;

import Application.*;
import DataTypes.Task;
import Networking.ClientConnection_RMI;
import Networking.Client_RMI_Impl;
import Networking.ServerConnection_RMI;
import Networking.Server_RMI;
import Views.ForceSynchronizationOfScenarioTestClasses;
import Views.LoginView.LoginViewController;
import Views.MainView.MainViewController;
import Views.PlanningPokerView.PlanningPokerViewController;
import Views.TestServer;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.junit.jupiter.api.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;


@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class GameTestWithServerConnection {
    private static PlanningPokerViewController planningPokerViewController;
    private static MainViewController mainViewController;
    private static LoginViewController loginViewController;
    private static ClientConnection_RMI client;
    private static ClientConnection_RMI client2;
    private static ServerConnection_RMI server;
    private boolean runLaterExecuted = false;
    private boolean testUserLoggedIn = false;
    private static final int maxWaitingTicks = 1000;
    private static Thread serverThread;
    private static Thread clientThread;
    InnerTestClassListener testListener;


    @BeforeAll
    public static void initServerAndJavaFXClient() {
        //Try to acquire a centralized lock, in order to force UI related scenario test classes to be run 1 at a time instead of randomly:
        boolean lockAcquired = false;
        while (!lockAcquired) {
            lockAcquired = ForceSynchronizationOfScenarioTestClasses.getSynchronizationLock().tryLock();
            if (!lockAcquired) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    continue;
                }
            }
        }

        AtomicBoolean serverInitialized = new AtomicBoolean(false);
        AtomicBoolean clientInitialized = new AtomicBoolean(false);

        //Initializes a local server, that can be tested against:
        serverThread = new Thread(() -> {
            try {
                server = TestServer.getInstance();
                serverInitialized.set(true);
            } catch (RemoteException | AlreadyBoundException e) {
                throw new RuntimeException();
            }
        });
        serverThread.start();

        // Wait for server to be initialized in separate thread (Simulate separate server):
        int ticks = 0;
        while (!serverInitialized.get() && maxWaitingTicks >= ticks) {
            try {
                if (!serverInitialized.get()) {
                    Thread.sleep(10);
                    ticks++;
                }
            } catch (InterruptedException e) {
                ticks = Integer.MAX_VALUE;
            }
        }

        //Initializes the javaFx component library, and starts the server which the client uses to connect with.
        clientThread = new Thread(() -> {
            try {
                Platform.startup(() -> {
                    try {
                        client = ClientFactory.getInstance().getClient();
                        clientInitialized.set(true);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (IllegalStateException e) {
                Platform.runLater(() -> {
                    try {
                        client = ClientFactory.getInstance().getClient();
                        clientInitialized.set(true);
                    } catch (RemoteException ee) {
                        throw new RuntimeException(ee);
                    }
                });
            }
        });
        clientThread.start();

        // Wait for the javaFx component library to be initialized in a separate thread (Simulate the Client Connection running on a separate thread):
        ticks = 0;
        while (!clientInitialized.get() && maxWaitingTicks >= ticks) {
            try {
                if (!clientInitialized.get()) {
                    Thread.sleep(10);
                    ticks++;
                }
            } catch (InterruptedException e) {
                ticks = Integer.MAX_VALUE;
            }
        }
    }

    @AfterAll
    public static void shutDownJavaFxThreadAndServer() {
        //Terminate server:
        try {
            server.unRegisterClient(client);
            TestServer.resetServer();
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }

        // Release the centralized lock in order to allow other UI related test classes to execute their scenario tests:
        ForceSynchronizationOfScenarioTestClasses.getSynchronizationLock().unlock();
    }

    @BeforeEach
    void setUp() {
        // Arrange test parameters:
        String username = "test";
        String password = "1234";
        testListener = new InnerTestClassListener();

        // Login to the system with the test user account
        Platform.runLater(() -> {
            try {
                // Assign a testListener to catch events the server fires:
                client.addPropertyChangeListener(testListener);

                // Check that the login view is properly loaded / Start the application:
                loginViewController = ViewFactory.getInstance().loadLoginView();

                // Simulate user entering a username:
                loginViewController.usernameTextField.setText(username);

                // Simulate user entering a password:
                loginViewController.passwordTextField.setText(password);

                // Click "Login":
                loginViewController.loginButton.fire();
                testUserLoggedIn = true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Wait for runLater method to be executed:
        int ticks = 0;
        while (!testUserLoggedIn && maxWaitingTicks >= ticks) {
            try {
                if (!testUserLoggedIn) {
                    Thread.sleep(10);
                    ticks++;
                }
            } catch (InterruptedException e) {
                ticks = Integer.MAX_VALUE;
            }
        }

        // Assign the MainViewController:
        Platform.runLater(() -> {
            try {
                mainViewController = ViewFactory.getInstance().loadMainView();
                runLaterExecuted = true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Wait for setup to finish executing:
        ticks = 0;
        while (!runLaterExecuted && maxWaitingTicks >= ticks) {
            try {
                if (!runLaterExecuted) {
                    Thread.sleep(10);
                    ticks++;
                }
            } catch (InterruptedException e) {
                ticks = Integer.MAX_VALUE;
            }
        }
        runLaterExecuted = false;
    }

    @AfterEach
    void tearDown() {
        planningPokerViewController = null;
        runLaterExecuted = false;
        testUserLoggedIn = false;
        try {
            client.removePropertyChangeListener(testListener);
        } catch (RemoteException e) {
            //Do nothing
        }
    }


    @Test
    void refreshIfThereIsNoMoreTasks() {

    }

    @Test
    void getEffortList() {

    }

    @Test
    void showPlayingCards() {
    }

    /**
     * This inner class is used to handle events that are fired from the test server, in order to evaluate if operations from the client towards the server succeeded or not.
     */
    private static class InnerTestClassListener implements PropertyChangeListener {
        private String propertyName;
        private Object oldValue;
        private Object newValue;

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            propertyName = evt.getPropertyName();
            oldValue = evt.getOldValue();
            newValue = evt.getNewValue();
        }

        public String getPropertyName() {
            return propertyName;
        }

        public Object getOldValue() {
            return oldValue;
        }

        public Object getNewValue() {
            return newValue;
        }
    }
}