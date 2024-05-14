package Model.Main;

import DataTypes.PlanningPoker;
import Util.PropertyChangeSubject;

import java.util.ArrayList;

public interface MainServerModel extends PropertyChangeSubject
{
  boolean validatePlanningPoker(String planningPokerID);
  PlanningPoker createPlanningPoker();
  PlanningPoker getPlanningPokerGame(String planningPokerId);
  void getAllPlanningPokersFromDb();
}
