package Views.LoginView;

import Model.Login.LoginModel;
import Views.ViewModel;

public class LoginViewModel extends ViewModel {
    private final LoginModel loginModel;

    public LoginViewModel(LoginModel loginModel) {
        super();
        this.loginModel = loginModel;
    }
}
