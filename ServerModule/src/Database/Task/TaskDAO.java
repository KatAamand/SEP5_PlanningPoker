package Database.Task;

import DataTypes.Task;

public interface TaskDAO
{
  Task create(String header, String desc);
  Task update(String header, String desc);
}
