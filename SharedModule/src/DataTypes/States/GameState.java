package DataTypes.States;

import DataTypes.PlanningPoker;

public class GameState implements PlanningPokerState
{
    @Override public void init(PlanningPoker planningPoker)
    {
        planningPoker.setCurrentState(this);
    }
}
