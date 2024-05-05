package Views.LoginView;

import Application.ClientFactory;
import Networking.Client_RMI_Impl;
import Networking.ServerConnection_RMI;
import Views.ForceSynchronizationOfScenarioTestClasses;
import javafx.application.Platform;
import org.junit.jupiter.api.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static org.junit.jupiter.api.Assertions.*;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class LoginTestWithoutServerConnection
{
  @BeforeAll public static void setUp() {
    // Try to acquire a centralized lock, in order to force UI related scenario test classes to be run 1 at a time instead of randomly:
    boolean lockAcquired = false;
    while(!lockAcquired) {
      lockAcquired = ForceSynchronizationOfScenarioTestClasses.getSynchronizationLock().tryLock();
      if(!lockAcquired) {
        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          continue;
        }
      }
    }
    System.out.println("Running LoginTestWithoutServerConnection");
  }

  @AfterAll public static void breakDown() {
    // Release the centralized lock in order to allow other UI related test classes to execute their scenario tests:
    ForceSynchronizationOfScenarioTestClasses.getSynchronizationLock().unlock();
    System.out.println("Finished LoginTestWithoutServerConnection");
  }


  @Test public void clientThrowsRunTimeExceptionWhenMissingServerConnection () {
    //Initializes a local server, that can be tested against:
    /*AtomicBoolean serverInitialized = new AtomicBoolean(false);
    int maxWaitingTicks = 500;
    Thread serverThread = new Thread(() -> {
      try
      {
        ServerConnection_RMI server = new Server_RMI();
        Registry registry = LocateRegistry.createRegistry(1099);
        registry.bind("Model", server);
        serverInitialized.set(true);
      }
      catch (RemoteException | AlreadyBoundException e)
      {
        throw new RuntimeException(e);
      }
    });
    serverThread.start();

    // Wait for server to be initialized in separate thread (Simulate separate server):
    int ticks = 0;
    while(!serverInitialized.get() && maxWaitingTicks >= ticks) {
      try {
        if(!serverInitialized.get()) {
          Thread.sleep(10);
          ticks++;
        }
      } catch (InterruptedException e) {
        ticks = Integer.MAX_VALUE;
      }
    }*/

    //Initializes the javaFx component library, and simulates a client connecting to a server. Throws an error if server is not running, which is intended
    try {
      Platform.startup(() -> {
        try {
          ClientFactory.getInstance().getClient();
        } catch (RemoteException | RuntimeException e) {
          //Do nothing.
        }
      });
    } catch (IllegalStateException e) {
      Platform.runLater(() -> {
        try {
          ClientFactory.getInstance().getClient();
        } catch (RemoteException | RuntimeException ex) {
          //Do nothing
        }
      });
    }

    //Check if a client connection already has been established by any of the other test classes running side-by-side this class. If so, eliminate this connection for testing purposes here:
    try {
      ClientFactory.getInstance().getClient();
      //If we reach this line, and no exception has been thrown, then another thread has already established server connection. We must unregister our client here.
      Registry registry = LocateRegistry.getRegistry("localhost", 1099);
      try {
        ServerConnection_RMI server = (ServerConnection_RMI) registry.lookup("Model");
        server.unRegisterClient(ClientFactory.getInstance().getClient());
        ClientFactory.getInstance().getClient();
      } catch (NotBoundException e) {
        throw new RuntimeException(e);
      }
    } catch (RemoteException | RuntimeException e) {
      //Connection has not been established. Assert if exception is thrown:
      assertThrows(RuntimeException.class, () -> new Client_RMI_Impl(), "It is expected for an exception to be thrown when connection with the server cannot be established");
    }

  }
}