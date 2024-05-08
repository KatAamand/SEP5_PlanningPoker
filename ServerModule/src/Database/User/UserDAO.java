package Database.User;

import DataTypes.User;

import java.sql.SQLException;

public interface UserDAO
{
  User create(String username, String password) throws SQLException;
  User readByLoginInfo(String username, String password) throws SQLException;
}
