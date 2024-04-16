package Views.LobbyView;

import Model.Lobby.LobbyModel;
import Model.Login.LoginModel;

public class LobbyViewModel
{
  private final LoginModel clientModel;

  public LobbyViewModel(LobbyModel clientModel)
  {
    this.clientModel = clientModel;
  }
}
