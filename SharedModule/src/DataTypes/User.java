package DataTypes;

import java.io.Serializable;

public class User implements Serializable
{
  private String userName;
  private String password;
  private Session session;

  public User(String userName, String password)
  {
    setUserName(userName);
    setPassword(password);
  }

  public void setUserName(String userName)
  {
    this.userName = userName;
  }

  public String getUserName()
  {
    return this.userName;
  }


  public void setPassword(String pswd)
  {
    this.password = pswd;
  }

  public String getPassword()
  {
    return this.password;
  }


  @Override public boolean equals(Object obj)
  {
    if(!(obj instanceof User))
    {
      return false;
    }
    User user = (User) obj;
    return (this.getUserName().equals(user.getUserName())
        && this.getPassword().equals(user.getPassword()));
  }


  public User copy()
  {
    return new User(this.getUserName(), this.getPassword());
  }
}
