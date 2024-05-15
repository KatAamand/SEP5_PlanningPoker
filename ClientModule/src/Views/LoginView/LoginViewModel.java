package Views.LoginView;

import Application.Session;
import Application.ViewFactory;
import DataTypes.User;
import Model.Login.LoginModel;
import Views.ViewModel;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.beans.PropertyChangeEvent;
import java.util.function.Consumer;

public class LoginViewModel extends ViewModel {
    private final LoginModel loginModel;
    private Consumer<Boolean> onLoginResult;
    private Consumer<Boolean> onUserCreatedResult;
    private StringProperty username = new SimpleStringProperty();
    private StringProperty usernameErrorMessage = new SimpleStringProperty();
    private StringProperty password = new SimpleStringProperty();
    private StringProperty passwordErrorMessage = new SimpleStringProperty();

    public LoginViewModel(LoginModel loginModel) {
        super();
        this.loginModel = loginModel;

        loginModel.addPropertyChangeListener("userLoginSuccess", this::loginUser);
        loginModel.addPropertyChangeListener("userCreatedSuccess", this::userCreated);
        loginModel.addPropertyChangeListener("userValidationFailed", this::loginAttemptFailed);

        username.addListener(((observable, oldValue, newValue) -> validateUsername(newValue)));
        password.addListener((((observable, oldValue, newValue) -> validatePassword(newValue))));
    }

    private void loginAttemptFailed(PropertyChangeEvent evt) {
        showAlertBox("Login fejlede", (String) evt.getNewValue());
    }

    public void attemptLogin() {
        String enteredUsername = getUsername();
        String enteredPassword = getPassword();

        if (validateInput(enteredUsername, enteredPassword)) {
            requestLogin(enteredUsername, enteredPassword);
        }
    }

    public void attemptCreateUser() {
        String enteredUsername = getUsername();
        String enteredPassword = getPassword();

        if (validateInput(enteredUsername, enteredPassword)) {
            requestCreateUser(enteredUsername, enteredPassword);
        }
    }

    private boolean validateInput(String enteredUsername, String enteredPassword) {
        boolean isUsernameValid = validateUsername(enteredUsername);
        boolean isPasswordValid = validatePassword(enteredPassword);

        return isUsernameValid && isPasswordValid;
    }


    private boolean validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            usernameErrorMessage.set("Brugernavn må ikke være tomt");
            return false;
        } else if (username.length() < 4) {
            usernameErrorMessage.set("Brugernavn skal være mindst 4 tegn");
            return false;
        } else {
            usernameErrorMessage.set(null); // No errors in input value
            return true;
        }
    }

    private boolean validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            passwordErrorMessage.set("Password må ikke være tomt");
            return false;
        } else if (password.length() < 4) {
            passwordErrorMessage.set("Password skal være mindst 4 tegn");
            return false;
        } else {
            passwordErrorMessage.set(null); // No errors in input value
            return true;
        }
    }

    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public StringProperty usernameErrorMessageProperty() {
        return usernameErrorMessage;
    }

    public String getPassword() {
        return password.get();
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public StringProperty passwordErrorMessageProperty() {
        return passwordErrorMessage;
    }

    private void loginUser(PropertyChangeEvent event) {
        Platform.runLater(() -> {
            onLoginResult.accept(true);
            Session.setCurrentUser((User) event.getNewValue());
        });
    }

    private void userCreated(PropertyChangeEvent event) {
        Platform.runLater(() -> {
            System.out.println("VM: User created: " + event.getNewValue());
            onUserCreatedResult.accept(true);
        });
    }

    public void requestLogin(String username, String password) {

        loginModel.requestLogin(username, password);
    }

    public void requestCreateUser(String username, String password) {
        loginModel.requestCreateUser(username, password);
    }


    public void setOnLoginResult(Consumer<Boolean> onLoginResult) {
        this.onLoginResult = onLoginResult;
    }
    public void setOnUserCreatedResult(Consumer<Boolean> onUserCreatedResult) {
        this.onUserCreatedResult = onUserCreatedResult;
    }

    public void showDialogBox(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void showAlertBox(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void closeApplication(Stage currentStage)
    {
        currentStage.setOnCloseRequest(event -> {
            // Consume the event to prevent the default close behavior:
            event.consume();

            // Logout and Unregister this client from the server:
            if(Session.getCurrentUser() == null) {
                loginModel.requestLogout(null, null);
            } else {
                loginModel.requestLogout(Session.getCurrentUser().getUsername(), Session.getCurrentUser().getPassword());
            }
        });
    }
}
