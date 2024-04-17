package Views.LobbyView;

import Application.Session;
import Model.Lobby.LobbyModel;
import Views.ViewModel;

public class LobbyViewModel extends ViewModel
{
  private final LobbyModel lobbyModel;
  private final Session session;

  public LobbyViewModel(LobbyModel lobbyModel, Session session)
  {
    super();
    this.session = session;
    this.lobbyModel = lobbyModel;
  }
}
