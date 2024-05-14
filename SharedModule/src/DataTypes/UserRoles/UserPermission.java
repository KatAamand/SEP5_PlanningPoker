package DataTypes.UserRoles;

/** Defines all the possible permissions that a role can be tied to. These are needed to perform key actions while interacting with the application */
public enum UserPermission
{
  CREATE_TASK,
  EDIT_TASK,
  DELETE_TASK,
  SKIP_TASK,
  ASSIGN_FINAL_EFFORT,
  ASSIGN_TEAM_ROLES,
  SET_GAME_PASSWORD,
  EXPORT_TASKLIST,
  IMPORT_TASKLIST
}
