package DataTypes.UserRoles;

import java.util.List;

public interface Role
{
  List<UserPermission> getPermissions();
  UserRole getRole();
  List<UserPermission> copyPermissionsFrom(UserRole userRole);
}
