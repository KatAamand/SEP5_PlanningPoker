package Database.Connection;

import java.sql.Connection;

public abstract class DatabaseConnection
{
  private String user;
  private String password;
  private String url;

  public abstract Connection getConnection();

}
