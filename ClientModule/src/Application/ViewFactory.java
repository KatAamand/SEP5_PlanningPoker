package Application;

import Views.ChatView.ChatViewController;
import Views.LobbyView.LobbyViewController;
import Views.LoginView.LoginViewController;
import Views.MainView.MainViewController;
import Views.GameView.GameViewController;
import Views.PlanningPokerView.PlanningPokerViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
    private GameViewController gameViewController;
    private LobbyViewController lobbyViewController;
    private ChatViewController chatViewController;
    private PlanningPokerViewController planningPokerViewController;

    private Stage loginViewStage;
    private Stage mainViewStage;
    private FXMLLoader fxmlLoader;

    private ViewFactory() {
        this.viewModelFactory = ViewModelFactory.getInstance();
        this.loginViewStage = new Stage();
    }


    public static ViewFactory getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new ViewFactory();
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
                    return new LoginViewController(viewModelFactory.getLoginViewModel(), this);
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
                    return new MainViewController(viewModelFactory.getMainViewModel(), this);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            });

            Scene mainViewScene = new Scene(fxmlLoader.load());
            mainViewStage = new Stage();
            mainViewStage.setTitle("Main");
            mainViewStage.setScene(mainViewScene);
            mainViewStage.show();
            mainViewController = fxmlLoader.getController();
        }

        return mainViewController;
    }

    // Load SessionView
    public GameViewController loadGameViewController() throws IOException {
        if (gameViewController == null) {
            fxmlLoader = new FXMLLoader(getClass().getResource("../Views/GameView/GameView.fxml"));
            fxmlLoader.setControllerFactory(controllerClass -> new GameViewController());

            gameViewController = fxmlLoader.getController();
        }

        return gameViewController;
    }

    public Parent loadGameView() throws IOException {
        fxmlLoader = new FXMLLoader(getClass().getResource("../Views/GameView/GameView.fxml"));
        return fxmlLoader.load();
    }

    // Load LobbyView
    public LobbyViewController loadLobbyViewController() throws IOException {
        if (lobbyViewController == null) {
            fxmlLoader = new FXMLLoader(getClass().getResource("../Views/LobbyView/LobbyView.fxml"));
            fxmlLoader.setControllerFactory(controllerClass -> new LobbyViewController());

            lobbyViewController = fxmlLoader.getController();
        }

        return lobbyViewController;
    }

    public Parent loadLobbyView() throws IOException {
        fxmlLoader = new FXMLLoader(getClass().getResource("../Views/LobbyView/LobbyView.fxml"));
        return fxmlLoader.load();
    }

    // Load LobbyView
    public ChatViewController loadChatViewController() throws IOException {
        if (chatViewController == null) {
            fxmlLoader = new FXMLLoader(getClass().getResource("../Views/ChatView/ChatView.fxml"));
            fxmlLoader.setControllerFactory(controllerClass -> new ChatViewController());

            chatViewController = fxmlLoader.getController();
        }

        return chatViewController;
    }

    public Parent loadChatView() throws IOException {
        fxmlLoader = new FXMLLoader(getClass().getResource("../Views/ChatView/ChatView.fxml"));
        return fxmlLoader.load();
    }

    public PlanningPokerViewController loadPlanningPokerView() throws IOException {
        if (planningPokerViewController == null) {
            fxmlLoader = new FXMLLoader(getClass().getResource("../Views/PlanningPokerView/PlanningPokerView.fxml"));

            Scene planningPokerViewScene = new Scene(fxmlLoader.load());
            Stage planningPokerViewStage = new Stage();
            planningPokerViewStage.setTitle("Chat");
            planningPokerViewStage.setScene(planningPokerViewScene);
            planningPokerViewStage.show();
            planningPokerViewController = fxmlLoader.getController();
        }

        return planningPokerViewController;
    }


    // For closing loginView upon login
    public void closeLoginView() {
        loginViewStage.close();
    }

    // For closing MainView after game has been created/joined
    public void closeMainView() {
        mainViewStage.close();
    }

}
