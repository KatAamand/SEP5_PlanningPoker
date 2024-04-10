package Views.MainView;

import Model.ClientModel;
import Views.ViewModel;

public class MainViewModel extends ViewModel {
    private final ClientModel clientModel;
    public MainViewModel(ClientModel clientModel) {
        super();
        this.clientModel = clientModel;
    }
}
