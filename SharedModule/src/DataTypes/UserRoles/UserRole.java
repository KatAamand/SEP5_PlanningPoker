package DataTypes.UserRoles;

/** Defines the possible roles that can be assigned to a user */
public enum UserRole
{
  DEVELOPER, // Assigned to all users,
  SCRUM_MASTER, // Assigned to the project manager / game host. Is responsible for the estimation game.
  PRODUCT_OWNER, // Assigned to the player responsible for the product details/technicalities.
  ADMIN // Can perform all operations that exist in the application
}
