package Database.Effort;

import DataTypes.Effort;

import java.sql.SQLException;

public interface EffortDAO
{
  Effort readByEffort(String effort) throws SQLException;
}
