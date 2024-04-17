package DataTypes.States;

import DataTypes.Session;

public class GameState implements SessionState {
    @Override public void init(Session session)
    {
        session.setCurrentState(this);
    }
}
