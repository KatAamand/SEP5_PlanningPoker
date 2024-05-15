package Database.Task;

import DataTypes.Task;

import java.sql.SQLException;
import java.util.ArrayList;

public interface TaskDAO
{
  Task create(String header, String desc, int planningPokerId) throws SQLException;
  void update(Task task) throws SQLException;
  ArrayList<Task> readByPlanningPokerId(int planningPokerId) throws SQLException;
  Task readByTaskId(int id) throws SQLException;
  void delete(Task task) throws SQLException;
}
