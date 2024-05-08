package Views.GameView;

import Application.ModelFactory;
import DataTypes.Task;
import Model.Game.GameModel;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;

import java.rmi.RemoteException;

public class GameViewModel
{
  private final GameModel gameModel;
  private Property<String> taskHeaderProperty;
  private Property<String> taskDescProperty;

  public GameViewModel() throws RemoteException
  {
    this.gameModel = ModelFactory.getInstance().getGameModel();
    taskHeaderProperty = new SimpleStringProperty();
    taskDescProperty = new SimpleStringProperty();
  }

  public void refresh() {
    Task nextTask = gameModel.nextTaskToEvaluate();

    if(nextTask != null) {
      taskHeaderPropertyProperty().setValue(nextTask.getTaskHeader());
      taskDescPropertyProperty().setValue(nextTask.getDescription());
    } else {
      taskHeaderPropertyProperty().setValue("No more tasks");
      taskDescPropertyProperty().setValue("No more tasks");
    }
  }

  public Property<String> taskHeaderPropertyProperty()
  {
    return taskHeaderProperty;
  }

  public Property<String> taskDescPropertyProperty()
  {
    return taskDescProperty;
  }
}
