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
    private LoginViewController loginViewController; 
    private MainViewController mainViewController; 
    private final Stage loginStage;
    private FXMLLoader fxmlLoader;

    private ViewFactory(ViewModelFactory viewModelFactory, Stage stage) {
        this.viewModelFactory = viewModelFactory;
        this.loginStage = stage;
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

            Scene loginScene = new Scene(fxmlLoader.load());
            loginStage.setTitle("Login");
            loginStage.setScene(loginScene);
            loginStage.show();
            loginViewController = fxmlLoader.getController();
        }
        return loginViewController;
    }

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
            Stage mainStage = new Stage();
            mainStage.setTitle("Vinyl Library");
            mainStage.setScene(mainViewScene);
            mainStage.show();
            mainViewController = fxmlLoader.getController();
        }

        return mainViewController;
    }

    public void closeLoginView() {
        loginStage.close();
    }

}
