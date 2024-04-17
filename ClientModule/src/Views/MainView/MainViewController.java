package Views.MainView;

import Views.MainView.MainViewModel;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class MainViewController {
    MainViewModel mainViewModel;

    public Button createPlanningPokerButton;
    public Button connectToPlanningPokerButton;
    public TextField planningPokerIdTextField;

    public MainViewController(MainViewModel mainViewModel) {
        this.mainViewModel = mainViewModel;
    }

    public void initialize() {

    }

    public void onCreatePlanningPokerPressed(ActionEvent actionEvent) {
        // Tries to create a session
    }

    public void onConnectToPlanningPokerPressed(ActionEvent actionEvent) {
        // Tries to connect to a session
    }
}
