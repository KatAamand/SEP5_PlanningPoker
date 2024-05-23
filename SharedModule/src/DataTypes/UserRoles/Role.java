package DataTypes.UserRoles;

import java.io.Serializable;
import java.util.List;

/** This interface defines the key methods related to UserRoles. It is utilized in the context of the Strategy design Pattern,
 * and enables different responses/actions depending on the specific implementation of this Role interface  <br>
 */
public interface Role extends Serializable
{

  /** Returns a list containing all the UserPermission's that this Role contains.
   * @return A list of UserPermissions, specifying the access level of this Role.
   */
  List<UserPermission> getPermissions();


  /** Returns the UserRole attached to the specific implementation of this Role interface.
   * @return A UserRole.
   */
  UserRole getUserRole();


  /** Copies all UserPermissions from the specified Role and adds these to this specific Role implementations list of UserPermissions.
   * @param role The Role to copy permissions from.
   */
  void copyAndApplyPermissionsFrom(Role role);


  /** Returns the name of this Role as a String
   * @return A String containing the name of the role.
   */
  String getRoleAsString();
}
