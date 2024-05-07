package Database.User;

import DataTypes.User;

public interface UserDAO
{
  User create(String username, String password);
  User readByLoginInfo(String username, String password);
}
