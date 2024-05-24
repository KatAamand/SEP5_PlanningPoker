package Database.PlanningPoker;

import DataTypes.PlanningPoker;

import java.sql.SQLException;
import java.util.ArrayList;

public interface PlanningPokerDAO
{
  /**
   * Creates a new planningPoker with a unique serializable Planning Poker ID.
   * @return the created planningPoker object.
   * @throws SQLException if there is an error accessing the database.
   */
  PlanningPoker create() throws SQLException;

  /**
   * Retrieves a planningpPoker by its ID from the database.
   * @param planningPokerid the ID of the planningPoker to retrieve.
   * @return the planningPoker object.
   * @throws SQLException if there is an error accessing the database.
   */
  PlanningPoker readByPlanningPoker(int planningPokerid) throws SQLException;

  /**
   * Retrieves all PlanningPoker objects from the database.
   * @return an ArrayList<PlanningPoker> containing all PlanningPoker objects.
   * @throws SQLException if there is an error accessing the database.
   */
  ArrayList<PlanningPoker> getAllPlanningPoker() throws SQLException;
}
