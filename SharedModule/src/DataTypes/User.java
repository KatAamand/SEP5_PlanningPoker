package DataTypes;

import DataTypes.UserRoles.Role;

import java.io.Serializable;

public class User implements Serializable
{
  private String username;
  private String password;
  private PlanningPoker planningPoker;
  private Role role;

  public User(String username, String password)
  {
    setUsername(username);
    setPassword(password);
  }

  public void setUsername(String userName)
  {
    this.username = userName;
  }

  public String getUsername()
  {
    return this.username;
  }

  public void setPassword(String pswd)
  {
    this.password = pswd;
  }

  public String getPassword()
  {
    return this.password;
  }

  public PlanningPoker getPlanningPoker() {
    return planningPoker;
  }

  public void setPlanningPoker(PlanningPoker planningPoker) {
    this.planningPoker = planningPoker;
    planningPoker.addUserToSession(this);
  }

  @Override public boolean equals(Object obj)
  {
    if(!(obj instanceof User))
    {
      return false;
    }
    User user = (User) obj;
    return (this.getUsername().equals(user.getUsername())
        && this.getPassword().equals(user.getPassword()));
  }


  public User copy()
  {
    return new User(this.getUsername(), this.getPassword());
  }
}
