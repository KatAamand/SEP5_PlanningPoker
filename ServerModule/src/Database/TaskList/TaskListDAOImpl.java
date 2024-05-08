package Database.TaskList;

import DataTypes.Task;
import Database.Connection.DatabaseConnection;
import Database.Effort.EffortDAOImpl;

import java.sql.*;

public class TaskListDAOImpl extends DatabaseConnection implements TaskListDAO
{
  private static TaskListDAOImpl instance;

  private TaskListDAOImpl() throws SQLException
  {
    DriverManager.registerDriver(new org.postgresql.Driver());
  }

  public static synchronized TaskListDAOImpl getInstance() throws SQLException
  {
    if (instance == null)
    {
      instance = new TaskListDAOImpl();
    }
    return instance;
  }

  @Override public TaskListDAOImpl readByPlanningPokerId(int planningPokerId)
      throws SQLException
  {
    try (Connection connection = getConnection())
    {
      PreparedStatement statement = connection.prepareStatement("SELECT * FROM TaskList WHERE planningPokerId = ?")
    statement.setInt(1, planningPokerId);
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next())
      {
        String taskId = resultSet.getString("taskId");
        return new TaskListDAOImpl(planningPokerId, taskId)
      }
    }
  }

  @Override public Connection getConnection() throws SQLException
  {
    return null;
  }
}
