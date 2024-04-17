package Model.Main;

import Util.PropertyChangeSubject;

public interface MainServerModel extends PropertyChangeSubject
{
  void validatePlanningPoker(String planningPokerID);

  void createPlanningPoker();
}
