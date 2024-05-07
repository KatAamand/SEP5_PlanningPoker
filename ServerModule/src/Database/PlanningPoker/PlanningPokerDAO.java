package Database.PlanningPoker;

import DataTypes.PlanningPoker;

public interface PlanningPokerDAO
{
  PlanningPoker create();
  PlanningPoker readByPlanningPoker(int id);
}
