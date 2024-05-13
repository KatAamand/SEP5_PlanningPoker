package DataTypes;

import DataTypes.UserRoles.ConcreteRoles.Developer;
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
    setRole(new Developer());
  }

  public void setUsername(String userName)
  {
    this.username = userName;
  }

  public String getUsername()
  {
    return this.username;
  }

  public void setPassword(String pswd) {
    this.password = pswd;
  }

  public String getPassword() {
    return this.password;
  }

  public PlanningPoker getPlanningPoker() {
    return planningPoker;
  }

  public void setPlanningPoker(PlanningPoker planningPoker) {
    this.planningPoker = planningPoker;
    planningPoker.addUserToSession(this);
  }

  public Role getRole() {
    return this.role;
  }

  public void setRole(Role role) {
    if(role == null) {
      throw new NullPointerException();
    } else {
      this.role = role;
    }
  }

  @Override public boolean equals(Object obj) {
    if(!(obj instanceof User))
    {
      return false;
    }
    User user = (User) obj;
    return (this.getUsername().equals(user.getUsername())
        && this.getPassword().equals(user.getPassword())
        && this.getRole().equals(user.getRole()));
  }

  public User copy() {
    User newUser = new User(this.getUsername(), this.getPassword());
    newUser.setRole(this.getRole());
    return newUser;
  }
}
