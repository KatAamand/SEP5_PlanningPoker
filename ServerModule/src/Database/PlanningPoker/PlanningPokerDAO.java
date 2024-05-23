package Database.PlanningPoker;

import DataTypes.PlanningPoker;

import java.sql.SQLException;
import java.util.ArrayList;

public interface PlanningPokerDAO
{
  PlanningPoker create() throws SQLException;
  PlanningPoker readByPlanningPoker(int planningPokerid) throws SQLException;

  /**
   * Retrieves all PlanningPoker objects from the database.
   * @return an ArrayList<PlanningPoker> containing all PlanningPoker objects.
   * @throws SQLException if there is an error accessing the database.
   */
  ArrayList<PlanningPoker> getAllPlanningPoker() throws SQLException;

}
