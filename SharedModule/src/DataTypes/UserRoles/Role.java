package DataTypes.UserRoles;

import java.io.Serializable;
import java.util.List;

public interface Role extends Serializable
{
  List<UserPermission> getPermissions();
  UserRole getUserRole();
  void copyAndApplyPermissionsFrom(Role role);
  String getRoleAsString();
}
