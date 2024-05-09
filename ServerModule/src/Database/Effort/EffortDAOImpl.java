package Database.Effort;

import DataTypes.Effort;
import DataTypes.User;
import Database.Connection.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;

public class EffortDAOImpl extends DatabaseConnection implements EffortDAO
{
  private static EffortDAOImpl instance;
  private EffortDAOImpl() throws SQLException
  {
    DriverManager.registerDriver(new org.postgresql.Driver());
  }

  public static synchronized EffortDAOImpl getInstance() throws SQLException{
    if (instance == null)
    {
      instance = new EffortDAOImpl();
    }
    return instance;
  }

  @Override public Effort readByEffort(String effort) throws SQLException
  {
    try (Connection connection = getConnection()) {
      PreparedStatement statement = connection.prepareStatement(
          "SELECT value, imagepath FROM Effort WHERE value = ?");
      statement.setString(1, effort);
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        String retrievedEffortValue = resultSet.getString("value");
        String retrievedImgPath = resultSet.getString("imagepath");
        return new Effort(retrievedEffortValue, retrievedImgPath);
      } else {
        return null;
      }
    }
  }

  @Override
  public ArrayList<Effort> getEffortList() throws SQLException {
    try (Connection connection = getConnection()) {
      PreparedStatement statement = connection.prepareStatement(
          "SELECT value, imagepath FROM Effort");
      ResultSet resultSet = statement.executeQuery();
      ArrayList<Effort> effortList = new ArrayList<>();
      while (resultSet.next()) {
        String retrievedEffortValue = resultSet.getString("value");
        String retrievedImgPath = resultSet.getString("imagepath");
        effortList.add(new Effort(retrievedEffortValue, retrievedImgPath));
      }
      return effortList;
    }
  }


  @Override public Connection getConnection() throws SQLException
  {
  return super.getConnection();
  }
}
