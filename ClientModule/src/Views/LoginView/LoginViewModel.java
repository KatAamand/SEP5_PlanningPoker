package Views.LoginView;

import Application.Session;
import DataTypes.User;
import Model.Login.LoginModel;
import Views.ViewModel;
import javafx.application.Platform;

import java.beans.PropertyChangeEvent;
import java.util.function.Consumer;

public class LoginViewModel extends ViewModel {
    private final LoginModel loginModel;
    private final Session session;

    private Consumer<Boolean> onLoginResult;

    public LoginViewModel(LoginModel loginModel, Session session) {
        super();
        this.session = session;
        this.loginModel = loginModel;

        loginModel.addPropertyChangeListener("LoginSuccess", this::loginUser);
    }

    private void loginUser(PropertyChangeEvent event) {
        Platform.runLater(() -> {
            onLoginResult.accept(true);
            session.setCurrentUser((User) event.getNewValue());
        });
    }

    public void setOnLoginResult(Consumer<Boolean> onLoginResult) {
        this.onLoginResult = onLoginResult;
    }

    public void requestLogin(String username, String password) {
        loginModel.requestLogin(username, password);
    }

    public void requestCreateUser(String username, String password) {
        loginModel.requestCreateUser(username, password);
    }
}
