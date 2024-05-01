package Views.LoginView;

import Application.ClientFactory;
import Application.Session;
import Application.ViewFactory;
import Networking.ClientConnection_RMI;
import Networking.ServerConnection_RMI;
import Networking.Server_RMI;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class LoginViewControllerTestWithServerConnection
{
  private static LoginViewController loginViewController;
  private static ClientConnection_RMI client;
  private static ServerConnection_RMI server;
  private boolean runLaterExecuted = false;
  private static final int maxWaitingTicks = 1000;
  private static Thread serverThread;
  private static Thread clientThread;
  private static Thread javaFxPlatformThread;

  @BeforeAll public static void initServer()
  {
    AtomicBoolean serverInitialized = new AtomicBoolean(false);
    AtomicBoolean clientInitialized = new AtomicBoolean(false);

    //Initializes a local server, that can be tested against:
    serverThread = new Thread(() -> {
      try
      {
        server = new Server_RMI();
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
    }


    //Initializes the javaFx component library, and starts the server which the client uses to connect with.
    clientThread = new Thread (() -> {
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
      });
    clientThread.start();

    // Wait for server to be initialized in a separate thread (Simulate the Client Connection running on a separate thread):
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

  @BeforeEach void setUp()
  {
    //Does nothing at the moment
  }

  @AfterEach void tearDown()
  {
    loginViewController = null;
    runLaterExecuted = false;
  }

  @Test public void testClientDoesNotThrowRunTimeExceptionWhenServerConnectionIsEstablished()
  {
    assertDoesNotThrow(() -> ClientFactory.getInstance().getClient());
  }

  @Test public void testCreateUserMainSunnyScenario()
  {
    // Arrange test parameters:
    String username = "testBruger1";
    String password = "qwerty123";
    InnerTestClassListener testListener = new InnerTestClassListener();

    // Act
    Platform.runLater(() -> {
      try
      {
        // Assign a testListener to catch events the server fires:
        client.addPropertyChangeListener(testListener);

        // 1. Check that the login view is properly loaded / Start the application:
        loginViewController = ViewFactory.getInstance().loadLoginView();
        Stage activeStage = (Stage) loginViewController.createUserButton.getScene().getWindow();
        activeStage.hide();

        // 2. Enter a username "testBruger1":
        loginViewController.usernameTextField.setText(username);

        // 3. Enter a password "qwerty123":
        loginViewController.passwordTextField.setText(password);

        // 4. Click "Opret Bruger":
        loginViewController.createUserButton.fire();
        runLaterExecuted = true;
      } catch (IOException e)
      {
        throw new RuntimeException(e);
      }
    });

    // Wait for runLater method to be executed:
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

    // 5. Check if the user is created on the server, by reading the servers' response:
    boolean wasUserCreated = testListener.getPropertyName().equals("userCreatedSuccess");

    // 6. Check if the user exists in the server data, by attempting to log in with the created user:
    try {
      server.validateUser(username, password, client);
    } catch (RemoteException e) {
      //Do nothing
    }
    boolean doesUserExistOnServer = testListener.getPropertyName().equals("userLoginSuccess");

    // 7. Check partial results and combine to total result:
    boolean testResult = wasUserCreated && doesUserExistOnServer;

    //Assert if test was successful or not:
    assertEquals(true, testResult, "Expected to be able to create a user named [" + username + "] with password [" + password + "]");
  }


  @Test public void testCreateUserWithNoNameSpecified() {
    // Arrange test parameters:
    String password = "qwerty123";
    InnerTestClassListener testListener = new InnerTestClassListener();

    // Act:
    Platform.runLater(() -> {
      try
      {
        // Assign a testListener to catch events the server fires:
        client.addPropertyChangeListener(testListener);

        // 1. Check that the login view is properly loaded / Start the application:
        loginViewController = ViewFactory.getInstance().loadLoginView();
        Stage activeStage = (Stage) loginViewController.createUserButton.getScene().getWindow();
        activeStage.hide();

        // 2. Do not enter any userName:

        // 3. Enter a password "qwerty123":
        loginViewController.passwordTextField.setText(password);

        // 4. Click "Opret Bruger":
        loginViewController.createUserButton.fire();
        runLaterExecuted = true;
      } catch (IOException e)
      {
        throw new RuntimeException(e);
      }
    });

    // Wait for runLater method to be executed:
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

    // 5. Check if the user is created on the server, by reading the servers' response:
    boolean wasUserCreated = testListener.getPropertyName().equals("userCreatedSuccess");

    // 6. Check if the user exists in the server data, by attempting to log in with the created user:
    try {
      server.validateUser(null, password, client);
    } catch (RemoteException e) {
      //Do nothing
    }
    boolean doesUserExistOnServer = testListener.getPropertyName().equals("userLoginSuccess");

    // 7. Check partial results and combine to total result:
    boolean testResult = wasUserCreated && doesUserExistOnServer;

    // Assert if test was successful or not:
    assertEquals(false, testResult, "Expected to NOT be able to create a user named [" + null + "] with password [" + password + "]");
  }


  @Test public void testCreateUserWithNoPasswordSpecified() {
    // Arrange test parameters:
    String username = "testBruger1";
    InnerTestClassListener testListener = new InnerTestClassListener();

    // Act
    Platform.runLater(() -> {
      try
      {
        // Assign a testListener to catch events the server fires:
        client.addPropertyChangeListener(testListener);

        // 1. Check that the login view is properly loaded / Start the application:
        loginViewController = ViewFactory.getInstance().loadLoginView();
        Stage activeStage = (Stage) loginViewController.createUserButton.getScene().getWindow();
        activeStage.hide();

        // 2. Enter a username "testBruger1":
        loginViewController.usernameTextField.setText(username);

        // 3. Do not enter any password:

        // 4. Click "Opret Bruger":
        loginViewController.createUserButton.fire();
        runLaterExecuted = true;
      } catch (IOException e)
      {
        throw new RuntimeException(e);
      }
    });

    // Wait for runLater method to be executed.
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

    // 5. Check if the user is created on the server, by reading the servers' response:
    boolean wasUserCreated = testListener.getPropertyName().equals("userCreatedSuccess");

    // 6. Check if the user exists in the server data, by attempting to log in with the created user:
    try {
      server.validateUser(username, null, client);
    } catch (RemoteException e) {
      //Do nothing
    }
    boolean doesUserExistOnServer = testListener.getPropertyName().equals("userLoginSuccess");

    // 7. Check partial results and combine to total result:
    boolean testResult = wasUserCreated && doesUserExistOnServer;

    // Assert if test was successful or not:
    assertEquals(false, testResult, "Expected to NOT be able to create a user named [" + username + "] with password [" + null + "]");
  }


  @Test public void testCreateUserWithBlankNameSpecified() {
    // Arrange test parameters:
    String username = "     ";
    String password = "qwerty123";
    InnerTestClassListener testListener = new InnerTestClassListener();

    // Act
    Platform.runLater(() -> {
      try
      {
        // Assign a testListener to catch events the server fires:
        client.addPropertyChangeListener(testListener);

        // 1. Check that the login view is properly loaded / Start the application:
        loginViewController = ViewFactory.getInstance().loadLoginView();
        Stage activeStage = (Stage) loginViewController.createUserButton.getScene().getWindow();
        activeStage.hide();

        // 2. Enter a username "     ":
        loginViewController.usernameTextField.setText(username);

        // 3. Enter a password "qwerty123":
        loginViewController.passwordTextField.setText(password);

        // 4. Click "Opret Bruger":
        loginViewController.createUserButton.fire();
        runLaterExecuted = true;
      } catch (IOException e)
      {
        throw new RuntimeException(e);
      }
    });

    // Wait for runLater method to be executed:
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

    // 5. Check if the user is created on the server, by reading the servers' response:
    boolean wasUserCreated = testListener.getPropertyName().equals("userCreatedSuccess");

    // 6. Check if the user exists in the server data, by attempting to log in with the created user:
    try {
      server.validateUser(username, password, client);
    } catch (RemoteException e) {
      //Do nothing
    }
    boolean doesUserExistOnServer = testListener.getPropertyName().equals("userLoginSuccess");

    // 7. Check partial results and combine to total result:
    boolean testResult = wasUserCreated && doesUserExistOnServer;

    // Assert if test was successful or not:
    assertEquals(false, testResult, "Expected to NOT be able to create a user named [" + username + "] with password [" + password + "]");
  }


  @Test public void testCreateUserWithBlankPasswordSpecified() {
    // Arrange test parameters:
    String username = "testBruger1";
    String password = "     ";
    InnerTestClassListener testListener = new InnerTestClassListener();

    // Act
    Platform.runLater(() -> {
      try
      {
        // Assign a testListener to catch events the server fires:
        client.addPropertyChangeListener(testListener);

        // 1. Check that the login view is properly loaded / Start the application:
        loginViewController = ViewFactory.getInstance().loadLoginView();
        Stage activeStage = (Stage) loginViewController.createUserButton.getScene().getWindow();
        activeStage.hide();

        // 2. Enter a username "testBruger1":
        loginViewController.usernameTextField.setText(username);

        // 3. Enter a password "     ":
        loginViewController.passwordTextField.setText(password);

        // 4. Click "Opret Bruger":
        loginViewController.createUserButton.fire();
        runLaterExecuted = true;
      } catch (IOException e)
      {
        throw new RuntimeException(e);
      }
    });

    // Wait for runLater method to be executed:
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

    // 5. Check if the user is created on the server, by reading the servers' response:
    boolean wasUserCreated = testListener.getPropertyName().equals("userCreatedSuccess");

    // 6. Check if the user exists in the server data, by attempting to log in with the created user:
    try {
      server.validateUser(username, password, client);
    } catch (RemoteException e) {
      //Do nothing
    }
    boolean doesUserExistOnServer = testListener.getPropertyName().equals("userLoginSuccess");

    // 7. Check partial results and combine to total result:
    boolean testResult = wasUserCreated && doesUserExistOnServer;

    //Assert if test was successful or not:
    assertEquals(false, testResult, "Expected to NOT be able to create a user named [" + username + "] with password [" + password + "]");
  }


  @Test public void testLoginMainSunnyScenario()
  {
    // Arrange test parameters:
    String username = "test";
    String password = "123";
    InnerTestClassListener testListener = new InnerTestClassListener();

    // Act
    Platform.runLater(() -> {
      try
      {
        // Assign a testListener to catch events the server fires:
        client.addPropertyChangeListener(testListener);

        // 1. Check that the login view is properly loaded / Start the application:
        loginViewController = ViewFactory.getInstance().loadLoginView();
        Stage activeStage = (Stage) loginViewController.createUserButton.getScene().getWindow();
        activeStage.hide();

        // 2. Enter a username "testBruger1":
        loginViewController.usernameTextField.setText(username);

        // 3. Enter a password "qwerty123":
        loginViewController.passwordTextField.setText(password);

        // 4. Click "Login":
        loginViewController.loginButton.fire();
        runLaterExecuted = true;
      } catch (IOException e)
      {
        throw new RuntimeException(e);
      }
    });

    // Wait for runLater method to be executed:
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

    // 5. Check if the user was logged in, by reading the servers' response:
    boolean wasUserCreated = testListener.getPropertyName().equals("userLoginSuccess");

    // 6. Check if the logged-in user is assigned to the local Session object:
    try {
      Thread.sleep(200);
    } catch (InterruptedException e) {
      //Sleep for 200ms to allow the application time to set the userName in the ViewModel.
    }
    boolean isCurrentUserSet = Session.getCurrentUser().getUsername().equals(username);

    // 7. Check partial results and combine to total result:
    boolean testResult = wasUserCreated && isCurrentUserSet;

    //Assert if test was successful or not:
    assertEquals(true, testResult, "Expected to be able to Login with user named [" + username + "] with password [" + password + "]");
  }


  @Test public void testLoginWithFreshlyCreatedUser()
  {

    // Arrange test parameters:
    String username = "testBruger1";
    String password = "qwerty123";
    InnerTestClassListener testListener = new InnerTestClassListener();

    // Act
    Platform.runLater(() -> {
      try
      {
        // Assign a testListener to catch events the server fires:
        client.addPropertyChangeListener(testListener);

        // 1. Check that the login view is properly loaded / Start the application:
        loginViewController = ViewFactory.getInstance().loadLoginView();
        Stage activeStage = (Stage) loginViewController.createUserButton.getScene().getWindow();
        activeStage.hide();

        // 2. Enter a username "testBruger1":
        loginViewController.usernameTextField.setText(username);

        // 3. Enter a password "qwerty123":
        loginViewController.passwordTextField.setText(password);

        // 4. Click "Opret Bruger":
        loginViewController.createUserButton.fire();
        runLaterExecuted = true;
      } catch (IOException e)
      {
        throw new RuntimeException(e);
      }
    });

    // Wait for runLater method to be executed:
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

    // 5. Check if the user is created on the server, by reading the servers' response:
    boolean wasUserCreated = testListener.getPropertyName().equals("userCreatedSuccess");


    // 6. Attempt to log in with the freshly created user:
    runLaterExecuted = false;
    Platform.runLater(() -> {
        // 1. Enter a username "testBruger1":
        loginViewController.usernameTextField.setText(username);

        // 2. Enter a password "qwerty123":
        loginViewController.passwordTextField.setText(password);

        // 3. Click "Login":
        loginViewController.loginButton.fire();
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

    // 7. Check if user was logged-in successfully:
    boolean wasUserLoggedIn = testListener.getPropertyName().equals("userLoginSuccess");


    // 8. Check if the logged-in user is assigned to the local Session object:
    try {
      Thread.sleep(200);
    } catch (InterruptedException e) {
      //Sleep for 200ms to allow the application time to set the userName in the ViewModel.
    }
    boolean isCurrentUserSet = Session.getCurrentUser().getUsername().equals(username);


    // 9. Check partial results and combine to total result:
    boolean testResult = wasUserCreated && wasUserLoggedIn && isCurrentUserSet;

    // Assert if test was successful or not:
    assertEquals(true, testResult, "Expected to be able log in with a newly created user named [" + username + "] with password [" + password + "]");
  }


  @Test public void testLoginWithNonExistantUsernameSpecified()
  {
    // Arrange test parameters:
    String username = "test27_qwerty112";
    String password = "123";
    InnerTestClassListener testListener = new InnerTestClassListener();

    // Act
    Platform.runLater(() -> {
      try
      {
        // Assign a testListener to catch events the server fires:
        client.addPropertyChangeListener(testListener);

        // 1. Check that the login view is properly loaded / Start the application:
        loginViewController = ViewFactory.getInstance().loadLoginView();
        Stage activeStage = (Stage) loginViewController.createUserButton.getScene().getWindow();
        activeStage.hide();

        // 2. Enter a username "test27_qwerty112":
        loginViewController.usernameTextField.setText(username);

        // 3. Enter a password "qwerty123":
        loginViewController.passwordTextField.setText(password);

        // 4. Click "Login":
        loginViewController.loginButton.fire();
        runLaterExecuted = true;
      } catch (IOException e)
      {
        throw new RuntimeException(e);
      }
    });

    // Wait for runLater method to be executed:
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

    // 5. Check if the user was logged in, by reading the servers' response:
    boolean wasUserLoggedIn = testListener.getPropertyName().equals("userLoginSuccess");

    //Assert if test was successful or not:
    assertEquals(false, wasUserLoggedIn, "Expected to NOT be able to Login with non-existent user named [" + username + "] with password [" + password + "]");
  }


  @Test public void testLoginWithExistingUsernameButInvalidPasswordSpecified()
  {
    // Arrange test parameters:
    String username = "test";
    String password = "qwerty123";
    InnerTestClassListener testListener = new InnerTestClassListener();

    // Act
    Platform.runLater(() -> {
      try
      {
        // Assign a testListener to catch events the server fires:
        client.addPropertyChangeListener(testListener);

        // 1. Check that the login view is properly loaded / Start the application:
        loginViewController = ViewFactory.getInstance().loadLoginView();
        Stage activeStage = (Stage) loginViewController.createUserButton.getScene().getWindow();
        activeStage.hide();

        // 2. Enter a username:
        loginViewController.usernameTextField.setText(username);

        // 3. Enter a password:
        loginViewController.passwordTextField.setText(password);

        // 4. Click "Login":
        loginViewController.loginButton.fire();
        runLaterExecuted = true;
      } catch (IOException e)
      {
        throw new RuntimeException(e);
      }
    });

    // Wait for runLater method to be executed:
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

    // 5. Check if the user was logged in, by reading the servers' response:
    boolean wasUserLoggedIn = testListener.getPropertyName().equals("userLoginSuccess");

    //Assert if test was successful or not:
    assertEquals(false, wasUserLoggedIn, "Expected to NOT be able to Login with existing user named [" + username + "] with invalid password [" + password + "]");
  }


  @Test public void testLoginWithNoUsernameSpecified()
  {
    // Arrange test parameters:
    String password = "qwerty123";
    InnerTestClassListener testListener = new InnerTestClassListener();

    // Act
    Platform.runLater(() -> {
      try
      {
        // Assign a testListener to catch events the server fires:
        client.addPropertyChangeListener(testListener);

        // 1. Check that the login view is properly loaded / Start the application:
        loginViewController = ViewFactory.getInstance().loadLoginView();
        Stage activeStage = (Stage) loginViewController.createUserButton.getScene().getWindow();
        activeStage.hide();

        // 2. Do not enter any username:

        // 3. Enter a password:
        loginViewController.passwordTextField.setText(password);

        // 4. Click "Login":
        loginViewController.loginButton.fire();
        runLaterExecuted = true;
      } catch (IOException e)
      {
        throw new RuntimeException(e);
      }
    });

    // Wait for runLater method to be executed:
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

    // 5. Check if the user was logged in, by reading the servers' response:
    boolean wasUserLoggedIn = testListener.getPropertyName().equals("userLoginSuccess");

    //Assert if test was successful or not:
    assertEquals(false, wasUserLoggedIn, "Expected to NOT be able to Login when no username is specified [" + null + "] with invalid password [" + password + "]");
  }


  @Test public void testLoginWithExistingUsernameButNoPasswordSpecified()
  {
    // Arrange test parameters:
    String username = "test";
    InnerTestClassListener testListener = new InnerTestClassListener();

    // Act
    Platform.runLater(() -> {
      try
      {
        // Assign a testListener to catch events the server fires:
        client.addPropertyChangeListener(testListener);

        // 1. Check that the login view is properly loaded / Start the application:
        loginViewController = ViewFactory.getInstance().loadLoginView();
        Stage activeStage = (Stage) loginViewController.createUserButton.getScene().getWindow();
        activeStage.hide();

        // 2. Enter a username:
        loginViewController.usernameTextField.setText(username);

        // 3.Do not enter any password

        // 4. Click "Login":
        loginViewController.loginButton.fire();
        runLaterExecuted = true;
      } catch (IOException e)
      {
        throw new RuntimeException(e);
      }
    });

    // Wait for runLater method to be executed:
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

    // 5. Check if the user was logged in, by reading the servers' response:
    boolean wasUserLoggedIn = testListener.getPropertyName().equals("userLoginSuccess");

    //Assert if test was successful or not:
    assertEquals(false, wasUserLoggedIn, "Expected to NOT be able to Login with existing user named [" + username + "] with no password specified [" + null + "]");
  }


  @Test public void testLoginWithBlankUsernameSpecified()
  {
    // Arrange test parameters:
    String username = "     ";
    String password = "123";
    InnerTestClassListener testListener = new InnerTestClassListener();

    // Act
    Platform.runLater(() -> {
      try
      {
        // Assign a testListener to catch events the server fires:
        client.addPropertyChangeListener(testListener);

        // 1. Check that the login view is properly loaded / Start the application:
        loginViewController = ViewFactory.getInstance().loadLoginView();
        Stage activeStage = (Stage) loginViewController.createUserButton.getScene().getWindow();
        activeStage.hide();

        // 2. Enter a username:
        loginViewController.usernameTextField.setText(username);

        // 3. Enter a password:
        loginViewController.passwordTextField.setText(password);

        // 4. Click "Login":
        loginViewController.loginButton.fire();
        runLaterExecuted = true;
      } catch (IOException e)
      {
        throw new RuntimeException(e);
      }
    });

    // Wait for runLater method to be executed:
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

    // 5. Check if the user was logged in, by reading the servers' response:
    boolean wasUserLoggedIn = testListener.getPropertyName().equals("userLoginSuccess");

    //Assert if test was successful or not:
    assertEquals(false, wasUserLoggedIn, "Expected to NOT be able to Login when a blank user name is specified [" + username + "] with password [" + password + "]");
  }


  @Test public void testLoginWithValidUsernameAndBlankPasswordSpecified()
  {
    // Arrange test parameters:
    String username = "test";
    String password = "     ";
    InnerTestClassListener testListener = new InnerTestClassListener();

    // Act
    Platform.runLater(() -> {
      try
      {
        // Assign a testListener to catch events the server fires:
        client.addPropertyChangeListener(testListener);

        // 1. Check that the login view is properly loaded / Start the application:
        loginViewController = ViewFactory.getInstance().loadLoginView();
        Stage activeStage = (Stage) loginViewController.createUserButton.getScene().getWindow();
        activeStage.hide();

        // 2. Enter a username:
        loginViewController.usernameTextField.setText(username);

        // 3. Enter a password:
        loginViewController.passwordTextField.setText(password);

        // 4. Click "Login":
        loginViewController.loginButton.fire();
        runLaterExecuted = true;
      } catch (IOException e)
      {
        throw new RuntimeException(e);
      }
    });

    // Wait for runLater method to be executed:
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

    // 5. Check if the user was logged in, by reading the servers' response:
    boolean wasUserLoggedIn = testListener.getPropertyName().equals("userLoginSuccess");

    //Assert if test was successful or not:
    assertEquals(false, wasUserLoggedIn, "Expected to NOT be able to Login when a valid user name is specified [" + username + "] with a blank password [" + password + "]");
  }


  /* This test is not working as intended. Commented out for now.
  @Test public void testLoginViewCanBeTerminatedAfterEnteringNameAndPassword()
  {
    // Arrange test parameters:
    String username = "test";
    String password = "123";
    InnerTestClassListener testListener = new InnerTestClassListener();

    // Act
    Platform.runLater(() -> {
      try
      {
        // Assign a testListener to catch events the server fires:
        client.addPropertyChangeListener(testListener);

        // 1. Check that the login view is properly loaded / Start the application:
        loginViewController = ViewFactory.getInstance().loadLoginView();
        Stage activeStage = (Stage) loginViewController.createUserButton.getScene().getWindow();
        activeStage.hide();

        // 2. Enter a username:
        loginViewController.usernameTextField.setText(username);

        // 3. Enter a password:
        loginViewController.passwordTextField.setText(password);

        // 4. Simulate user closing the shown stage/window/scene:
        //activeStage.close();
        runLaterExecuted = true;
      } catch (IOException e)
      {
        throw new RuntimeException(e);
      }
    });

    // Wait for runLater method to be executed:
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

    try {
      if(!runLaterExecuted) {
        Thread.sleep(2000);
      }
    } catch (InterruptedException e) {
      // continue
    }

    // 5. Check if both the main JavaFx platform thread and the Client thread have been terminated:
    boolean areAllThreadsTerminated = !clientThread.isAlive();

    //Assert if test was successful or not:
    assertEquals(true, clientThread.isAlive(), "Expected application to fully close its main thread [isAlive = " + (clientThread.isAlive()) + "]");
  }*/






  /** This inner class is used to handle events that are fired from the test server, in order to evaluate if operations from the client towards the server succeeded or not. */
  private static class InnerTestClassListener implements PropertyChangeListener {
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