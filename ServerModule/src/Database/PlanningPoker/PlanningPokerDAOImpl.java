package Database.PlanningPoker;

import DataTypes.PlanningPoker;
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

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating planning poker failed, no rows affected.");
            }

            try {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    String planningPokerId = String.valueOf(generatedKeys.getInt(1));
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
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM planning_poker WHERE planning_pokerid = ?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                PlanningPoker planningPoker = new PlanningPoker();
                planningPoker.setPlanningPokerID(String.valueOf(resultSet.getInt("planning_pokerid")));
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
            TaskDAO taskDAO = TaskDAOImpl.getInstance();
            while(resultSet.next())
            {
                int planningPokerID = resultSet.getInt("planning_pokerId");
                PlanningPoker planningPokerToAdd = new PlanningPoker(String.valueOf(planningPokerID));
                //planningPokerToAdd.setTaskList(taskDAO.readByPlanningPokerId(planningPokerID)); TODO: Figure out why this line makes client crash on startup
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
