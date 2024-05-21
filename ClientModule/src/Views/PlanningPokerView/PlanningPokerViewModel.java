package Views.PlanningPokerView;

import Application.ModelFactory;
import Application.Session;
import Application.ViewFactory;
import Application.ViewModelFactory;
import DataTypes.UserRoles.UserPermission;
import Model.PlanningPoker.PlanningPokerModel;
import Util.PropertyChangeSubject;
import Views.TaskView.TaskViewModel;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.rmi.RemoteException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class PlanningPokerViewModel implements PropertyChangeSubject {
    private TaskViewModel taskViewModel;
    private PlanningPokerModel planningPokerModel;
    PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);


    public PlanningPokerViewModel() throws RemoteException {
        taskViewModel = ViewModelFactory.getInstance().getTaskViewModel();
        planningPokerModel = ModelFactory.getInstance().getPlanningPokerModel();

        // Assign listener:
        planningPokerModel.addPropertyChangeListener("PlanningPokerObjUpdated", (evt) -> {
            refresh();
        });

        planningPokerModel.addPropertyChangeListener("UpdatedLocalUser", (evt) -> {
            // Only update the username, role and session ID section when receiving this event.
            setTaskViewData();
        });

        planningPokerModel.addPropertyChangeListener("gameStarted", (evt) -> {
            propertyChangeSupport.firePropertyChange("gameStarted", null, null);
        });
    }

    public void init() {
        Platform.runLater(this::refresh);
    }

    /**
     * Initializes the initial data that is shown inside the TaskView pane upon first loading the PlanningPokerView
     */
    private void setTaskViewData() {
        //Set the user label in the TaskView:
        String user = "ERROR";
        String role = "Unknown";
        if (Session.getCurrentUser() != null) {
            user = Session.getCurrentUser().getUsername();
            role = Session.getCurrentUser().getRole().getRoleAsString();

            if(Session.getCurrentUser().getAdmin() != null) {
                role += " / " + Session.getCurrentUser().getAdmin().getRoleAsString();
            }
        }
        String finalRole = role;
        String finalUser = user;
        Platform.runLater(() -> {taskViewModel.labelUserIdProperty().setValue(finalUser + " [" + finalRole + "]");});

        //Set the GameId in the TaskView:
        int planningPokerId = 0;
        if (Session.getConnectedGameId() != 0) {
            planningPokerId = planningPokerModel.getActivePlanningPokerGame().getPlanningPokerID();
        }
        taskViewModel.sessionIdProperty().setValue(String.valueOf(planningPokerId));
    }

    public void refresh() {
        // Refreshes the username, game id and user role shown on the Planning Poker Game UI:
        setTaskViewData();
    }

    public void closePlanningPoker(Stage currentStage) {
        currentStage.setOnCloseRequest(event -> {
            // Consume the event to prevent the default close behavior:
            event.consume();

            // Try to close the window
            try {
                // Reset the UI elements:
                try {
                    // This list must be reset upon exiting the shown Planning Poker view in order to avoid tasks from the previous game mistakenly being added when using the skip functionality:
                    ModelFactory.getInstance().getGameModel().getSkippedTaskList().clear();
                } catch (RemoteException e) {
                    throw new RuntimeException();
                }

                // TODO: Implement the functionality to delete the currently active PlanningPoker game, as described in Use Case #2, ALT0 sequence.

                System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", PlanningPokerViewModel: Closing planning poker window");

                // Reset the users permission level to simply be a developer, outside the game that is being closed:
                // Also removes the user from the current session, before closing it
                planningPokerModel.resetUserPermissionUponLeavingGame();


                // Redirect the local user to the main view:
                Platform.runLater(() -> {
                    ViewFactory.getInstance().getMainViewStage().show();
                    ViewFactory.getInstance().resetPlanningPokerViewController();
                    currentStage.close();
                });
            } catch (RuntimeException e) {
                // Error occurred. Close the entire application. Likely cause is that the server has shut down / connection has been lost.
                Platform.exit();
            }
        });
    }

    public void requestGameStart() {
        if(Session.getCurrentUser().getRole().getPermissions().contains(UserPermission.START_PLANNINGPOKER_GAME)) {
            planningPokerModel.requestStartGame();
        } else {
            this.showAlertBox();
        }

    }

    private void showAlertBox() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid permission");
        alert.setHeaderText("Only the Scrum Master may start the game");
        alert.showAndWait();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void addPropertyChangeListener(String name, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(name, listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(String name, PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(name, listener);
    }
}
