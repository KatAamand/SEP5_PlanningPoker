package Database.Effort;

import DataTypes.Effort;
import Database.Connection.DatabaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class EffortDAOImpl extends DatabaseConnection implements EffortDAO
{
  private static EffortDAOImpl instance;
  private EffortDAOImpl() throws SQLException
  {
    DriverManager.registerDriver(new org.postgresql.Driver());
  }

  public static synchronized EffortDAOImpl getInstance() throws SQLException{
    if (instance == null)
    {
      instance = new EffortDAOImpl();
    }
    return instance;
  }

  @Override public Effort read() throws SQLException
  {
    return null;
  }

  @Override public Connection getConnection() throws SQLException
  {
  return super.getConnection();
  }
}
