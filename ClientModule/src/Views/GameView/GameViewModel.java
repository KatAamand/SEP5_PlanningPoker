package Views.GameView;

import Model.Game.GameModel;
import Views.ViewModel;

public class GameViewModel extends ViewModel
{
  private final GameModel gameModel;

  public GameViewModel(GameModel gameModel)
  {
    super();
    this.gameModel = gameModel;
  }
}
