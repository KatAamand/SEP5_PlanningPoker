package Views.GameView;

import Model.Game.GameModel;
import Model.Login.LoginModel;

public class GameViewModel
{
  private final LoginModel clientModel;

  public GameViewModel(GameModel clientModel)
  {
    this.clientModel = clientModel;
  }
}
