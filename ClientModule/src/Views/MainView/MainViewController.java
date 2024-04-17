package Views.MainView;

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

    public void onCreatePlanningPokerPressed() {
       mainViewModel.requestCreatePlanningPokerID();

    }

    public void onConnectToPlanningPokerPressed() {
        try
        {
            String planningPokerID = null;
            mainViewModel.requestConnectPlanningPokerID(planningPokerID);
        }
        catch (NumberFormatException e)
        {
            System.out.println("Invalid PlaningPoker_ID format.");
        }

    }
}
