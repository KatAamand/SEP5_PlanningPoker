package Database.Effort;

import DataTypes.Effort;

import java.sql.SQLException;
import java.util.ArrayList;

public interface EffortDAO
{
  /**
   * Retrieves a effort by its value from the database.
   * @param effort the value of the effort to retrieve
   * @return the effortvalue and the coorsponding image for plannigpokerCards.
   * @throws SQLException if there is an error accessing the database, or null if not found.
   */
  Effort readByEffort(String effort) throws SQLException;

  /**
   * Retrieves a list of all Effort objects from the database.
   * @return an ArrayList<Effort> containing all efforts.
   * @throws SQLException if there is an error accessing the database.
   */
  ArrayList<Effort> getEffortList() throws SQLException;

}
