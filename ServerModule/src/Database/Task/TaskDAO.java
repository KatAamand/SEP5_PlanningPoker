package Database.Task;

import DataTypes.Task;

import java.sql.SQLException;
import java.util.ArrayList;

public interface TaskDAO
{
  Task create(String header, String desc) throws SQLException;
  Task update(String header, String desc) throws SQLException;
  ArrayList<Task> readByPlanningPokerId(int planningPokerId) throws SQLException;
}
