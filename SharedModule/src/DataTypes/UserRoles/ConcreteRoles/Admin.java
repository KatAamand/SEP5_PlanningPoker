package DataTypes.UserRoles.ConcreteRoles;

import DataTypes.UserRoles.Role;
import DataTypes.UserRoles.UserPermission;
import DataTypes.UserRoles.UserRole;

import java.util.ArrayList;
import java.util.List;

public class Admin implements Role
{
  private final UserRole role = UserRole.ADMIN;
  private final String roleAsString = "Admin";
  private List<UserPermission> permissions; // The specific permissions can be found in the UserRoles.UserPermission.java class. These are used when determining whether this user may or may not perform specific actions.

  public Admin() {
    permissions = new ArrayList<>();

    // Add all the permissions the Admin role should have, below:
    this.copyAndApplyPermissionsFrom(new ProductOwner());
    this.copyAndApplyPermissionsFrom(new ScrumMaster());
  }

  @Override public List<UserPermission> getPermissions() {
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

  @Override public String getRoleAsString() {
    return roleAsString;
  }

  @Override public boolean equals(Object obj) {
    if(!(obj instanceof Admin))
    {
      return false;
    }
    Admin admin = (Admin) obj;
    return (this.getUserRole().equals(admin.getUserRole())
        && this.getPermissions().equals(admin.getPermissions()));
  }
}
