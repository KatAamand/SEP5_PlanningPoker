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
            taskStatement = connection.prepareStatement("INSERT INTO task(header, \"desc\", planning_pokerid) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            taskStatement.setString(1, header);
            taskStatement.setString(2, desc);
            taskStatement.setString(3, String.valueOf(planningPokerId));
            taskStatement.executeUpdate();

            ResultSet generatedKeys = taskStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int taskId = generatedKeys.getInt(1);
                return new Task(taskId, header, desc);
            } else {
                throw new SQLException("Creating task failed, no ID obtained.");
            }
        }
    }

    @Override
    public void update(Task task) throws SQLException {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("UPDATE task SET header = ?, \"desc\" = ?, final_effort = ? WHERE taskid = ?");
            statement.setString(1, task.getTaskHeader());
            statement.setString(2, task.getDescription());
            statement.setString(3, task.getFinalEffort());
            statement.setInt(4, task.getTaskID());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("Task not found with ID: " + task.getTaskID());
            }
        }
    }

    @Override
    public Task readByTaskId(int taskId) throws SQLException {
        try (Connection connection = super.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM task WHERE taskid = ?");
            statement.setInt(1, taskId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String header = resultSet.getString("header");
                String description = resultSet.getString("desc");
                String finalEffort = resultSet.getString("final_effort"); // Assuming you have this column in the table
                return new Task(taskId, header, description, finalEffort); // Adjust Task constructor if necessary
            } else {
                return null; // Task not found
            }
        }
    }

    @Override
    public ArrayList<Task> readByPlanningPokerId(int planningPokerId) throws SQLException {
        try (Connection connection = super.getConnection()) {
            ArrayList<Task> tasks = new ArrayList<>();

            PreparedStatement statement = connection.prepareStatement("SELECT taskid, header, \"desc\", final_effort FROM task WHERE planning_pokerid = ?");
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
