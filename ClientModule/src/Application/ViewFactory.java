package Application;

import Views.LoginView.LoginViewController;
import Views.MainView.MainViewController;
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


    // For closing loginView upon login
    public void closeLoginView() {
        loginViewStage.close();
    }

}
