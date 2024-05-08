package Database.TaskList;

import DataTypes.Task;

import java.sql.SQLException;

public interface TaskListDAO
{
  Task readByPlanningPokerId(int planningPokerId) throws SQLException;
}
