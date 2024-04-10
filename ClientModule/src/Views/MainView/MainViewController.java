package Views.MainView;

import Views.MainView.MainViewModel;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class MainViewController {
    MainViewModel mainViewModel;

    public Button createSessionButton;
    public Button connectToSessionButton;
    public TextField sessionIdTextField;

    public MainViewController(MainViewModel mainViewModel) {
        this.mainViewModel = mainViewModel;
    }

    public void onCreateSessionButtonPressed(ActionEvent actionEvent) {
        // Tries to create a session
    }

    public void onConnectToSessionButtonPressed(ActionEvent actionEvent) {
        // Tries to connect to a session
    }
}
