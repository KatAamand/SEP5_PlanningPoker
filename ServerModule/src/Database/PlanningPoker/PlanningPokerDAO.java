package Database.PlanningPoker;

import DataTypes.PlanningPoker;

import java.sql.SQLException;
import java.util.ArrayList;

public interface PlanningPokerDAO
{
  PlanningPoker create() throws SQLException;
  PlanningPoker readByPlanningPoker(int planningPokerid) throws SQLException;
  ArrayList<PlanningPoker> getAllPlanningPoker() throws SQLException;
}
