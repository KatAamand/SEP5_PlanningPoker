package Views.LobbyView;

import Application.ModelFactory;
import Model.Lobby.LobbyModel;
import java.rmi.RemoteException;

public class LobbyViewModel
{
  private final LobbyModel lobbyModel;

  public LobbyViewModel() throws RemoteException
  {
    this.lobbyModel = ModelFactory.getInstance().getLobbyModel();
  }
}
