package Views.PlanningPokerView;

import Application.ClientFactory;
import Application.ViewFactory;
import Application.ViewModelFactory;
import Networking.ClientConnection_RMI;
import Networking.ServerConnection_RMI;
import Networking.Server_RMI;
import Views.ForceSynchronizationOfScenarioTestClasses;
import Views.GameView.GameViewModel;
import Views.LobbyView.LobbyViewController;
import Views.LoginView.LoginViewController;
import Views.MainView.MainViewController;
import Views.TaskView.SingleTaskViewModel;
import Views.TaskView.TaskViewModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.junit.jupiter.api.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class CreateSessionTestWithServerConnection
{
  private static PlanningPokerViewController planningPokerViewController;
  private static MainViewController mainViewController;
  private static LoginViewController loginViewController;
  private static TaskViewModel taskViewModel;
  private static GameViewModel gameViewModel;
  private static LobbyViewController lobbyViewController;
  private static ClientConnection_RMI client;
  private static Registry registry;
  private static ServerConnection_RMI server;
  private boolean runLaterExecuted = false;
  private boolean testUserLoggedIn = false;
  private static final int maxWaitingTicks = 1000;
  private static Thread serverThread;
  private static Thread clientThread;
  InnerTestClassListener testListener;


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
        server = new Server_RMI();
        registry = LocateRegistry.createRegistry(1099);
        registry.bind("Model", server);
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
          try
          {
            client = ClientFactory.getInstance().getClient();
            clientInitialized.set(true);
          }
          catch (RemoteException e)
          {
            throw new RuntimeException(e);
          }
        });
      } catch (IllegalStateException e) {
        Platform.runLater(() -> {
          try
          {
            client = ClientFactory.getInstance().getClient();
            clientInitialized.set(true);
          }
          catch (RemoteException ee)
          {
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
    System.out.println("reached 3");

  }


  @AfterAll public static void shutDownJavaFxThreadAndServer()
  {
    //Terminate server:
    try {
      server.unRegisterClient(client);
      registry.unbind("Model");
    } catch (RemoteException | NotBoundException e) {
      throw new RuntimeException(e);
    }

    try {
      UnicastRemoteObject.unexportObject(registry, true);
    } catch (NoSuchObjectException e) {
      throw new RuntimeException(e);
    }

    // Release the centralized lock in order to allow other UI related test classes to execute their scenario tests:
    ForceSynchronizationOfScenarioTestClasses.getSynchronizationLock().unlock();
    System.out.println("Finished CreateSessionTestWithServerConnection");
  }



  @BeforeEach void setUp()
  {
    // Arrange test parameters:
    String username = "test";
    String password = "123";
    testListener = new InnerTestClassListener();

    // Login to the system with the test user account
    Platform.runLater(() -> {
      try
      {
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
      client.removePropertyChangeListener(testListener);
    } catch (RemoteException e) {
      //Do nothing
    }
  }


  @Test public void createANewPlanningPokerSession() {
    Platform.runLater(() -> {
      try {
        // 1. Simulate user pressing the 'Create Planning Poker' button:
        mainViewController.createPlanningPokerButton.fire();

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

    // Check that the server has created a new planning poker session by catching the server response:
    boolean wasPlanningPokerGameCreated = testListener.getPropertyName().equals("planningPokerCreatedSuccess");


    // Assign the TaskViewModel and GameViewModel:
    Platform.runLater(() -> {
      try {
        taskViewModel = ViewModelFactory.getInstance().getTaskViewModel();
        lobbyViewController = planningPokerViewController.getLobbyViewController();
        gameViewModel = ViewModelFactory.getInstance().getGameViewModel();
        runLaterExecuted = true;
      } catch (IOException e)
      {
        throw new RuntimeException(e);
      }
    });

    // Wait for runLater method to be executed:
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

    // 2. Simulate the user creating a task:
    String header = "Test1";
    String descript = "Testing1";

    // Check that sessionId and userId were set in the task view:
    boolean sessionIdSetInTaskView = !taskViewModel.sessionIdProperty().getValue().equals("UNDEFINED");
    boolean userIdSetInTaskView = !taskViewModel.labelUserIdProperty().getValue().equals("UNDEFINED");

    // Create a task:
    lobbyViewController.getEmbeddedAddTaskViewController().textFieldTaskHeader.setText(header);
    lobbyViewController.getEmbeddedAddTaskViewController().textAreaTaskDescription.setText(descript);
    lobbyViewController.getEmbeddedAddTaskViewController().onSavePressed(new ActionEvent());

    // Give application and server some time to handle the creation request:
    try {
      Thread.sleep(250);
    } catch (InterruptedException e) {
      //Do nothing.
    }

    // Check if the added task is displayed in the taskView
    ArrayList<SingleTaskViewModel> currentTasks = taskViewModel.getSingleTaskViewModelList();
    boolean taskWasAddedAndDisplayed = false;

    for (SingleTaskViewModel model : currentTasks) {
      if(model.getTaskHeaderLabelProperty().getValue().contains(header) && model.getTaskDescProperty().getValue().equals(descript)) {
        taskWasAddedAndDisplayed = true;
      }
    }


    // 3. Simulate user starting the session after a task was added:
    lobbyViewController.onStartGameButtonPressed();

    // Give application and server some time to handle the start game:
    try {
      Thread.sleep(250);
    } catch (InterruptedException e) {
      //Do nothing.
    }

    // Check if the game was started by checking that the GameViewModel is displaying the added task:
    boolean startedGameAndShowingProperTask = gameViewModel.taskDescPropertyProperty().getValue().equals(descript) && gameViewModel.taskHeaderPropertyProperty().getValue().equals(header);


    // Evaluate if any of the checks during this run failed:
    boolean properlyExecuted = sessionIdSetInTaskView && userIdSetInTaskView && wasPlanningPokerGameCreated && taskWasAddedAndDisplayed && startedGameAndShowingProperTask;

    // Assert
    assertEquals(true, properlyExecuted, "Should be true. A new planning poker game was not properly created and started with min. 1 task.");
  }


  @Test public void localClientIsRevertedBackToMainScreenWhenPlanningPokerGameIsClosedBySameLocalClient() {
    // Create a Planning Poker Game with User
    Platform.runLater(() -> {
      try {
        // Simulate user pressing the 'Create Planning Poker' button:
        mainViewController.createPlanningPokerButton.fire();

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

    // Check that the server has created a new planning poker session by catching the server response:
    boolean wasPlanningPokerGameCreated = testListener.getPropertyName().equals("planningPokerCreatedSuccess");


    // Assign the TaskViewModel and GameViewModel:
    Platform.runLater(() -> {
      try {
        taskViewModel = ViewModelFactory.getInstance().getTaskViewModel();
        lobbyViewController = planningPokerViewController.getLobbyViewController();
        gameViewModel = ViewModelFactory.getInstance().getGameViewModel();
        runLaterExecuted = true;
      } catch (IOException e)
      {
        throw new RuntimeException(e);
      }
    });

    // Wait for runLater method to be executed:
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


    // 1. Simulate the user creating a task:
    String header = "Test1";
    String descript = "Testing1";

    // Create a task:
    lobbyViewController.getEmbeddedAddTaskViewController().textFieldTaskHeader.setText(header);
    lobbyViewController.getEmbeddedAddTaskViewController().textAreaTaskDescription.setText(descript);
    lobbyViewController.getEmbeddedAddTaskViewController().onSavePressed(new ActionEvent());

    // Give application and server some time to handle the creation request:
    try {
      Thread.sleep(250);
    } catch (InterruptedException e) {
      //Do nothing.
    }

    // Check if the added task is displayed in the taskView
    ArrayList<SingleTaskViewModel> currentTasks = taskViewModel.getSingleTaskViewModelList();
    boolean taskWasAddedAndDisplayed = false;

    for (SingleTaskViewModel model : currentTasks) {
      if(model.getTaskHeaderLabelProperty().getValue().contains(header) && model.getTaskDescProperty().getValue().equals(descript)) {
        taskWasAddedAndDisplayed = true;
      }
    }

    // 2. Simulate user closing the currently shown stage by either Alt+F4 or other means (clicking the exit icon in the window)
    Platform.runLater(() -> {
      Window windowToClose = planningPokerViewController.getChatViewController().chatTextArea.getScene().getWindow();
      Event closeRequestEvent = new WindowEvent(windowToClose, WindowEvent.WINDOW_CLOSE_REQUEST);
      windowToClose.fireEvent(closeRequestEvent);
      runLaterExecuted = true;
    });

    // Wait for runLater method to be executed:
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


    //Check if the currently showing window is the MainView
    boolean isMainViewShowing = ViewFactory.getInstance().getMainViewStage().isShowing();

    //Evaluate all the essential booleans:
    boolean testResult = taskWasAddedAndDisplayed && isMainViewShowing && wasPlanningPokerGameCreated;


    //Assert
    assertEquals(true, testResult, "Expected that the MainView should be the active window after closing the planning poker view.");
  }

  @Test public void localClientIsRevertedBackToMainScreenWhenPlanningPokerGameIsClosedByANetworkConnectedClient() {
    assertEquals(true, false, "Must be tested manually!! Please review Test-Cases for Use Case #2 for details. "
        + "(ALT0, Brugeren kan til enhver til afslutte en sessions-oprettelse, hvorefter at alle tilsluttede brugere sendes retur til “opret/tilslut” skærmen.)"
        + "\nAs of 01/05-2024 this functionality IS NOT implemented NOR functioning. This should be tested with several clients running in each their own JVM.");
  }

  @Test public void localUserDeletedATaskFromTheTaskList() {
    // Create a Planning Poker Game with User
    Platform.runLater(() -> {
      try {
        // Simulate user pressing the 'Create Planning Poker' button:
        mainViewController.createPlanningPokerButton.fire();

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

    // Check that the server has created a new planning poker session by catching the server response:
    boolean wasPlanningPokerGameCreated = testListener.getPropertyName().equals("planningPokerCreatedSuccess");


    // Assign the TaskViewModel and GameViewModel:
    Platform.runLater(() -> {
      try {
        taskViewModel = ViewModelFactory.getInstance().getTaskViewModel();
        lobbyViewController = planningPokerViewController.getLobbyViewController();
        gameViewModel = ViewModelFactory.getInstance().getGameViewModel();
        runLaterExecuted = true;
      } catch (IOException e)
      {
        throw new RuntimeException(e);
      }
    });

    // Wait for runLater method to be executed:
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


    // 1. Simulate the user creating a task:
    String header = "Test1";
    String descript = "Testing1";

    // Create a task:
    lobbyViewController.getEmbeddedAddTaskViewController().textFieldTaskHeader.setText(header);
    lobbyViewController.getEmbeddedAddTaskViewController().textAreaTaskDescription.setText(descript);
    lobbyViewController.getEmbeddedAddTaskViewController().onSavePressed(new ActionEvent());

    // Give application and server some time to handle the creation request:
    try {
      Thread.sleep(250);
    } catch (InterruptedException e) {
      //Do nothing.
    }

    // Check if the added task is displayed in the taskView
    ArrayList<SingleTaskViewModel> currentTasks = taskViewModel.getSingleTaskViewModelList();
    boolean taskWasAddedAndDisplayed = false;

    for (SingleTaskViewModel model : currentTasks) {
      if(model.getTaskHeaderLabelProperty().getValue().contains(header) && model.getTaskDescProperty().getValue().equals(descript)) {
        taskWasAddedAndDisplayed = true;
      }
    }

    // A planning poker game has been created and a single task has been added to the game.
    // TODO ______________________________________________
    //TODO ONCE IMPLEMENTED, INSERT DELETION SEQUENCE BELOW

    assertEquals(true, false, "User removal of tasks is currently not implemented! Awaiting implementation! Please review Test-Cases from Use Case #2 for details.");
  }


  @Test public void startingANewPlanningPokerSessionWithoutAddingAnyTasksIsNotPossible() {
    Platform.runLater(() -> {
      try {
        // Simulate user pressing the 'Create Planning Poker' button:
        mainViewController.createPlanningPokerButton.fire();

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

    // Check that the server has created a new planning poker session by catching the server response:
    boolean wasPlanningPokerGameCreated = testListener.getPropertyName().equals("planningPokerCreatedSuccess");


    // Assign the TaskViewModel and GameViewModel:
    Platform.runLater(() -> {
      try {
        taskViewModel = ViewModelFactory.getInstance().getTaskViewModel();
        lobbyViewController = planningPokerViewController.getLobbyViewController();
        gameViewModel = ViewModelFactory.getInstance().getGameViewModel();
        runLaterExecuted = true;
      } catch (IOException e)
      {
        throw new RuntimeException(e);
      }
    });

    // Wait for runLater method to be executed:
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


    // Check that sessionId and userId were set in the task view:
    boolean sessionIdSetInTaskView = !taskViewModel.sessionIdProperty().getValue().equals("UNDEFINED");
    boolean userIdSetInTaskView = !taskViewModel.labelUserIdProperty().getValue().equals("UNDEFINED");


    // 1. Simulate user starting the session without any added tasks:
    lobbyViewController.onStartGameButtonPressed();

    // Give application and server some time to handle the start game:
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      //Do nothing.
    }

    // Check if the game was started by checking if the lobbyView element is hidden or showing. Hidden means the game has started and GameView is showing instead:
    Node nestedNode = planningPokerViewController.getLobbyViewController().getEmbeddedAddTaskViewController().textAreaTaskDescription.getParent().getParent().getParent().getParent().getParent();
    boolean didGameRefuseToStart = nestedNode.isVisible();

    // Evaluate if any of the checks during this run failed:
    boolean properlyExecuted = sessionIdSetInTaskView && userIdSetInTaskView && wasPlanningPokerGameCreated && didGameRefuseToStart;

    // Assert
    assertEquals(true, properlyExecuted, "Should be true. A planning poker game should not be able to begin with less than 1 tasks added to the current game.");
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