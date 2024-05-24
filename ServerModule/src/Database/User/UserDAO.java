package Database.User;

import DataTypes.User;

import java.sql.SQLException;
import java.util.ArrayList;

public interface UserDAO
{
  /**
   * Creates a new user with the specified username and password.
   * @param username the username of the user.
   * @param password the password of the user.
   * @return the created user object.
   * @throws SQLException if there is an error accessing the database.
   */
  User create(String username, String password) throws SQLException;

  /**
   * Retrieves a user by its username and password from the database.
   * @param username the username of the user.
   * @param password the password of the user.
   * @return the user object, or null if not found.
   * @throws SQLException if there is an error accessing the database.
   */
  User readByLoginInfo(String username, String password) throws SQLException;

  /**
   * Retrieves all User objects from the database.
   * @return an ArrayList<User> containing all users.
   * @throws SQLException if there is an error accessing the database.
   */
  ArrayList<User> getAllUsers() throws SQLException;
}
