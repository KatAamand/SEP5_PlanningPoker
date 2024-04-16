package DataTypes;

import DataTypes.States.LobbyState;
import DataTypes.States.SessionState;

import javax.swing.plaf.nimbus.State;
import java.util.ArrayList;

public class Session
{
  private String connectionCode;
  private ArrayList<User> connectedUsers;
  private SessionState currentState;
  public Session()
  {
    connectedUsers = new ArrayList<>();
  }

  public void setCurrentState(SessionState sessionState)
  {
    this.currentState = sessionState;
  }

  public SessionState getCurrentState(SessionState sessionState)
  {
    return sessionState;
  }

  public void declareFinalEffort(Task task)
  {

  }



}
