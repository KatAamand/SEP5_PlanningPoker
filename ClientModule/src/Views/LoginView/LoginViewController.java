package Views.LoginView;

import Views.LoginView.LoginViewModel;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class LoginViewController {
    public TextField usernameTextField;
    public TextField passwordTextField;
    public Button loginButton;
    public Button createUserButton;
    private LoginViewModel loginViewModel;

    public LoginViewController(LoginViewModel loginViewModel) {
        this.loginViewModel = loginViewModel;
    }

    public void onLoginButtonPressed()
    {
        // tries to login
    }

    public void onCreateUserButtonPressed() {
        // tries to create a user
    }
}
