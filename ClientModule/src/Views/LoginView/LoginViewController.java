package Views.LoginView;

import Application.ViewFactory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LoginViewController {
    @FXML public TextField usernameTextField;
    @FXML public TextField passwordTextField;
    @FXML public Button loginButton;
    @FXML public Button createUserButton;
    private final LoginViewModel loginViewModel;
    private final ViewFactory viewFactory;
    @FXML public Label usernameErrorLabel;
    @FXML public Label passwordErrorLabel;

    public LoginViewController(LoginViewModel loginViewModel, ViewFactory viewFactory) {
        this.loginViewModel = loginViewModel;
        this.viewFactory = viewFactory;
    }

    public void initialize() {
        loginViewModel.setOnLoginResult(this::onLoginResult);
        loginViewModel.setOnUserCreatedResult(this::onUserCreatedResult);
        Platform.runLater(this::catchCloseRequests);

        // Binding properties
        usernameTextField.textProperty().bindBidirectional(loginViewModel.usernameProperty());
        usernameErrorLabel.textProperty().bind(loginViewModel.usernameErrorMessageProperty());
        passwordTextField.textProperty().bindBidirectional(loginViewModel.passwordProperty());
        passwordErrorLabel.textProperty().bind(loginViewModel.passwordErrorMessageProperty());

        loginButton.disableProperty().bind(loginViewModel.usernameErrorMessageProperty().isNotNull().or(loginViewModel.passwordErrorMessageProperty().isNotNull()));
        createUserButton.disableProperty().bind(loginViewModel.usernameErrorMessageProperty().isNotNull().or(loginViewModel.passwordErrorMessageProperty().isNotNull()));
    }

    public void onLoginResult(Boolean success) {
        Platform.runLater(() -> {
            if (success) {
                try {
                    usernameTextField.clear();
                    passwordTextField.clear();

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
            System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", LoginViewController: User created: " + success);
            if (success) {
                // Create alert that lets the user know that the user has been created
                loginViewModel.showDialogBox("Bruger oprettet", "Din bruger er nu oprettet og du kan logge ind");
            }
        });
    }

    public void catchCloseRequests()
    {
        Stage thisStage = (Stage) loginButton.getScene().getWindow();
        loginViewModel.closeApplication(thisStage);
    }



    public void onLoginButtonPressed() {
        loginViewModel.attemptLogin();
    }

    public void onCreateUserButtonPressed() {
        loginViewModel.attemptCreateUser();
    }
}
