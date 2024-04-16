package Application;


import Views.ChatView.ChatViewModel;
import Views.LobbyView.LobbyViewModel;
import Views.LoginView.LoginViewModel;
import Views.MainView.MainViewModel;
import Views.GameView.GameViewModel;
import Views.TaskView.TaskViewModel;

import java.rmi.RemoteException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ViewModelFactory {
    private static ViewModelFactory instance;
    private final static Lock lock = new ReentrantLock();
    private ModelFactory modelFactory;
    private LoginViewModel loginViewModel;
    private MainViewModel mainViewModel;
    private LobbyViewModel lobbyViewModel;
    private GameViewModel gameViewModel;
    private TaskViewModel taskViewModel;
    private ChatViewModel chatViewModel;

    private ViewModelFactory(ModelFactory modelFactory) {
        this.modelFactory = modelFactory;
    }

    public static ViewModelFactory getInstance(ModelFactory modelFactory) {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new ViewModelFactory(modelFactory);
                }
            }
        }

        return instance;
    }

    public LoginViewModel getLoginViewModel() throws RemoteException {
        if (loginViewModel == null) {
            loginViewModel = new LoginViewModel(modelFactory.getLoginModel());
        }

        return loginViewModel;
    }

    public MainViewModel getMainViewModel() throws RemoteException {
        if (mainViewModel == null) {
            mainViewModel = new MainViewModel(modelFactory.getMainViewModel());
        }

        return mainViewModel;
    }

    public LobbyViewModel getLobbyViewModel() throws RemoteException {
        if (lobbyViewModel == null) {
            lobbyViewModel = new LobbyViewModel(modelFactory.getLobbyModel());
        }

        return lobbyViewModel;
    }

    public GameViewModel getGameViewModel() throws RemoteException {
        if (gameViewModel == null) {
            gameViewModel = new GameViewModel(modelFactory.getGameModel());
        }

        return gameViewModel;
    }

    public TaskViewModel getTaskViewModel() throws RemoteException {
        if (taskViewModel == null) {
            taskViewModel = new TaskViewModel(modelFactory.getTaskModel());
        }

        return taskViewModel;
    }

    public ChatViewModel getChatViewModel() throws RemoteException {
        if (chatViewModel == null) {
            chatViewModel = new ChatViewModel(modelFactory.getChatModel());
        }

        return chatViewModel;
    }

}