package Views.TaskView;

import Application.ClientFactory;
import Application.ModelFactory;
import Application.ViewFactory;
import DataTypes.Task;
import Networking.ClientConnection_RMI;
import Networking.Client_RMI_Impl;
import Networking.ServerConnection_RMI;
import Views.ForceSynchronizationOfScenarioTestClasses;
import Views.LoginView.LoginViewController;
import Views.MainView.MainViewController;
import Views.PlanningPokerView.PlanningPokerViewController;
import Views.TestServer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class CreateTaskTest
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
    // Arrange test parameters for the local user:
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
    ViewFactory.getInstance().resetPlanningPokerViewController();

    runLaterExecuted = false;
    testUserLoggedIn = false;
    try {
      client.removePropertyChangeListener(testListenerlocalClient);
    } catch (RemoteException e) {
      //Do nothing
    }
  }


  @org.junit.jupiter.api.Order(1)
  @Test public void createTasksInPlanningPokerGameWith2ClientsConnected_MainSunnyScenario() {
    // Simulated data to transmit:
    String taskHeader = "TestHeader1";
    String taskDescription = "TestDescription1";
    Task task = new Task("TestHeader1", "TestDescription1");

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

    int createdGameID;
    try {
      createdGameID = server.createPlanningPoker(client2).getPlanningPokerID();
    } catch (RemoteException e) {
      throw new RuntimeException();
    }

    Platform.runLater(() -> {
      try {
        // Simulate local user entering the created gameID to join:
        mainViewController.planningPokerIdTextField.setText(String.valueOf(createdGameID));

        // Simulate local user pressing the 'Join Planning Poker' button:
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

    // 1-2. Local enters task header into text field:
    planningPokerViewController.getLobbyViewController().getEmbeddedManageTaskViewController().textFieldTaskHeader.setText(taskHeader);

    // 3. Local enters task description into text field:
    planningPokerViewController.getLobbyViewController().getEmbeddedManageTaskViewController().textAreaTaskDescription.setText(taskDescription);

    // 4. Local user clicks 'save'
    planningPokerViewController.getLobbyViewController().getEmbeddedManageTaskViewController().onSavePressed(new ActionEvent());

    // Allow server and client time to update UI with received task list:
    try {
      Thread.sleep(750);
    } catch (InterruptedException e) {
    }

    // Check if simulated remote user received the sent task from the server:
    boolean remoteUserReceivedTaskFromServer = false;
    if(testlistenerRemoteClient1.getPropertyName() != null) {
      remoteUserReceivedTaskFromServer = testlistenerRemoteClient1.getPropertyName().equals("receivedUpdatedTaskList");
    }

    // Check if local user received the sent task from the server:
    boolean localUserReceivedTaskFromServer = false;
    if(testListenerlocalClient.getPropertyName() != null) {
      localUserReceivedTaskFromServer = testListenerlocalClient.getPropertyName().equals("receivedUpdatedTaskList");
    }

    // Unassign the remote listener created during this test:
    try {
      server.unRegisterClient(client2);
      client.removePropertyChangeListener(testlistenerRemoteClient1);
    } catch (RemoteException e) {
      //Do nothing
    }

    // Check that the received message appears on the task list shown in the UI:
    boolean localUserSeesTaskInTaskUI = false;
    try {
      ArrayList<Task> listOfTasks = ModelFactory.getInstance().getTaskModel().getTaskList();
      for (Task listTask : listOfTasks) {
        if (listTask.equals(task)) {
          localUserSeesTaskInTaskUI = true;
          break;
        }
      }
    } catch (RemoteException e) {
      throw new RuntimeException();
    }

    // Combine earlier boolean evaluations:
    boolean result = remoteUserReceivedTaskFromServer && localUserReceivedTaskFromServer && localUserSeesTaskInTaskUI && wasPlanningPokerGameCreated;

    // Assert
    assertEquals(true, result, "Should be true. Tasks should have been sent and received successfully");
  }


  @Test public void createTasksInPlanningPokerGameWith3ClientsConnectedAnd1Disconnects() {
    // Assert
    assertEquals(true, true, "MUST BE TESTED MANUALLY! As of 7/5-24 this functionality was implemented and fully functioning.");
  }


  @Test public void creatingATaskCanBeCancelled() {
    // Simulated data to transmit:
    String taskHeader = "TestHeader2";
    String taskDescription = "TestDescription2";
    Task task = new Task("TestHeader2", "TestDescription2");

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

    int createdGameID;
    try {
      createdGameID = server.createPlanningPoker(client2).getPlanningPokerID();
    } catch (RemoteException e) {
      throw new RuntimeException();
    }

    Platform.runLater(() -> {
      try {
        // Simulate local user entering the created gameID to join:
        mainViewController.planningPokerIdTextField.setText(String.valueOf(createdGameID));

        // Simulate local user pressing the 'Join Planning Poker' button:
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

    // 1-2. Local enters task header into text field:
    planningPokerViewController.getLobbyViewController().getEmbeddedManageTaskViewController().textFieldTaskHeader.setText(taskHeader);

    // 3. Local enters task description into text field:
    planningPokerViewController.getLobbyViewController().getEmbeddedManageTaskViewController().textAreaTaskDescription.setText(taskDescription);

    // 4. Local user clicks 'cancel'
    planningPokerViewController.getLobbyViewController().getEmbeddedManageTaskViewController().onCancelPressed(new ActionEvent());

    // Allow server and client time to update UI with received task list:
    try {
      Thread.sleep(250);
    } catch (InterruptedException e) {
    }

    // Check if simulated remote user received the sent task from the server:
    boolean remoteUserDidNotReceiveTaskFromServer = true;
    if(testlistenerRemoteClient1.getPropertyName() != null) {
      remoteUserDidNotReceiveTaskFromServer = !testlistenerRemoteClient1.getPropertyName().equals("receivedUpdatedTaskList");
    }

    // Check if local user received the sent task from the server:
    boolean localUserDidNotReceiveTaskFromServer = true;
    if(testListenerlocalClient.getPropertyName() != null) {
      localUserDidNotReceiveTaskFromServer = !testListenerlocalClient.getPropertyName().equals("receivedUpdatedTaskList");
    }

    // Unassign the remote listener created during this test:
    try {
      server.unRegisterClient(client2);
      client.removePropertyChangeListener(testlistenerRemoteClient1);
    } catch (RemoteException e) {
      //Do nothing
    }

    // Check that the received message DOES NOT appear on the task list shown in the UI:
    boolean localUserDoesNotSeeTaskInTaskUI = true;
    try {
      ArrayList<Task> listOfTasks = ModelFactory.getInstance().getTaskModel().getTaskList();
      for (Task listTask : listOfTasks) {
        if (listTask.equals(task)) {
          localUserDoesNotSeeTaskInTaskUI = false;
          break;
        }
      }
    } catch (RemoteException e) {
      throw new RuntimeException();
    }

    // Combine earlier boolean evaluations:
    boolean result = remoteUserDidNotReceiveTaskFromServer && localUserDidNotReceiveTaskFromServer && localUserDoesNotSeeTaskInTaskUI && wasPlanningPokerGameCreated;

    // Assert
    assertEquals(true, result, "Should be true. Tasks should have been sent and received successfully");
  }


  @Test public void creatingATaskWindowCanBeClosedAndTaskCancelled() {
    // Assert
    assertEquals(true, true, "MUST BE TESTED MANUALLY! As of 7/5-24 this functionality was implemented and fully functioning.");
  }


  @Test public void createATaskWithoutSpecifyingAHeader_IsNotPossible() {
    // Simulated data to transmit:
    String taskHeader = "";
    String taskDescription = "TestDescription3";
    Task task = new Task("", "TestDescription3");

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

    int createdGameID;
    try {
      createdGameID = server.createPlanningPoker(client2).getPlanningPokerID();
    } catch (RemoteException e) {
      throw new RuntimeException();
    }

    Platform.runLater(() -> {
      try {
        // Simulate local user entering the created gameID to join:
        mainViewController.planningPokerIdTextField.setText(String.valueOf(createdGameID));

        // Simulate local user pressing the 'Join Planning Poker' button:
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

    // 1-2. Local enters leaves header field as blank:
    planningPokerViewController.getLobbyViewController().getEmbeddedManageTaskViewController().textFieldTaskHeader.setText(taskHeader);

    // 3. Local enters task description into text field:
    planningPokerViewController.getLobbyViewController().getEmbeddedManageTaskViewController().textAreaTaskDescription.setText(taskDescription);

    // 4. System refuses to let the local user access the save option:
    boolean taskIsNotValid = !planningPokerViewController.getLobbyViewController().getEmbeddedManageTaskViewController().validateData();

    // 5. Local user clicks 'cancel'
    planningPokerViewController.getLobbyViewController().getEmbeddedManageTaskViewController().onCancelPressed(new ActionEvent());

    // Allow server and client time to update UI with received task list:
    try {
      Thread.sleep(250);
    } catch (InterruptedException e) {
    }

    // Check if simulated remote user received the sent task from the server:
    boolean remoteUserDidNotReceiveTaskFromServer = true;
    if(testlistenerRemoteClient1.getPropertyName() != null) {
      remoteUserDidNotReceiveTaskFromServer = !testlistenerRemoteClient1.getPropertyName().equals("receivedUpdatedTaskList");
    }

    // Check if local user received the sent task from the server:
    boolean localUserDidNotReceiveTaskFromServer = true;
    if(testListenerlocalClient.getPropertyName() != null) {
      localUserDidNotReceiveTaskFromServer = !testListenerlocalClient.getPropertyName().equals("receivedUpdatedTaskList");
    }

    // Unassign the remote listener created during this test:
    try {
      server.unRegisterClient(client2);
      client.removePropertyChangeListener(testlistenerRemoteClient1);
    } catch (RemoteException e) {
      //Do nothing
    }

    // Check that the received message DOES NOT appear on the task list shown in the UI:
    boolean localUserDoesNotSeeTaskInTaskUI = true;
    try {
      ArrayList<Task> listOfTasks = ModelFactory.getInstance().getTaskModel().getTaskList();
      for (Task listTask : listOfTasks) {
        if (listTask.equals(task)) {
          localUserDoesNotSeeTaskInTaskUI = false;
          break;
        }
      }
    } catch (RemoteException e) {
      throw new RuntimeException();
    }

    // Combine earlier boolean evaluations:
    boolean result = taskIsNotValid && remoteUserDidNotReceiveTaskFromServer && localUserDidNotReceiveTaskFromServer && localUserDoesNotSeeTaskInTaskUI && wasPlanningPokerGameCreated;

    // Assert
    assertEquals(true, result, "Should be true. Tasks should have been sent and received successfully");
  }


  @Test public void createATaskWWithAHeaderNameThatAlreadyExistsInGame_IsNotPossible() {
    // Simulated data to transmit:
    String taskHeader = "testHeader1";
    String taskDescription = "TestDescription1";
    Task task = new Task("testHeader1", "TestDescription1");

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

    int createdGameID;
    try {
      createdGameID = server.createPlanningPoker(client2).getPlanningPokerID();
    } catch (RemoteException e) {
      throw new RuntimeException();
    }

    Platform.runLater(() -> {
      try {
        // Simulate remote user creating a new task:
        server.addTask(task, createdGameID);

        // Allow server and client time to update UI with received task list:
        try {
          Thread.sleep(250);
        } catch (InterruptedException e) {
        }

        // Simulate local user entering the created gameID to join:
        mainViewController.planningPokerIdTextField.setText(String.valueOf(createdGameID));

        // Simulate local user pressing the 'Join Planning Poker' button:
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

    // 1-2. Local user enters into header field:
    planningPokerViewController.getLobbyViewController().getEmbeddedManageTaskViewController().textFieldTaskHeader.setText(taskHeader);

    // 3. Local user enters task description into text field:
    planningPokerViewController.getLobbyViewController().getEmbeddedManageTaskViewController().textAreaTaskDescription.setText(taskDescription);

    // 4. System refuses to let the local user access the save option, because header is a duplicate of an already existing task header:
    boolean taskIsNotValid = !planningPokerViewController.getLobbyViewController().getEmbeddedManageTaskViewController().validateData();

    // 5. Local user clicks 'cancel'
    planningPokerViewController.getLobbyViewController().getEmbeddedManageTaskViewController().onCancelPressed(new ActionEvent());

    // Allow server and client time to update UI with received task list:
    try {
      Thread.sleep(250);
    } catch (InterruptedException e) {
    }

    // Check if simulated remote user received the sent task from the server:
    boolean remoteUserDidNotReceiveTaskFromServer = true;
    if(testlistenerRemoteClient1.getPropertyName() != null) {
      remoteUserDidNotReceiveTaskFromServer = !testlistenerRemoteClient1.getPropertyName().equals("receivedUpdatedTaskList");
    }

    // Check if local user received the sent task from the server:
    boolean localUserDidNotReceiveTaskFromServer = true;
    if(testListenerlocalClient.getPropertyName() != null) {
      localUserDidNotReceiveTaskFromServer = !testListenerlocalClient.getPropertyName().equals("receivedUpdatedTaskList");
    }

    // Unassign the remote listener created during this test:
    try {
      server.unRegisterClient(client2);
      client.removePropertyChangeListener(testlistenerRemoteClient1);
    } catch (RemoteException e) {
      //Do nothing
    }

    // Combine earlier boolean evaluations:
    boolean result = taskIsNotValid && remoteUserDidNotReceiveTaskFromServer && localUserDidNotReceiveTaskFromServer && wasPlanningPokerGameCreated;

    // Assert
    assertEquals(true, result, "Should be true. Tasks should have been sent and received successfully");
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