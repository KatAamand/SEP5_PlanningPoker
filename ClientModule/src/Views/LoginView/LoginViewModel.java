package Views.LoginView;

import Model.ClientModel;
import Views.ViewModel;

public class LoginViewModel extends ViewModel {
    private final ClientModel clientModel;

    public LoginViewModel(ClientModel clientModel) {
        super();
        this.clientModel = clientModel; 
    }
}
