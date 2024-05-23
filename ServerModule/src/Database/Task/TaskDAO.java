package Database.Task;

import DataTypes.Task;

import java.sql.SQLException;
import java.util.ArrayList;

public interface TaskDAO
{
  /**
   * Creates a new task with the specified header, description, and associated Planning Poker ID.
   * @param header the header of the task.
   * @param desc the description of the task.
   * @param planningPokerId the ID of the Planning Poker game the task is associated with.
   * @return the created Task object.
   * @throws SQLException if there is an error accessing the database.
   */
  Task create(String header, String desc, int planningPokerId) throws SQLException;

  /**
   * Updates the specified task in the database.
   * @param task the task to update.
   * @throws SQLException if there is an error accessing the database.
   */
  void update(Task task) throws SQLException;

  /**
   * Retrieves all tasks associated with the specified Planning Poker ID from the database.
   * @param planningPokerId the ID of the Planning Poker game to retrieve tasks for.
   * @return an ArrayList<Task> containing the tasks.
   * @throws SQLException if there is an error accessing the database.
   */
  ArrayList<Task> readByPlanningPokerId(int planningPokerId) throws SQLException;

  /**
   * Retrieves a task by its ID from the database.
   * @param id the ID of the task to retrieve.
   * @return the Task object, or null if not found.
   * @throws SQLException if there is an error accessing the database.
   */
  Task readByTaskId(int id) throws SQLException;

  /**
   * Deletes the specified task from the database.
   * @param task the task to delete.
   * @throws SQLException if there is an error accessing the database.
   */
  void delete(Task task) throws SQLException;

}
