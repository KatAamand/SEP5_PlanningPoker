package Database.Task;

import DataTypes.Task;

import java.sql.SQLException;

public interface TaskDAO
{
  Task create(String header, String desc) throws SQLException;
  Task update(String header, String desc) throws SQLException;
}
