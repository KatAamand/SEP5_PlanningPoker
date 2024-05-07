package Database.TaskList;

import DataTypes.Task;

public interface TaskListDAO
{
  Task readByPlanningPokerId(int planningPokerId);
}
