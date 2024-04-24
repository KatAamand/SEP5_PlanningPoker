package Application;

import Model.Chat.ChatModel;
import Model.Chat.ChatModelImpl;
import Model.Lobby.LobbyModel;
import Model.Lobby.LobbyModelImpl;
import Model.Login.LoginModel;
import Model.Login.LoginModelImpl;
import Model.MainView.MainModel;
import Model.MainView.MainModelImpl;
import Model.Game.GameModel;
import Model.Game.GameModelImpl;
import Model.PlanningPoker.PlanningPokerModelImpl;
import Model.Task.TaskModel;
import Model.Task.TaskModelImpl;

import java.rmi.RemoteException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ModelFactory {
    private static ModelFactory instance;
    private static final Lock lock = new ReentrantLock();
    private LoginModel model;
    private ClientFactory clientFactory;
    private LoginModelImpl loginModel;
    private ChatModelImpl chatModel;
    private MainModel mainviewModel;
    private GameModel gameModel;
    private TaskModel taskModel;
    private LobbyModel lobbyModel;
    private PlanningPokerModelImpl planningPokerModelImpl;

    private ModelFactory() {
        try {
            this.clientFactory = ClientFactory.getInstance();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized ModelFactory getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new ModelFactory();
                }
            }
        }
        return instance;
    }

    public LoginModel getLoginModel() throws RemoteException {
        if (loginModel == null) {
            loginModel = new LoginModelImpl();
        }
        return loginModel;
    }

    public ChatModel getChatModel() throws RemoteException {
        if (chatModel == null) {
            chatModel = new ChatModelImpl();
        }
        return chatModel;
    }

    public MainModel getMainViewModel() throws RemoteException {
        if (mainviewModel == null) {
            mainviewModel = new MainModelImpl();
        }
        return mainviewModel; 
    }

    public GameModel getGameModel() throws RemoteException {
        if (gameModel == null) {
            gameModel = new GameModelImpl();
        }
        return gameModel;
    }

    public TaskModel getTaskModel() throws RemoteException {
        if (taskModel == null) {
            taskModel = new TaskModelImpl();
        }
        return taskModel;
    }

    public LobbyModel getLobbyModel() throws RemoteException {
        if (lobbyModel == null) {
            lobbyModel = new LobbyModelImpl();
        }
        return lobbyModel;
    }

    public PlanningPokerModelImpl getPlanningPokerModel() throws RemoteException {
        if (planningPokerModelImpl == null) {
            planningPokerModelImpl = new PlanningPokerModelImpl();
        }
        return planningPokerModelImpl;
    }

}
