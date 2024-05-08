package Database.PlanningPoker;

import DataTypes.PlanningPoker;
import DataTypes.User;
import Database.Connection.DatabaseConnection;
import Database.Effort.EffortDAOImpl;

import javax.xml.namespace.QName;
import java.sql.*;

public class PlanningPokerDAOImpl extends DatabaseConnection
    implements PlanningPokerDAO
{
  private static PlanningPokerDAOImpl instance;

  private PlanningPokerDAOImpl() throws SQLException
  {
    DriverManager.registerDriver(new org.postgresql.Driver());
  }


  public static synchronized PlanningPokerDAOImpl getInstance() throws SQLException{
    if (instance == null)
    {
      instance = new PlanningPokerDAOImpl();
    }
    return instance;
  }


  @Override public PlanningPoker create()
  {
    try (Connection connection = getConnection())

    {
      PreparedStatement statement = connection.prepareStatement(
          "insert into PlanningPoker() values (?)",
          PreparedStatement.RETURN_GENERATED_KEYS);
      statement.executeUpdate();
      ResultSet ids = statement.getResultSet();
      if (ids.next())
      {
        return new PlanningPoker(ids.getInt(1));
      }
      else
      {
        throw new SQLException("No Ids generated");
      }
      return new PlanningPoker();
    }

    catch (SQLException e)
    {
      throw new RuntimeException(e);
    }
  }

  @Override public PlanningPoker readByPlanningPoker(int id)
  {
    try (Connection connection = getConnection())

    {
      PreparedStatement statement = connection.prepareStatement(
          "SELECT * from PlanningPoker where id = ?");
      statement.setInt(1, id);
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next())
      {
        String name = resultSet
      } statement.executeUpdate();
      ResultSet ids = statement.getResultSet();
      if (ids.next())
      {
        return new PlanningPoker(ids.getInt(1));
      }
      else
      {
        throw new SQLException("No Ids generated");
      }
      return new PlanningPoker();
    }

    catch (SQLException e)
    {
      throw new RuntimeException(e);
    }
  }

  @Override public Connection getConnection() throws SQLException
  {

    return super.getConnection();
  }
}
