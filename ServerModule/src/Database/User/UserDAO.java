package Database.User;

import DataTypes.User;

import java.sql.SQLException;
import java.util.ArrayList;

public interface UserDAO
{
  User create(String username, String password) throws SQLException;
  User readByLoginInfo(String username, String password) throws SQLException;
  /**
   * Retrieves all User objects from the database.
   * @return an ArrayList<User> containing all users.
   * @throws SQLException if there is an error accessing the database.
   */
  ArrayList<User> getAllUsers() throws SQLException;

}
