package DataTypes.UserRoles.ConcreteRoles;

import DataTypes.UserRoles.Role;
import DataTypes.UserRoles.UserPermission;
import DataTypes.UserRoles.UserRole;

import java.util.ArrayList;
import java.util.List;


/** A Scrum Master is the game master, and holds the same permissions as a Developer as well as a set of permissions enabling game control functionality and interactions within a game. */
public class ScrumMaster implements Role
{
  private List<UserPermission> permissions; // The specific permissions can be found in the UserRoles.UserPermission.java class. These are used when determining whether this user may or may not perform specific actions.

  public ScrumMaster() {
    permissions = new ArrayList<>();

    // Add all the permissions the Scrum Master role should have, below:
    this.copyAndApplyPermissionsFrom(new Developer());
    permissions.add(UserPermission.ASSIGN_FINAL_EFFORT);
    permissions.add(UserPermission.SET_GAME_PASSWORD);
    permissions.add(UserPermission.SKIP_TASK);
  }

  @Override public List<UserPermission> getPermissions()
  {
    return permissions;
  }

  @Override public UserRole getRole() {
    return UserRole.SCRUM_MASTER;
  }

  @Override public void copyAndApplyPermissionsFrom(Role role) throws NullPointerException {
    if(role == null) {
      throw new NullPointerException();
    } else {
      List<UserPermission> permissionList = role.getPermissions();
      permissions.addAll(permissionList);
    }
  }

  @Override public boolean equals(Object obj) {
    if(!(obj instanceof ScrumMaster))
    {
      return false;
    }
    ScrumMaster scrumMaster = (ScrumMaster) obj;
    return (this.getRole().equals(scrumMaster.getRole())
        && this.getPermissions().equals(scrumMaster.getPermissions()));
  }
}
