package Database.Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Controls the general connection to the database.
 * Handles inserting user, password and url inherited by subclasses performing as entities in database.
 **/
public abstract class DatabaseConnectionUser
{
  private final String user = "piefiptf";;
  private final String password = "gFnqYKMTdno36JWY5Fv8ZBx4oBSgXxgM";
  private final String url = "jdbc:postgresql://ella.db.elephantsql.com:5432/piefiptf?currentSchema=planningpokeruserschema";

  public Connection getConnection() throws SQLException
  {
    return DriverManager.getConnection(url,user,password);
  }

}
