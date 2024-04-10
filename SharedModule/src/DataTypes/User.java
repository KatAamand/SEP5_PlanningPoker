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
    setCurrentSession(null);
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



  public void setCurrentSession(Session session)
  {
    this.session = session;
  }

  public Session getCurrentSession()
  {
    return this.session;
  }


  public void WriteMessage(Message message)
  {
    //TODO
  }



  public void joinSession(Session session, String connectionCode)
  {
    //TODO
  }



  public void createTask(Task task)
  {
    //TODO
  }


  public void estimateEffort(String effortValue)
  {
    //TODO
  }



  @Override public boolean equals(Object obj)
  {
    if(!(obj instanceof User))
    {
      return false;
    }
    User user = (User) obj;
    return (this.getUserName().equals(user.getUserName())
        && this.getPassword().equals(user.getPassword())
        && this.getCurrentSession() == user.getCurrentSession());
  }
}
