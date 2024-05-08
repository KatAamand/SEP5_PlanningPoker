package Database.Task;

import DataTypes.PlanningPoker;
import DataTypes.Task;
import Database.Connection.DatabaseConnection;
import Database.Effort.EffortDAOImpl;

import java.sql.*;

public class TaskDAOImpl extends DatabaseConnection implements TaskDAO
{
  private static TaskDAOImpl instance;
  private TaskDAOImpl() throws SQLException
  {
    DriverManager.registerDriver(new org.postgresql.Driver());
  }

  public static synchronized TaskDAOImpl getInstance() throws SQLException{
    if (instance == null)
    {
      instance = new TaskDAOImpl();
    }
    return instance;
  }



  @Override public Task create(String header, String desc) throws SQLException
  {
    try (Connection connection = getConnection())

    {
      PreparedStatement statement = connection.prepareStatement(
          "insert into Task());
      statement.executeUpdate();
      ResultSet ids = statement.getResultSet();
      if (ids.next())
      {
        return new PlanningPoker(ids.getInt(1));
      } else {
        throw new SQLException("No Ids generated");
      }
      return new PlanningPoker();
    }

    catch (SQLException e)
    {
      throw new RuntimeException(e);
    }
  }

  @Override public Task update(String header, String desc) throws SQLException
  {
    return null;
  }

  @Override public Connection getConnection() throws SQLException
  {

    return super.getConnection();
  }
}
