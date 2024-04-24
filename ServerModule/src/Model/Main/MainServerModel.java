package Model.Main;

import DataTypes.PlanningPoker;
import Util.PropertyChangeSubject;

public interface MainServerModel extends PropertyChangeSubject
{
  boolean validatePlanningPoker(String planningPokerID);

  PlanningPoker createPlanningPoker();
  PlanningPoker getPlanningPokerGame(String planningPokerId);
}
