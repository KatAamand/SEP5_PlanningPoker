package Views;

import Model.ClientModel;

public abstract class ViewModel {
    private ClientModel model;

    public ViewModel(ClientModel model) {
        this.model = model;
    }

    public ClientModel getModel() {
        return model;
    }
}
