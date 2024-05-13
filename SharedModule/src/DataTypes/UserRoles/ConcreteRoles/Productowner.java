package DataTypes.UserRoles.ConcreteRoles;

import DataTypes.UserRoles.Role;
import DataTypes.UserRoles.UserPermission;
import DataTypes.UserRoles.UserRole;

import java.util.ArrayList;
import java.util.List;

/** A Product Owner also has all the same permissions as a Developer, as well as the specific permissions required for a Product Owner. */
public class Productowner implements Role
{
  private List<UserPermission> permissions; // The specific permissions can be found in the UserRoles.UserPermission.java class. These are used when determining whether this user may or may not perform specific actions.

  public Productowner() {
    permissions = new ArrayList<>();

    // Add all the permissions the Product Owner role should have, below:
    this.copyAndApplyPermissionsFrom(new Developer());
    permissions.add(UserPermission.CREATE_TASK);
    permissions.add(UserPermission.EDIT_TASK);
    permissions.add(UserPermission.DELETE_TASK);
    permissions.add(UserPermission.IMPORT_TASKLIST);
  }

  @Override public List<UserPermission> getPermissions() {
    return permissions;
  }

  @Override public UserRole getRole() {
    return UserRole.PRODUCT_OWNER;
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
    if(!(obj instanceof Productowner))
    {
      return false;
    }
    Productowner pOwner = (Productowner) obj;
    return (this.getRole().equals(pOwner.getRole())
        && this.getPermissions().equals(pOwner.getPermissions()));
  }
}
