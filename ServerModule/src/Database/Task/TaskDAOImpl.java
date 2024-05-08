package Database.Task;

import DataTypes.Task;
import Database.Connection.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;

public class TaskDAOImpl extends DatabaseConnection implements TaskDAO {
    private static TaskDAOImpl instance;

    private TaskDAOImpl() throws SQLException {
        DriverManager.registerDriver(new org.postgresql.Driver());
    }

    public static synchronized TaskDAOImpl getInstance() throws SQLException {
        if (instance == null) {
            instance = new TaskDAOImpl();
        }
        return instance;
    }

    @Override
    public Task create(String header, String desc, int planningPokerId) throws SQLException {
        PreparedStatement taskStatement = null;
        PreparedStatement taskListStatement = null;

        try (Connection connection = getConnection()) {
            taskStatement = connection.prepareStatement("INSERT INTO task(header, \"desc\") VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            taskStatement.setString(1, header);
            taskStatement.setString(2, desc);
            taskStatement.executeUpdate();

            ResultSet generatedKeys = taskStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int taskId = generatedKeys.getInt(1);

                taskListStatement = connection.prepareStatement("INSERT INTO task_list(taskid, planning_pokerid) VALUES (?, ?)");
                taskListStatement.setInt(1, taskId);
                taskListStatement.setInt(2, planningPokerId);
                taskListStatement.executeUpdate();

                return new Task(taskId, header, desc);
            } else {
                throw new SQLException("Creating task failed, no ID obtained.");
            }
        }
    }

    @Override
    public void update(Task task) throws SQLException {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("UPDATE task SET header = ?, \"desc\" = ? WHERE taskid = ?");
            statement.setString(1, task.getTaskHeader());
            statement.setString(2, task.getDescription());
            statement.setInt(3, task.getTaskID());
            statement.executeUpdate();
        }
    }

    @Override
    public ArrayList<Task> readByPlanningPokerId(int planningPokerId) throws SQLException {
        try (Connection connection = super.getConnection()) {
            ArrayList<Task> tasks = new ArrayList<>();

            PreparedStatement statement = connection.prepareStatement("SELECT task.taskid, header, \"desc\", final_effort FROM task INNER JOIN task_list tl ON task.taskid = tl.taskid WHERE planning_pokerid = ?");
            statement.setInt(1, planningPokerId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int taskId = resultSet.getInt("taskid");
                String header = resultSet.getString("header");
                String description = resultSet.getString("desc");
                String finalEffort = resultSet.getString("final_effort");
                Task task = new Task(taskId, header, description, finalEffort);
                tasks.add(task);
            }

            return tasks;
        }

    }

    @Override
    public void delete(Task task) throws SQLException {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM task WHERE taskid = ?");
            statement.setInt(1, task.getTaskID());
            statement.executeUpdate();
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return super.getConnection();
    }
}
