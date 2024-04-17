package DataTypes.States;

import DataTypes.PlanningPoker;

public class LobbyState implements PlanningPokerState
{
    public void init(PlanningPoker planningPoker)
    {
        planningPoker.setCurrentState(this);
    }
}
