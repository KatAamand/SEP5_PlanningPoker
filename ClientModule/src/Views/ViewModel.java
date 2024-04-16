package Views;

import Model.Login.LoginModel;

public abstract class ViewModel {
    private LoginModel model;

    public ViewModel() {
        this.model = model;
    }

    public LoginModel getModel() {
        return model;
    }
}
