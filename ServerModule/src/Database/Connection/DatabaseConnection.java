package Database.Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class DatabaseConnection
{
  private String user;
  private String password;
  private String url;

  public Connection getConnection() throws SQLException
  {
    return DriverManager.getConnection("jdbc:postgresql://ella.db.elephantsql.com:5432/piefiptf","piefiptf","gFnqYKMTdno36JWY5Fv8ZBx4oBSgXxgM");
  }

}
