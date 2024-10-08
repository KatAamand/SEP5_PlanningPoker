package DataTypes.UserRoles.ConcreteRoles;

import DataTypes.UserRoles.Role;
import DataTypes.UserRoles.UserPermission;
import DataTypes.UserRoles.UserRole;

import java.util.ArrayList;
import java.util.List;


/** A Scrum Master is the game master, and holds the same permissions as a Developer as well as a set of permissions enabling game control functionality and interactions within a game. */
public class ScrumMaster implements Role
{
  private final UserRole role = UserRole.SCRUM_MASTER;
  private final String roleAsString = "Scrum Master";
  private List<UserPermission> permissions; // The specific permissions can be found in the UserRoles.UserPermission.java class. These are used when determining whether this user may or may not perform specific actions.

  public ScrumMaster() {
    permissions = new ArrayList<>();

    // Add all the permissions the Scrum Master role should have, below:
    this.copyAndApplyPermissionsFrom(new Developer());
    permissions.add(UserPermission.ASSIGN_FINAL_EFFORT);
    permissions.add(UserPermission.SET_GAME_PASSWORD);
    permissions.add(UserPermission.SKIP_TASK);
    permissions.add(UserPermission.EDIT_TASK); // Required, in order to properly be able to set the final effort on a task - which is basically editing an existing task.
    permissions.add(UserPermission.ASSIGN_TEAM_ROLES);
    permissions.add(UserPermission.REVEAL_USER_EFFORTS);
    permissions.add(UserPermission.CLEAR_USER_EFFORTS);
    permissions.add(UserPermission.START_PLANNINGPOKER_GAME);
  }

  @Override public List<UserPermission> getPermissions()
  {
    return permissions;
  }

  @Override public UserRole getUserRole() {
    return role;
  }

  @Override public void copyAndApplyPermissionsFrom(Role role) throws NullPointerException {
    if(role == null) {
      throw new NullPointerException();
    } else {
      List<UserPermission> permissionList = role.getPermissions();
      permissions.addAll(permissionList);
    }
  }

  @Override public String getRoleAsString()
  {
    return roleAsString;
  }

  @Override public boolean equals(Object obj) {
    if(!(obj instanceof ScrumMaster))
    {
      return false;
    }
    ScrumMaster scrumMaster = (ScrumMaster) obj;
    return (this.getUserRole().equals(scrumMaster.getUserRole())
        && this.getPermissions().equals(scrumMaster.getPermissions()));
  }
}
