package DataTypes;

import DataTypes.UserRoles.ConcreteRoles.Admin;
import DataTypes.UserRoles.ConcreteRoles.Developer;
import DataTypes.UserRoles.Role;

import java.io.Serializable;

public class User implements Serializable
{
  private String username;
  private String password;
  private PlanningPoker planningPoker;
  private Role role;
  private Role adminRole; // Is null, if the user is not an Admin - otherwise is the UserRole 'Admin' if the user is an admin.

  public User(String username, String password) {
    setUsername(username);
    setPassword(password);
    setRole(new Developer());
    adminRole = null;
  }

  public void setUsername(String userName) {
    this.username = userName;
  }

  public String getUsername() {
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
    if(planningPoker != null) {
      this.planningPoker = planningPoker;

      boolean userNotFound = true;
      for (User user : planningPoker.getConnectedUsers()) {
        if(user.getUsername().equals(this.getUsername())) {
          userNotFound = false;
        }
      }

      if(userNotFound) {
        planningPoker.addUserToSession(this);
      }
    }
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

  public String getRoleAsString() {
    return role.getRoleAsString();
  }

  public Role getAdmin() {
    return this.adminRole;
  }

  public void setAdmin(Role adminRole) {
    this.adminRole = adminRole;
  }

  @Override public boolean equals(Object obj) {
    if(!(obj instanceof User))
    {
      return false;
    }
    User user = (User) obj;
    if(this.getAdmin() != null && user.getAdmin() != null) {
      return (this.getUsername().equals(user.getUsername())
          && this.getPassword().equals(user.getPassword())
          && this.getRole().equals(user.getRole())
          && this.getAdmin().equals(user.getAdmin()));
    } else if ((this.getAdmin() == null && user.getAdmin() != null) || this.getAdmin() != null && user.getAdmin() == null)
      return false;
    else {
      return (this.getUsername().equals(user.getUsername())
          && this.getPassword().equals(user.getPassword())
          && this.getRole().equals(user.getRole()));
    }
  }

  public User copy() {
    User newUser = new User(this.getUsername(), this.getPassword());
    newUser.setRole(this.getRole());
    newUser.setAdmin(this.getAdmin());
    return newUser;
  }
}
