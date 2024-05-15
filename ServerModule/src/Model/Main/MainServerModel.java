package Model.Main;

import DataTypes.PlanningPoker;
import Util.PropertyChangeSubject;

public interface MainServerModel extends PropertyChangeSubject
{
  boolean validatePlanningPoker(int planningPokerID);
  PlanningPoker createPlanningPoker();
  PlanningPoker getPlanningPokerGame(int planningPokerId);
  void getAllPlanningPokersFromDb();
}
