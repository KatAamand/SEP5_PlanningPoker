package Database.Effort;

import DataTypes.Effort;

import java.sql.SQLException;
import java.util.ArrayList;

public interface EffortDAO
{
  Effort readByEffort(String effort) throws SQLException;
  ArrayList<Effort> getEffortList() throws SQLException;
}
