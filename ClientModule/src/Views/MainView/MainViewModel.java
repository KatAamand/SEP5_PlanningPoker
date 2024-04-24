package Views.MainView;

import Application.ModelFactory;
import Application.Session;
import Model.MainView.MainModel;
import javafx.application.Platform;

import java.beans.PropertyChangeEvent;
import java.rmi.RemoteException;
import java.util.function.Consumer;

public class MainViewModel  {
    private final MainModel mainModel;
    private Consumer<Boolean> onPlanningPokerIDValidated;
    private Consumer<Boolean> onPlanningPokerIDCreatedResult;


    public MainViewModel() throws RemoteException
    {
        this.mainModel = ModelFactory.getInstance().getMainViewModel();

        mainModel.addPropertyChangeListener("planningPokerIDValidatedSuccess", this::planningPokerIDValidated);
        mainModel.addPropertyChangeListener("planningPokerIDCreatedSuccess", this::planningPokerIDCreated);
    }


    private void planningPokerIDValidated(PropertyChangeEvent event) {
        Platform.runLater(() -> {
            onPlanningPokerIDValidated.accept(true);
        });
    }

    private void planningPokerIDCreated(PropertyChangeEvent event) {
        Platform.runLater(() -> {
            System.out.println("VM: PlanningPoker created: " + event.getNewValue() + " with ID: " + Session.getConnectedGameId());
            onPlanningPokerIDCreatedResult.accept(true);
        });
    }

    public void setOnPlanningPokerIDValidateResult(Consumer<Boolean> onPlanningPokerIDValidated) {
        this.onPlanningPokerIDValidated = onPlanningPokerIDValidated;
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
