package Database.Effort;

import DataTypes.Effort;

import java.sql.SQLException;
import java.util.ArrayList;

public interface EffortDAO
{
  // TODO: Source code comment/document
  Effort readByEffort(String effort) throws SQLException;

  /**
   * Retrieves a list of all Effort objects from the database.
   * @return an ArrayList<Effort> containing all efforts.
   * @throws SQLException if there is an error accessing the database.
   */
  ArrayList<Effort> getEffortList() throws SQLException;

}
