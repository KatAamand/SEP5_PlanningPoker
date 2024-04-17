package Views.LoginView;

import Application.ViewFactory;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.IOException;

public class LoginViewController {
    public TextField usernameTextField;
    public TextField passwordTextField;
    public Button loginButton;
    public Button createUserButton;
    private final LoginViewModel loginViewModel;
    private final ViewFactory viewFactory;

    public LoginViewController(LoginViewModel loginViewModel, ViewFactory viewFactory) {
        this.loginViewModel = loginViewModel;
        this.viewFactory = viewFactory;
    }

    public void initialize() {
        loginViewModel.setOnLoginResult(this::onLoginResult);
        loginViewModel.setOnLoginResult(this::onUserCreatedResult);
    }

    public void onLoginResult(Boolean success) {
        Platform.runLater(() -> {
            if (success) {
                try {
                    viewFactory.loadMainView();
                    viewFactory.closeLoginView();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }


    public void onUserCreatedResult(Boolean success) {
        Platform.runLater(() -> {
            if (success) {
                // Create alert that let's the user know that the user has been created
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Bruger oprettet");
                alert.setHeaderText(null);
                alert.setContentText("Din bruger er nu oprettet og du kan logge ind.");
                alert.showAndWait();
            } else {
                // Create alert that let's the user know that there was an error creating the user
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Brugeroprettelse fejlet");
                alert.setHeaderText(null);
                alert.setContentText("Din bruge kan ikke oprettes, vent og prøv igen senere");
                alert.showAndWait();
            }
        });
    }

    public void onLoginButtonPressed()
    {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();

        loginViewModel.requestLogin(username, password);
        usernameTextField.clear();
        passwordTextField.clear();
    }

    public void onCreateUserButtonPressed() {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();

        loginViewModel.requestCreateUser(username, password);
    }
}
