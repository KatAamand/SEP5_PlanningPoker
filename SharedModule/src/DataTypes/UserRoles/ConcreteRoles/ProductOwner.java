package DataTypes.UserRoles.ConcreteRoles;

import DataTypes.UserRoles.Role;
import DataTypes.UserRoles.UserPermission;
import DataTypes.UserRoles.UserRole;

import java.util.List;

public class ProductOwner implements Role
{
  @Override public List<UserPermission> getPermissions()
  {
    //TODO
    return null;
  }

  @Override public UserRole getRole()
  {
    //TODO
    return null;
  }

  @Override public List<UserPermission> copyPermissionsFrom(UserRole userRole)
  {
    //TODO
    return null;
  }
}
