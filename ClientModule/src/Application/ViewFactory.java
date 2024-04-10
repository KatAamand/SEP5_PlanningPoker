package Application;

import Views.LobbyView.LobbyViewController;
import Views.LoginView.LoginViewController;
import Views.MainView.MainViewController;
import Views.SessionView.SessionViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ViewFactory {
    private static ViewFactory instance;
    private final static Lock lock = new ReentrantLock();
    private ViewModelFactory viewModelFactory;

    // Controller variables
    private LoginViewController loginViewController; 
    private MainViewController mainViewController;
    private SessionViewController sessionViewController;
    private LobbyViewController lobbyViewController;
    private TaskViewController taskViewController;
    private ChatViewController chatViewController;

    private final Stage loginViewStage;
    private FXMLLoader fxmlLoader;

    private ViewFactory(ViewModelFactory viewModelFactory, Stage stage) {
        this.viewModelFactory = viewModelFactory;
        this.loginViewStage = stage;
    }

    public static ViewFactory getInstance(ViewModelFactory viewModelFactory, Stage stage) {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new ViewFactory(viewModelFactory, stage);
                }
            }
        }

        return instance;
    }

    // Load LoginView as first view
    public LoginViewController loadLoginView() throws IOException {
        if (loginViewController == null) {
            fxmlLoader = new FXMLLoader(getClass().getResource("../Views/LoginView/LoginView.fxml"));
            fxmlLoader.setControllerFactory(controllerClass -> {
                try {
                    return new LoginViewController(viewModelFactory.getLoginViewModel());
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            });

            Scene loginViewScene = new Scene(fxmlLoader.load());
            loginViewStage.setTitle("Login");
            loginViewStage.setScene(loginViewScene);
            loginViewStage.show();
            loginViewController = fxmlLoader.getController();
        }
        return loginViewController;
    }

    // Load MainView
    public MainViewController loadMainView() throws IOException {
        if (mainViewController == null) {
            fxmlLoader = new FXMLLoader(getClass().getResource("../Views/MainView/MainView.fxml"));
            fxmlLoader.setControllerFactory(controllerClass -> {
                try {
                    return new MainViewController(viewModelFactory.getMainViewModel());
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            });

            Scene mainViewScene = new Scene(fxmlLoader.load());
            Stage mainViewStage = new Stage();
            mainViewStage.setTitle("Main");
            mainViewStage.setScene(mainViewScene);
            mainViewStage.show();
            mainViewController = fxmlLoader.getController();
        }

        return mainViewController;
    }

    // Load SessionView
    public SessionViewController loadSessionView() throws IOException {
        if (sessionViewController == null) {
            fxmlLoader = new FXMLLoader(getClass().getResource("../Views/SessionsView/SessionView.fxml"));
            fxmlLoader.setControllerFactory(controllerClass -> {
                try {
                    return new SessionViewController(viewModelFactory.getMainViewModel());
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            });

            Scene sessionViewScene = new Scene(fxmlLoader.load());
            Stage sessionViewStage = new Stage();
            sessionViewStage.setTitle("Session");
            sessionViewStage.setScene(sessionViewScene);
            sessionViewStage.show();
            sessionViewController = fxmlLoader.getController();
        }

        return sessionViewController;
    }

    // Load LobbyView
    public LobbyViewController loadLobbyView() throws IOException {
        if (lobbyViewController == null) {
            fxmlLoader = new FXMLLoader(getClass().getResource("../Views/LobbyView/LobbyView.fxml"));
            fxmlLoader.setControllerFactory(controllerClass -> {
                try {
                    return new LobbyViewController(viewModelFactory.getMainViewModel());
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            });

            Scene lobbyViewScene = new Scene(fxmlLoader.load());
            Stage lobbyViewStage = new Stage();
            lobbyViewStage.setTitle("Lobby");
            lobbyViewStage.setScene(lobbyViewScene);
            lobbyViewStage.show();
            lobbyViewController = fxmlLoader.getController();
        }

        return lobbyViewController;
    }

    // Load TaskView
    public TaskViewController loadTaskView() throws IOException {
        if (taskViewController == null) {
            fxmlLoader = new FXMLLoader(getClass().getResource("../Views/LobbyView/LobbyView.fxml"));
            fxmlLoader.setControllerFactory(controllerClass -> {
                try {
                    return new TaskViewController(viewModelFactory.getMainViewModel());
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            });

            Scene taskViewScene = new Scene(fxmlLoader.load());
            Stage taskViewStage = new Stage();
            taskViewStage.setTitle("Tasks");
            taskViewStage.setScene(taskViewScene);
            taskViewStage.show();
            taskViewController = fxmlLoader.getController();
        }

        return taskViewController;
    }

    // Load LobbyView
    public ChatViewController loadChatView() throws IOException {
        if (chatViewController == null) {
            fxmlLoader = new FXMLLoader(getClass().getResource("../Views/LobbyView/LobbyView.fxml"));
            fxmlLoader.setControllerFactory(controllerClass -> {
                try {
                    return new ChatViewController(viewModelFactory.getMainViewModel());
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            });

            Scene chatViewScene = new Scene(fxmlLoader.load());
            Stage chatViewStage = new Stage();
            chatViewStage.setTitle("Chat");
            chatViewStage.setScene(chatViewScene);
            chatViewStage.show();
            chatViewController = fxmlLoader.getController();
        }

        return chatViewController;
    }


    // For closing loginView upon login
    public void closeLoginView() {
        loginViewStage.close();
    }

}
