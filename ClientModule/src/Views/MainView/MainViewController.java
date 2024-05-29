package Views.MainView;

import Application.ViewFactory;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.RemoteException;

public class MainViewController
{

  public Button createPlanningPokerButton;
  public Button connectToPlanningPokerButton;
  public TextField planningPokerIdTextField;
  private final MainViewModel mainViewModel;
  private final ViewFactory viewFactory;

  public MainViewController(MainViewModel mainViewModel,
      ViewFactory viewFactory)
  {
    this.mainViewModel = mainViewModel;
    this.viewFactory = viewFactory;
  }

  public void initialize()
  {
    mainViewModel.setOnPlanningPokerIDCreatedResult(
        this::onCreatePlanningPokerResult);
    mainViewModel.setOnPlanningPokerIDValidateResult(
        this::onConnectToPlanningPokerResult);
    Platform.runLater(this::catchCloseRequests);
  }

  public void onCreatePlanningPokerResult(Boolean success)
  {
    Platform.runLater(() -> {
      if (success)
      {
        try
        {
          viewFactory.loadPlanningPokerView();
          viewFactory.closeMainView();
        }
        catch (IOException e)
        {
          throw new RuntimeException(e);
        }
      }
    });
  }

  public void onConnectToPlanningPokerResult(Boolean success)
  {
    Platform.runLater(() -> {
      if (success)
      {
        try
        {
          viewFactory.loadPlanningPokerView();
          viewFactory.closeMainView();
        }
        catch (IOException e)
        {
          throw new RuntimeException(e);
        }
      }
    });
  }

  public void onCreatePlanningPokerPressed() throws RemoteException
  {
    mainViewModel.requestCreatePlanningPokerID();
  }

  public void onConnectToPlanningPokerPressed() throws RemoteException
  {
    try
    {
      int planningPokerID = Integer.parseInt(planningPokerIdTextField.getText());
      mainViewModel.requestConnectPlanningPokerID(planningPokerID);
      planningPokerIdTextField.clear();
    }
    catch (NumberFormatException e)
    {
      System.out.println("Invalid PlaningPoker_ID format.");
    }

  }

  public void catchCloseRequests()
  {
    Stage thisStage = (Stage) createPlanningPokerButton.getScene().getWindow();
    mainViewModel.closeApplication(thisStage);
  }
}
