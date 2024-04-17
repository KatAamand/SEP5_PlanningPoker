package Views.LoginView;

import Application.Session;
import Model.Login.LoginModel;
import Views.ViewModel;

public class LoginViewModel extends ViewModel {
    private final LoginModel loginModel;
    private final Session session;

    public LoginViewModel(LoginModel loginModel, Session session) {
        super();
        this.session = session;
        this.loginModel = loginModel;
    }
}
