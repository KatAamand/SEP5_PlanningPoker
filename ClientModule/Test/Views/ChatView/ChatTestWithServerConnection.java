package Views.ChatView;

import Application.ClientFactory;
import Application.ViewFactory;
import DataTypes.Message;
import Networking.ClientConnection_RMI;
import Networking.Client_RMI_Impl;
import Networking.ServerConnection_RMI;
import Views.ForceSynchronizationOfScenarioTestClasses;
import Views.LoginView.LoginViewController;
import Views.MainView.MainViewController;
import Views.PlanningPokerView.PlanningPokerViewController;
import Views.TestServer;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class ChatTestWithServerConnection
{
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
  InnerTestClassListener testListenerlocalClient;
  InnerTestClassListener testlistenerRemoteClient1;

  @BeforeAll public static void initServerAndJavaFxClient()
  {
    //Try to acquire a centralized lock, in order to force UI related scenario test classes to be run 1 at a time instead of randomly:
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

    AtomicBoolean serverInitialized = new AtomicBoolean(false);
    AtomicBoolean clientInitialized = new AtomicBoolean(false);

    //Initializes a local server, that can be tested against:
    serverThread = new Thread(() -> {
      try
      {
        server = TestServer.getInstance();
        serverInitialized.set(true);
      }
      catch (RemoteException | AlreadyBoundException e)
      {
        throw new RuntimeException();
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
    }

    //Initializes the javaFx component library, and starts the server which the client uses to connect with.
    clientThread = new Thread (() -> {
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
    while(!clientInitialized.get() && maxWaitingTicks >= ticks) {
      try {
        if(!clientInitialized.get()) {
          Thread.sleep(10);
          ticks++;
        }
      } catch (InterruptedException e) {
        ticks = Integer.MAX_VALUE;
      }
    }
  }


  @AfterAll public static void shutDownJavaFxThreadAndServer()
  {
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

  @BeforeEach void setUp()
  {
    // Arrange test parameters:
    String username = "test";
    String password = "1234";
    testListenerlocalClient = new InnerTestClassListener();

    // Login to the system with the test user account
    Platform.runLater(() -> {
      try
      {
        // Assign a testListener to catch events the server fires:
        client.addPropertyChangeListener(testListenerlocalClient);

        // Check that the login view is properly loaded / Start the application:
        loginViewController = ViewFactory.getInstance().loadLoginView();

        // Simulate user entering a username:
        loginViewController.usernameTextField.setText(username);

        // Simulate user entering a password:
        loginViewController.passwordTextField.setText(password);

        // Click "Login":
        loginViewController.loginButton.fire();
        testUserLoggedIn = true;
      } catch (IOException e)
      {
        throw new RuntimeException(e);
      }
    });

    // Wait for runLater method to be executed:
    int ticks = 0;
    while(!testUserLoggedIn && maxWaitingTicks >= ticks) {
      try {
        if(!testUserLoggedIn) {
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
      } catch (IOException e)
      {
        throw new RuntimeException(e);
      }
    });

    // Wait for setup to finish executing:
    ticks = 0;
    while(!runLaterExecuted && maxWaitingTicks >= ticks) {
      try {
        if(!runLaterExecuted) {
          Thread.sleep(10);
          ticks++;
        }
      } catch (InterruptedException e) {
        ticks = Integer.MAX_VALUE;
      }
    }
    runLaterExecuted = false;
  }

  @AfterEach void tearDown()
  {
    planningPokerViewController = null;
    runLaterExecuted = false;
    testUserLoggedIn = false;
    try {
      client.removePropertyChangeListener(testListenerlocalClient);
    } catch (RemoteException e) {
      //Do nothing
    }
  }

  @Test public void sendAndReceiveChatMessagesBetween2Clients_MainSunnyScenario() {
    // Simulated data to transmit:
    String transmitString = "Dette er bruger1";

    // Simulate another user creating a planning poker game, on the server:
    runLaterExecuted = false;
    Platform.runLater(() -> {
      client2 = new Client_RMI_Impl();
      testlistenerRemoteClient1 = new InnerTestClassListener();

      // Assign a testListener for the simulated remote client to catch events the server fires:
      try {
        server.registerClient(client2);
        client2.addPropertyChangeListener(testlistenerRemoteClient1);
      } catch (RemoteException e) {
        throw new RuntimeException();
      }

      runLaterExecuted = true;
    });

    // Wait for runLater method to finish executing:
    int ticks = 0;
    while(!runLaterExecuted && maxWaitingTicks >= ticks) {
      try {
        if(!runLaterExecuted) {
          Thread.sleep(10);
          ticks++;
        }
      } catch (InterruptedException e) {
        ticks = Integer.MAX_VALUE;
      }
    }
    runLaterExecuted = false;

    String createdGameID;
    try {
      createdGameID = server.createPlanningPoker(client2).getPlanningPokerID();
    } catch (RemoteException e) {
      throw new RuntimeException();
    }

    Platform.runLater(() -> {
      try {
        // Simulate user entering the created gameID to join:
        mainViewController.planningPokerIdTextField.setText(createdGameID);

        // Simulate user pressing the 'Join Planning Poker' button:
        mainViewController.connectToPlanningPokerButton.fire();

        // Assign the PlanningPokerViewController:
        planningPokerViewController = ViewFactory.getInstance().loadPlanningPokerView();
        Stage activeStage = (Stage) planningPokerViewController.getChatViewController().chatTextArea.getScene().getWindow();
        activeStage.hide();

        // Simulate the remote user joining the planning poker game:
        client2.validatePlanningPokerID(createdGameID);

        runLaterExecuted = true;
      } catch (IOException e)
      {
        throw new RuntimeException(e);
      }
    });


    // Wait for runLater method to finish executing:
    ticks = 0;
    while(!runLaterExecuted && maxWaitingTicks >= ticks) {
      try {
        if(!runLaterExecuted) {
          Thread.sleep(10);
          ticks++;
        }
      } catch (InterruptedException e) {
        ticks = Integer.MAX_VALUE;
      }
    }
    runLaterExecuted = false;


    // Check that the user joined the planning poker session by catching the server response:
    boolean wasPlanningPokerGameCreated = testListenerlocalClient.getPropertyName().equals("planningPokerIDValidatedSuccess");

    // 1. Local user enters text into the chat field:
    planningPokerViewController.getChatViewController().messageInputTextField.setText(transmitString);

    // 2. Local user clicks 'send'
    planningPokerViewController.getChatViewController().onMessageSendButtonPressed();

    // Check if local user received the sent message from the server:
    boolean localUserReceivedMessageFromServer = false;
    if(testListenerlocalClient.getPropertyName() != null) {
      localUserReceivedMessageFromServer = testListenerlocalClient.getPropertyName().equals("messageReceived");
    }

    // Check if simulated remote user received the sent message from the server:
    boolean remoteUserReceivedMessageFromServer = false;
    if(testlistenerRemoteClient1.getPropertyName() != null) {
      remoteUserReceivedMessageFromServer = testlistenerRemoteClient1.getPropertyName().equals("messageReceived");
    }

    // Unassign the remote listener created during this test:
    try {
      server.unRegisterClient(client2);
      client.removePropertyChangeListener(testlistenerRemoteClient1);
    } catch (RemoteException e) {
      //Do nothing
    }

    // Allow server and client time to update UI with received message:
    try {
      Thread.sleep(250);
    } catch (InterruptedException e) {
    }

    // Check that the received message appears on the chat history shown in the UI:
    boolean localUserSeesChatMessageInUI = planningPokerViewController.getChatViewController().chatTextArea.getText().contains(transmitString);

    // Combine earlier boolean evaluations:
    boolean result = localUserSeesChatMessageInUI && localUserReceivedMessageFromServer && remoteUserReceivedMessageFromServer && wasPlanningPokerGameCreated;

    // Assert
    assertEquals(true, result, "Should be true. Message should have been sent and received successfully");
  }


  @Test public void sendAndReceiveChatMessagesWithOnly1ClientConnectedToGame() {
    // Simulated data to transmit:
    String transmitString = "Dette er bruger1";

    // Simulate another user creating a planning poker game, on the server:
    runLaterExecuted = false;
    Platform.runLater(() -> {
      client2 = new Client_RMI_Impl();
      testlistenerRemoteClient1 = new InnerTestClassListener();

      // Assign a testListener for the simulated remote client to catch events the server fires:
      try {
        server.registerClient(client2);
        client2.addPropertyChangeListener(testlistenerRemoteClient1);
      } catch (RemoteException e) {
        throw new RuntimeException();
      }

      runLaterExecuted = true;
    });

    // Wait for runLater method to finish executing:
    int ticks = 0;
    while(!runLaterExecuted && maxWaitingTicks >= ticks) {
      try {
        if(!runLaterExecuted) {
          Thread.sleep(10);
          ticks++;
        }
      } catch (InterruptedException e) {
        ticks = Integer.MAX_VALUE;
      }
    }
    runLaterExecuted = false;

    String createdGameID;
    try {
      createdGameID = server.createPlanningPoker(client).getPlanningPokerID();
    } catch (RemoteException e) {
      throw new RuntimeException();
    }

    Platform.runLater(() -> {
      try {
        // Simulate user entering the created gameID to join:
        mainViewController.planningPokerIdTextField.setText(createdGameID);

        // Simulate user pressing the 'Join Planning Poker' button:
        mainViewController.connectToPlanningPokerButton.fire();

        // Assign the PlanningPokerViewController:
        planningPokerViewController = ViewFactory.getInstance().loadPlanningPokerView();
        Stage activeStage = (Stage) planningPokerViewController.getChatViewController().chatTextArea.getScene().getWindow();
        activeStage.hide();

        runLaterExecuted = true;
      } catch (IOException e)
      {
        throw new RuntimeException(e);
      }
    });


    // Wait for runLater method to finish executing:
    ticks = 0;
    while(!runLaterExecuted && maxWaitingTicks >= ticks) {
      try {
        if(!runLaterExecuted) {
          Thread.sleep(10);
          ticks++;
        }
      } catch (InterruptedException e) {
        ticks = Integer.MAX_VALUE;
      }
    }
    runLaterExecuted = false;


    // Check that the user joined the planning poker session by catching the server response:
    boolean wasPlanningPokerGameCreated = testListenerlocalClient.getPropertyName().equals("planningPokerIDValidatedSuccess");

    // 1. Local user enters text into the chat field:
    planningPokerViewController.getChatViewController().messageInputTextField.setText(transmitString);

    // 2. Local user clicks 'send'
    planningPokerViewController.getChatViewController().onMessageSendButtonPressed();

    // Check if local user received the sent message from the server:
    boolean localUserReceivedMessageFromServer = false;
    if(testListenerlocalClient.getPropertyName() != null) {
      localUserReceivedMessageFromServer = testListenerlocalClient.getPropertyName().equals("messageReceived");
    }

    // Allow server and client time to update UI with received message:
    try {
      Thread.sleep(250);
    } catch (InterruptedException e) {
    }

    // Check that the received message appears on the chat history shown in the UI:
    boolean localUserSeesChatMessageInUI = planningPokerViewController.getChatViewController().chatTextArea.getText().contains(transmitString);

    // Combine earlier boolean evaluations:
    boolean result = localUserSeesChatMessageInUI && localUserReceivedMessageFromServer && wasPlanningPokerGameCreated;

    // Assert
    assertEquals(true, result, "Should be true. Message should have been sent and received successfully");
  }


  @Test public void sendAndReceiveChatMessagesBetween2ClientsWhen3ClientsHaveBeenConnectedButOneOfThemHasLeftTheGameSession() {
    // Assert
    assertEquals(true, false, "Test must be done manually. Please refer to test-case #4 for a detailed description.\n As of 05/05-2024 this issue was still present. ");
  }


  @Test public void systemRefusesToSendEmptyChatMessages() {
    // Simulated data to transmit:
    String transmitString = "";

    // Simulate another user creating a planning poker game, on the server:
    runLaterExecuted = false;
    Platform.runLater(() -> {
      client2 = new Client_RMI_Impl();
      testlistenerRemoteClient1 = new InnerTestClassListener();

      // Assign a testListener for the simulated remote client to catch events the server fires:
      try {
        server.registerClient(client2);
        client2.addPropertyChangeListener(testlistenerRemoteClient1);
      } catch (RemoteException e) {
        throw new RuntimeException();
      }

      runLaterExecuted = true;
    });

    // Wait for runLater method to finish executing:
    int ticks = 0;
    while(!runLaterExecuted && maxWaitingTicks >= ticks) {
      try {
        if(!runLaterExecuted) {
          Thread.sleep(10);
          ticks++;
        }
      } catch (InterruptedException e) {
        ticks = Integer.MAX_VALUE;
      }
    }
    runLaterExecuted = false;

    String createdGameID;
    try {
      createdGameID = server.createPlanningPoker(client2).getPlanningPokerID();
    } catch (RemoteException e) {
      throw new RuntimeException();
    }

    Platform.runLater(() -> {
      try {
        // Simulate user entering the created gameID to join:
        mainViewController.planningPokerIdTextField.setText(createdGameID);

        // Simulate user pressing the 'Join Planning Poker' button:
        mainViewController.connectToPlanningPokerButton.fire();

        // Assign the PlanningPokerViewController:
        planningPokerViewController = ViewFactory.getInstance().loadPlanningPokerView();
        Stage activeStage = (Stage) planningPokerViewController.getChatViewController().chatTextArea.getScene().getWindow();
        activeStage.hide();

        // Simulate the remote user joining the planning poker game:
        client2.validatePlanningPokerID(createdGameID);

        runLaterExecuted = true;
      } catch (IOException e)
      {
        throw new RuntimeException(e);
      }
    });


    // Wait for runLater method to finish executing:
    ticks = 0;
    while(!runLaterExecuted && maxWaitingTicks >= ticks) {
      try {
        if(!runLaterExecuted) {
          Thread.sleep(10);
          ticks++;
        }
      } catch (InterruptedException e) {
        ticks = Integer.MAX_VALUE;
      }
    }
    runLaterExecuted = false;


    // Check that the user joined the planning poker session by catching the server response:
    boolean wasPlanningPokerGameCreated = testListenerlocalClient.getPropertyName().equals("planningPokerIDValidatedSuccess");

    // 1. Local user enters text into the chat field:
    planningPokerViewController.getChatViewController().messageInputTextField.setText(transmitString);

    // 2. Local user clicks 'send'
    planningPokerViewController.getChatViewController().onMessageSendButtonPressed();

    // Check if local user received the sent message from the server:
    boolean localUserReceivedMessageFromServer = false;
    if(testListenerlocalClient.getPropertyName() != null) {
      localUserReceivedMessageFromServer = testListenerlocalClient.getPropertyName().equals("messageReceived");
    }

    // Check if simulated remote user received the sent message from the server:
    boolean remoteUserReceivedMessageFromServer = false;
    if(testlistenerRemoteClient1.getPropertyName() != null) {
      remoteUserReceivedMessageFromServer = testlistenerRemoteClient1.getPropertyName().equals("messageReceived");
    }

    // Unassign the remote listener created during this test:
    try {
      server.unRegisterClient(client2);
      client.removePropertyChangeListener(testlistenerRemoteClient1);
    } catch (RemoteException e) {
      //Do nothing
    }

    // Allow server and client time to update UI with received message:
    try {
      Thread.sleep(250);
    } catch (InterruptedException e) {
    }

    // Combine earlier boolean evaluations:
    boolean result = !localUserReceivedMessageFromServer && !remoteUserReceivedMessageFromServer && wasPlanningPokerGameCreated;

    // Assert
    assertEquals(true, result, "Should be false. Empty message should NOT have been sent and received successfully");
  }


  @Test public void systemRefusesToSendNullChatMessages() {
    // Simulated data to transmit:
    String transmitString = null;

    // Simulate another user creating a planning poker game, on the server:
    runLaterExecuted = false;
    Platform.runLater(() -> {
      client2 = new Client_RMI_Impl();
      testlistenerRemoteClient1 = new InnerTestClassListener();

      // Assign a testListener for the simulated remote client to catch events the server fires:
      try {
        server.registerClient(client2);
        client2.addPropertyChangeListener(testlistenerRemoteClient1);
      } catch (RemoteException e) {
        throw new RuntimeException();
      }

      runLaterExecuted = true;
    });

    // Wait for runLater method to finish executing:
    int ticks = 0;
    while(!runLaterExecuted && maxWaitingTicks >= ticks) {
      try {
        if(!runLaterExecuted) {
          Thread.sleep(10);
          ticks++;
        }
      } catch (InterruptedException e) {
        ticks = Integer.MAX_VALUE;
      }
    }
    runLaterExecuted = false;

    String createdGameID;
    try {
      createdGameID = server.createPlanningPoker(client2).getPlanningPokerID();
    } catch (RemoteException e) {
      throw new RuntimeException();
    }

    Platform.runLater(() -> {
      try {
        // Simulate user entering the created gameID to join:
        mainViewController.planningPokerIdTextField.setText(createdGameID);

        // Simulate user pressing the 'Join Planning Poker' button:
        mainViewController.connectToPlanningPokerButton.fire();

        // Assign the PlanningPokerViewController:
        planningPokerViewController = ViewFactory.getInstance().loadPlanningPokerView();
        Stage activeStage = (Stage) planningPokerViewController.getChatViewController().chatTextArea.getScene().getWindow();
        activeStage.hide();

        // Simulate the remote user joining the planning poker game:
        client2.validatePlanningPokerID(createdGameID);

        runLaterExecuted = true;
      } catch (IOException e)
      {
        throw new RuntimeException(e);
      }
    });


    // Wait for runLater method to finish executing:
    ticks = 0;
    while(!runLaterExecuted && maxWaitingTicks >= ticks) {
      try {
        if(!runLaterExecuted) {
          Thread.sleep(10);
          ticks++;
        }
      } catch (InterruptedException e) {
        ticks = Integer.MAX_VALUE;
      }
    }
    runLaterExecuted = false;


    // Check that the user joined the planning poker session by catching the server response:
    boolean wasPlanningPokerGameCreated = testListenerlocalClient.getPropertyName().equals("planningPokerIDValidatedSuccess");

    // 1. Local user enters text into the chat field:
    planningPokerViewController.getChatViewController().messageInputTextField.setText(transmitString);

    // 2. Local user clicks 'send'
    boolean localUserReceivedMessageFromServer = false;
    boolean remoteUserReceivedMessageFromServer = false;

    try {
      planningPokerViewController.getChatViewController().onMessageSendButtonPressed();

      // Check if local user received the sent message from the server:
      if(testListenerlocalClient.getPropertyName() != null) {
        localUserReceivedMessageFromServer = testListenerlocalClient.getPropertyName().equals("messageReceived");
      }

      // Check if simulated remote user received the sent message from the server:
      if(testlistenerRemoteClient1.getPropertyName() != null) {
        remoteUserReceivedMessageFromServer = testlistenerRemoteClient1.getPropertyName().equals("messageReceived");
      }
    } catch (NullPointerException ignored) {

    }

    // Unassign the remote listener created during this test:
    try {
      server.unRegisterClient(client2);
      client.removePropertyChangeListener(testlistenerRemoteClient1);
    } catch (RemoteException e) {
      //Do nothing
    }

    // Allow server and client time to update UI with received message:
    try {
      Thread.sleep(250);
    } catch (InterruptedException e) {
    }

    // Combine earlier boolean evaluations:
    boolean result = !localUserReceivedMessageFromServer && !remoteUserReceivedMessageFromServer && wasPlanningPokerGameCreated;

    // Assert
    assertEquals(true, result, "Should be false. NULL message should NOT have been sent and received successfully");
  }


  @Test public void receiveSeveralChatMessagesRightAfterEachOtherBetween2Clients() {
    // Simulated data to transmit:
    String localUser_transmitString = "Dette er bruger1";
    Message remoteUser_firstMessage = new Message("Test fra bruger2");
    Message remoteUser_secondMessage = new Message("Dette er næste test fra Bruger2");

    // Simulate another user creating a planning poker game, on the server:
    runLaterExecuted = false;
    Platform.runLater(() -> {
      client2 = new Client_RMI_Impl();
      testlistenerRemoteClient1 = new InnerTestClassListener();

      // Assign a testListener for the simulated remote client to catch events the server fires:
      try {
        server.registerClient(client2);
        client2.addPropertyChangeListener(testlistenerRemoteClient1);
      } catch (RemoteException e) {
        throw new RuntimeException();
      }

      runLaterExecuted = true;
    });

    // Wait for runLater method to finish executing:
    int ticks = 0;
    while(!runLaterExecuted && maxWaitingTicks >= ticks) {
      try {
        if(!runLaterExecuted) {
          Thread.sleep(10);
          ticks++;
        }
      } catch (InterruptedException e) {
        ticks = Integer.MAX_VALUE;
      }
    }
    runLaterExecuted = false;

    String createdGameID;
    try {
      createdGameID = server.createPlanningPoker(client2).getPlanningPokerID();
    } catch (RemoteException e) {
      throw new RuntimeException();
    }

    Platform.runLater(() -> {
      try {
        // Simulate user entering the created gameID to join:
        mainViewController.planningPokerIdTextField.setText(createdGameID);

        // Simulate user pressing the 'Join Planning Poker' button:
        mainViewController.connectToPlanningPokerButton.fire();

        // Assign the PlanningPokerViewController:
        planningPokerViewController = ViewFactory.getInstance().loadPlanningPokerView();
        Stage activeStage = (Stage) planningPokerViewController.getChatViewController().chatTextArea.getScene().getWindow();
        activeStage.hide();

        // Simulate the remote user joining the planning poker game:
        client2.validatePlanningPokerID(createdGameID);

        runLaterExecuted = true;
      } catch (IOException e)
      {
        throw new RuntimeException(e);
      }
    });


    // Wait for runLater method to finish executing:
    ticks = 0;
    while(!runLaterExecuted && maxWaitingTicks >= ticks) {
      try {
        if(!runLaterExecuted) {
          Thread.sleep(10);
          ticks++;
        }
      } catch (InterruptedException e) {
        ticks = Integer.MAX_VALUE;
      }
    }
    runLaterExecuted = false;


    // Check that the user joined the planning poker session by catching the server response:
    boolean wasPlanningPokerGameCreated = testListenerlocalClient.getPropertyName().equals("planningPokerIDValidatedSuccess");

    // 1. Local user enters text into the chat field:
    planningPokerViewController.getChatViewController().messageInputTextField.setText(localUser_transmitString);

    // 2+3. Remote user sends a text message;
    try {
      server.sendMessage(remoteUser_firstMessage, client2.getCurrentUser());
    } catch (RemoteException e) {
      throw new RuntimeException();
    }

    // Check if local user received the sent message from the server:
    boolean localUserReceivedFirstMessageFromServer = false;
    if(testListenerlocalClient.getPropertyName() != null) {
      localUserReceivedFirstMessageFromServer = testListenerlocalClient.getPropertyName().equals("messageReceived");
    }

    // Check if simulated remote user received the sent message from the server:
    boolean remoteUserReceivedFirstMessageFromServer = false;
    if(testlistenerRemoteClient1.getPropertyName() != null) {
      remoteUserReceivedFirstMessageFromServer = testlistenerRemoteClient1.getPropertyName().equals("messageReceived");
    }

    // 4+5. Remote user sends another text message;
    try {
      server.sendMessage(remoteUser_secondMessage, client2.getCurrentUser());
    } catch (RemoteException e) {
      throw new RuntimeException();
    }

    // Check if local user received the sent message from the server:
    boolean localUserReceivedSecondMessageFromServer = false;
    if(testListenerlocalClient.getPropertyName() != null) {
      localUserReceivedSecondMessageFromServer = testListenerlocalClient.getPropertyName().equals("messageReceived");
    }

    // Check if simulated remote user received the sent message from the server:
    boolean remoteUserReceivedSecondMessageFromServer = false;
    if(testlistenerRemoteClient1.getPropertyName() != null) {
      remoteUserReceivedSecondMessageFromServer = testlistenerRemoteClient1.getPropertyName().equals("messageReceived");
    }

    // 6. Local user sends a message 'send'
    planningPokerViewController.getChatViewController().messageInputTextField.setText(localUser_transmitString);
    planningPokerViewController.getChatViewController().onMessageSendButtonPressed();

    // Check if local user received the sent message from the server:
    boolean localUserReceivedThirdMessageFromServer = false;
    if(testListenerlocalClient.getPropertyName() != null) {
      localUserReceivedThirdMessageFromServer = testListenerlocalClient.getPropertyName().equals("messageReceived");
    }

    // Check if simulated remote user received the sent message from the server:
    boolean remoteUserReceivedThirdMessageFromServer = false;
    if(testlistenerRemoteClient1.getPropertyName() != null) {
      remoteUserReceivedThirdMessageFromServer = testlistenerRemoteClient1.getPropertyName().equals("messageReceived");
    }

    // Unassign the remote listener created during this test:
    try {
      server.unRegisterClient(client2);
      client.removePropertyChangeListener(testlistenerRemoteClient1);
    } catch (RemoteException e) {
      //Do nothing
    }

    // Allow server and client time to update UI with received message:
    try {
      Thread.sleep(250);
    } catch (InterruptedException e) {
    }

    // Check that the received message appears on the chat history shown in the UI:
    boolean localUserSeesChatMessageInUI = planningPokerViewController.getChatViewController().chatTextArea.getText().contains(localUser_transmitString)
        && planningPokerViewController.getChatViewController().chatTextArea.getText().contains(remoteUser_firstMessage.getMessage())
        && planningPokerViewController.getChatViewController().chatTextArea.getText().contains(remoteUser_secondMessage.getMessage());

    // Combine earlier boolean evaluations:
    boolean result = localUserSeesChatMessageInUI && localUserReceivedThirdMessageFromServer && remoteUserReceivedThirdMessageFromServer && localUserReceivedSecondMessageFromServer && remoteUserReceivedSecondMessageFromServer && localUserReceivedFirstMessageFromServer && remoteUserReceivedFirstMessageFromServer && wasPlanningPokerGameCreated;

    // Assert
    assertEquals(true, result, "Should be true. Message should have been sent and received successfully");
  }


  /** This inner class is used to handle events that are fired from the test server, in order to evaluate if operations from the client towards the server succeeded or not. */
  private static class InnerTestClassListener implements PropertyChangeListener
  {
    private String propertyName;
    private Object oldValue;
    private Object newValue;

    @Override public void propertyChange(PropertyChangeEvent evt) {
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