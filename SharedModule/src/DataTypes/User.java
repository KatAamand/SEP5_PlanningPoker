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

    // Added to only check users against their unique usernames, instead of the entire user object.
    boolean userNotFound = true;
    for (User user : planningPoker.getConnectedUsers()) {
      if(user.getUsername().equals(this.getUsername())) {
        userNotFound = false;
      }
    }

    if(userNotFound) {
      planningPoker.addUserToSession(this);
    }
    // Commented out below, and added above since the below would return different users despite the only difference being the role applied to the specific user.
    /*if (planningPoker != null && !planningPoker.getConnectedUsers().contains(this)) {
      planningPoker.addUserToSession(this);
    }*/
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

  public String getRoleAsString()
  {
    return role.getRoleAsString();
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
