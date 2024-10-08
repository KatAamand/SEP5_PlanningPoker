package Views.MainView;

import Application.ModelFactory;
import Application.Session;
import Model.MainView.MainModel;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.beans.PropertyChangeEvent;
import java.rmi.RemoteException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
        Platform.runLater(() -> {onPlanningPokerIDValidated.accept(true);});
    }

    private void planningPokerIDCreated(PropertyChangeEvent event) {
        Platform.runLater(() -> {
            System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", MainViewModel: PlanningPoker created: " + event.getNewValue() + " with ID: " + Session.getConnectedGameId());
            onPlanningPokerIDCreatedResult.accept(true);
        });
    }

    public void setOnPlanningPokerIDValidateResult(Consumer<Boolean> onPlanningPokerIDValidated) {
        this.onPlanningPokerIDValidated = onPlanningPokerIDValidated;
    }
    public void setOnPlanningPokerIDCreatedResult(Consumer<Boolean> onPlanningPokerIDCreatedResult) {
        this.onPlanningPokerIDCreatedResult = onPlanningPokerIDCreatedResult;
    }
    public void requestCreatePlanningPokerID() throws RemoteException {
        mainModel.requestCreatePlanningPokerID();
    }

    public void requestConnectPlanningPokerID(int planningPokerID) throws RemoteException {
        mainModel.requestConnectPlanningPoker(planningPokerID);
    }

    public void closeApplication(Stage currentStage)
    {
        currentStage.setOnCloseRequest(event -> {
            // Consume the event to prevent the default close behavior:
            event.consume();

            // Logout and Unregister this client from the server:
            if(Session.getCurrentUser() == null) {
                try {
                    ModelFactory.getInstance().getLoginModel().requestLogout(null, null);
                } catch (RemoteException e) {
                    throw new RuntimeException();
                }
            } else {
                try {
                    ModelFactory.getInstance().getLoginModel().requestLogout(Session.getCurrentUser().getUsername(), Session.getCurrentUser().getPassword());
                } catch (RemoteException e) {
                    throw new RuntimeException();
                }
            }
        });
    }
}
