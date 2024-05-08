package Database.Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class DatabaseConnection
{
  private final String user = "piefiptf";;
  private final String password = "gFnqYKMTdno36JWY5Fv8ZBx4oBSgXxgM";
  private final String url = "jdbc:postgresql://ella.db.elephantsql.com:5432/piefiptf/postgres?currentSchema=planningpokerschema=jdbc";

  public Connection getConnection() throws SQLException
  {
    return DriverManager.getConnection(url,user,password);
  }

}
