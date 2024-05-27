package Database.PlanningPoker;

import DataTypes.PlanningPoker;
import DataTypes.Task;
import Database.Connection.DatabaseConnection;
import Database.Task.TaskDAO;
import Database.Task.TaskDAOImpl;

import javax.xml.namespace.QName;
import java.sql.*;
import java.util.ArrayList;

public class PlanningPokerDAOImpl extends DatabaseConnection implements PlanningPokerDAO {
    private static PlanningPokerDAOImpl instance;

    private PlanningPokerDAOImpl() throws SQLException {
        DriverManager.registerDriver(new org.postgresql.Driver());
    }


    public static synchronized PlanningPokerDAOImpl getInstance() throws SQLException {
        if (instance == null) {
            instance = new PlanningPokerDAOImpl();
        }
        return instance;
    }

    @Override
    public PlanningPoker create() throws SQLException {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO planning_poker DEFAULT VALUES", Statement.RETURN_GENERATED_KEYS);
            statement.executeUpdate();

            try {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int planningPokerId = generatedKeys.getInt(1);
                    return new PlanningPoker(planningPokerId);
                } else {
                    throw new SQLException("Creating planning poker failed, no ID obtained.");
                }
            } catch (SQLException e) {
                throw new SQLException("Creating planning poker failed, no ID obtained.");
            }
        }
    }

    @Override
    public PlanningPoker readByPlanningPoker(int id) throws SQLException {
        PreparedStatement statement = null;
        PreparedStatement taskListStatement = null;
        try (Connection connection = getConnection()) {
            statement = connection.prepareStatement("SELECT * FROM planning_poker WHERE planning_pokerid = ?", Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, id);
            statement.executeQuery();
            ResultSet resultSet = statement.getGeneratedKeys();

            if (resultSet.next()) {
                int planningPokerId = resultSet.getInt(1);

                taskListStatement = connection.prepareStatement("SELECT * FROM task WHERE planning_pokerid = ?");
                taskListStatement.setInt(1, planningPokerId);
                ResultSet taskListResultSet = taskListStatement.executeQuery();

                ArrayList<Task> taskList = new ArrayList<>();

                while (taskListResultSet.next()) {
                    int taskId = taskListResultSet.getInt("taskid");
                    TaskDAO taskDAO = TaskDAOImpl.getInstance();
                    Task task = taskDAO.readByTaskId(taskId);
                    taskList.add(task);
                }

                PlanningPoker planningPoker = new PlanningPoker(planningPokerId);
                planningPoker.setTaskList(taskList);

                return planningPoker;
            }
        } catch (SQLException e) {
            throw new SQLException("Failed to read planning poker");
        }

        return null;
    }

    @Override
    public ArrayList<PlanningPoker> getAllPlanningPoker() throws SQLException {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM planning_poker");
            ResultSet resultSet = statement.executeQuery();
            ArrayList<PlanningPoker> planningPokers = new ArrayList<>();

            while (resultSet.next()) {
                int planningPokerID = resultSet.getInt("planning_pokerId");
                PlanningPoker planningPokerToAdd = new PlanningPoker(planningPokerID);

                PreparedStatement taskListStatement = connection.prepareStatement("SELECT * FROM task WHERE planning_pokerid = ?");
                taskListStatement.setInt(1, planningPokerID);
                ResultSet taskListResultSet = taskListStatement.executeQuery();

                TaskDAO taskDAO = TaskDAOImpl.getInstance();
                ArrayList<Task> taskList = new ArrayList<>();

                while (taskListResultSet.next()) {
                    int taskId = taskListResultSet.getInt("taskid");
                    Task task = taskDAO.readByTaskId(taskId);
                    taskList.add(task);
                }

                planningPokerToAdd.setTaskList(taskList);
                planningPokers.add(planningPokerToAdd);
            }
            return planningPokers;
        } catch (SQLException e) {
            throw new SQLException("Failed to read planning poker");
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return super.getConnection();
    }
}
