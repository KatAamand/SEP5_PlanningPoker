package DataTypes.States;

import DataTypes.Session;

public class LobbyState implements SessionState
{

  public void init(Session session)
  {
session.setCurrentState(this);
  }
}
