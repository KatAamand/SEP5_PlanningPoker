package Views.LobbyView;

import Model.Lobby.LobbyModel;
import Views.ViewModel;

public class LobbyViewModel extends ViewModel
{
  private final LobbyModel lobbyModel;

  public LobbyViewModel(LobbyModel lobbyModel)
  {
    super();
    this.lobbyModel = lobbyModel;
  }
}
