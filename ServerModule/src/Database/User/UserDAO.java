package Database.User;

import DataTypes.User;

import java.sql.SQLException;
import java.util.ArrayList;

public interface UserDAO
{
  User create(String username, String password) throws SQLException;
  User readByLoginInfo(String username, String password) throws SQLException;
  ArrayList<User> getAllUsers() throws SQLException;
}
