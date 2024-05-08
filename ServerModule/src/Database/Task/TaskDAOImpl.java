package Database.Task;

import DataTypes.Effort;
import DataTypes.PlanningPoker;
import DataTypes.Task;
import Database.Connection.DatabaseConnection;
import Database.Effort.EffortDAOImpl;

import java.sql.*;
import java.util.ArrayList;

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
    try (Connection connection = getConnection()) {
      PreparedStatement statement = connection.prepareStatement("INSERT INTO task(header, \"desc\") VALUES (?, ?)");
      statement.setString(1, header);
      statement.setString(2, desc);
      statement.executeUpdate();

      return new Task(header, desc);
    }
  }




  @Override public Task update(String header, String desc) throws SQLException
  {
    return null;
  }

  @Override
  public ArrayList<Task> readByPlanningPokerId(int planningPokerId) throws SQLException {
    try (Connection connection = super.getConnection()) {
        PreparedStatement statement = connection.prepareStatement("SELECT header, \"desc\", final_effort FROM task INNER JOIN task_list tl ON task.taskid = tl.taskid WHERE planning_pokerid = ?");
        statement.setInt(1, planningPokerId);
        ResultSet resultSet = statement.executeQuery();
        ArrayList<Task> tasks = new ArrayList<>();

        while (resultSet.next()) {
            String header = resultSet.getString("header");
            String description = resultSet.getString("desc");
            String finalEffort = resultSet.getString("final_effort");
            Task task = new Task(header, description, finalEffort);
            tasks.add(task);
        }
        return tasks;
    }

  }

  @Override public Connection getConnection() throws SQLException
  {

    return super.getConnection();
  }
}
