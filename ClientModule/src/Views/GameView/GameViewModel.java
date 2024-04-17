package Views.GameView;

import Application.Session;
import Model.Game.GameModel;
import Views.ViewModel;

public class GameViewModel extends ViewModel
{
  private final GameModel gameModel;
  private final Session session;

  public GameViewModel(GameModel gameModel, Session session)
  {
    super();
    this.gameModel = gameModel;
    this.session = session;
  }
}
