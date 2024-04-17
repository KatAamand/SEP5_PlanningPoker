package Views.MainView;

import Application.Session;
import Model.MainView.MainModel;
import Views.ViewModel;

public class MainViewModel extends ViewModel {
    private final MainModel mainModel;
    private final Session session;
    public MainViewModel(MainModel mainModel, Session session) {
        super();
        this.session = session; 
        this.mainModel = mainModel;
    }


    public void requestCreatePlanningPokerID()
    {
        mainModel.requestCreatePlanningPokerID();
    }

    public void requestConnectPlanningPokerID(String planningPokerID)
    {
        mainModel.requestConnectPlanningPoker(planningPokerID);
    }
}
