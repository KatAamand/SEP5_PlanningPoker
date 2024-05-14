package DataTypes.UserRoles.ConcreteRoles;

import DataTypes.UserRoles.Role;
import DataTypes.UserRoles.UserPermission;
import DataTypes.UserRoles.UserRole;

import java.util.ArrayList;
import java.util.List;

/** A Developer is the basic most low-level role that a user in the application can hold. It contains the bare essential permissions for the general interactions with the application as a user. */
public class Developer implements Role
{
  private List<UserPermission> permissions; // The specific permissions can be found in the UserRoles.UserPermission.java class. These are used when determining whether this user may or may not perform specific actions.

  public Developer() {
    permissions = new ArrayList<>();

    // Add all the permissions the developer role should have, below:
    permissions.add(UserPermission.EXPORT_TASKLIST);
  }

  @Override public List<UserPermission> getPermissions() {
    return permissions;
  }

  @Override public UserRole getRole() {
    return UserRole.DEVELOPER;
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
    if(!(obj instanceof Developer))
    {
      return false;
    }
    Developer dev = (Developer) obj;
    return (this.getRole().equals(dev.getRole())
        && this.getPermissions().equals(dev.getPermissions()));
  }
}
