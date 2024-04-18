package Views.MainView;

import Application.Session;
import DataTypes.User;
import Model.MainView.MainModel;
import Views.ViewModel;
import javafx.application.Platform;

import java.beans.PropertyChangeEvent;
import java.util.function.Consumer;

public class MainViewModel extends ViewModel {
    private final MainModel mainModel;
    private final Session session;

    private Consumer<Boolean> onPlannigPokerIDValidated;
    private Consumer<Boolean> onPlanningPokerIDCreatedResult;
    public MainViewModel(MainModel mainModel, Session session) {
        super();
        this.session = session; 
        this.mainModel = mainModel;

        mainModel.addPropertyChangeListener("planningPokerIDValidateSuccess", this::plannigPokerIDValidated);
        mainModel.addPropertyChangeListener("planningPokerIDCreatedSuccess", this::plannigPokerIDCreated);
    }


    private void plannigPokerIDValidated(PropertyChangeEvent event) {
        Platform.runLater(() -> {
            onPlannigPokerIDValidated.accept(true);
            session.setCurrentUser((User) event.getNewValue());
        });
    }

    private void plannigPokerIDCreated(PropertyChangeEvent event) {
        Platform.runLater(() -> {
            System.out.println("VM: PlannigPoker created: " + event.getNewValue());
            onPlanningPokerIDCreatedResult.accept(true);
        });
    }

    public void setOnPlanningPokerIDValidateResult(Consumer<Boolean> onPlannigPokerIDValidated) {
        this.onPlannigPokerIDValidated = onPlannigPokerIDValidated;
    }
    public void setOnPlanningPokerIDCreatedResult(Consumer<Boolean> onPlanningPokerIDCreatedResult) {
        this.onPlanningPokerIDCreatedResult = onPlanningPokerIDCreatedResult;
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
