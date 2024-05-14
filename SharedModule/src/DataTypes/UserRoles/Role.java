package DataTypes.UserRoles;

import java.io.Serializable;
import java.util.List;

public interface Role extends Serializable
{
  List<UserPermission> getPermissions();
  UserRole getRole();
  void copyAndApplyPermissionsFrom(Role role);
}
