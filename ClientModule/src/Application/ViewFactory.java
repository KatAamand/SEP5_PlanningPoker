package Application;

import Views.ChatView.ChatViewController;
import Views.LobbyView.LobbyViewController;
import Views.LoginView.LoginViewController;
import Views.MainView.MainViewController;
import Views.GameView.GameViewController;
import Views.PlanningPokerView.PlanningPokerViewController;
import Views.TaskView.TaskViewController;
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
    private GameViewController gameViewController;
    private LobbyViewController lobbyViewController;
    private TaskViewController taskViewController;
    private ChatViewController chatViewController;
    private PlanningPokerViewController planningPokerViewController;

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
    public GameViewController loadSessionView() throws IOException {
        if (gameViewController == null) {
            fxmlLoader = new FXMLLoader(getClass().getResource("../Views/SessionsView/GameView.fxml"));
            fxmlLoader.setControllerFactory(controllerClass -> {
                try {
                    return new GameViewController(viewModelFactory.getGameViewModel());
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            });

            gameViewController = fxmlLoader.getController();
        }

        return gameViewController;
    }

    // Load LobbyView
    public LobbyViewController loadLobbyView() throws IOException {
        if (lobbyViewController == null) {
            fxmlLoader = new FXMLLoader(getClass().getResource("../Views/LobbyView/LobbyView.fxml"));
            fxmlLoader.setControllerFactory(controllerClass -> {
                try {
                    return new LobbyViewController(viewModelFactory.getLobbyViewModel());
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            });

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
                    return new TaskViewController(viewModelFactory.getTaskViewModel());
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            });
            
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
                    return new ChatViewController(viewModelFactory.getChatViewModel());
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            });
            
            chatViewController = fxmlLoader.getController();
        }

        return chatViewController;
    }

    public PlanningPokerViewController loadPlanningPokerView() throws IOException {
        if (planningPokerViewController == null) {
            fxmlLoader = new FXMLLoader(getClass().getResource("../Views/PlanningPokerView/PlanningPokerView.fxml"));
            fxmlLoader.setControllerFactory(controllerClass -> {
                try {
                    return new PlanningPokerViewController(loadSessionView(), loadTaskView(), loadChatView());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

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

}
