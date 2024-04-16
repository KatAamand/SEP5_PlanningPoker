package Views.MainView;

import Model.MainView.MainModel;
import Views.ViewModel;

public class MainViewModel extends ViewModel {
    private final MainModel mainModel;
    public MainViewModel(MainModel mainModel) {
        super();
        this.mainModel = mainModel;
    }
}
