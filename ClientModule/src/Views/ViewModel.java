package Views;

import Model.Login.LoginModel;

public abstract class ViewModel {
    private LoginModel model;

    public ViewModel() {}

    public LoginModel getModel() {
        return model;
    }
}
