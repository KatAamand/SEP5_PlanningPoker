package Database.PlanningPoker;

import DataTypes.PlanningPoker;

import java.sql.SQLException;
import java.util.ArrayList;

public interface PlanningPokerDAO
{
  // TODO: Source code comment/document
  PlanningPoker create() throws SQLException;

  // TODO: Source code comment/document
  PlanningPoker readByPlanningPoker(int planningPokerid) throws SQLException;

  /**
   * Retrieves all PlanningPoker objects from the database.
   * @return an ArrayList<PlanningPoker> containing all PlanningPoker objects.
   * @throws SQLException if there is an error accessing the database.
   */
  ArrayList<PlanningPoker> getAllPlanningPoker() throws SQLException;
}
