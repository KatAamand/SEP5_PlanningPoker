package Model.Main;

import DataTypes.PlanningPoker;
import DataTypes.User;
import DataTypes.UserRoles.UserRole;
import Networking.ClientConnection_RMI;
import Networking.ServerConnection_RMI;
import Networking.Server_RMI;
import Util.PropertyChangeSubject;
import java.util.ArrayList;
import java.util.Map;


/**
 * The MainServerModel interface defines all the key methods relating to handling the server-side logic of Planning Poker
 * game creation/load/validation as well as applying UserRoles to users in games and broadcasting changes made to
 * Planning Poker game objects to all connected users in each game. It extends the PropertyChangeSubject interface and is thus prepared for use with Observer Pattern principles.
 */
public interface MainServerModel extends PropertyChangeSubject
{

  /**
   * Validates the existence of a Planning Poker game with the specified ID.
   * @param planningPokerID the ID of the Planning Poker game to validate.
   * @return true if the game exists, false otherwise.
   */
  boolean validatePlanningPoker(int planningPokerID);


  // TODO: Source code comment/document
  PlanningPoker createPlanningPoker();


  /**
   * Returns the Planning Poker object specified by the given Planning Poker id, if such an object can be found.
   * @param planningPokerId The Game ID for the Planning Poker game, which should be returned.
   * @return A NON-NULL Planning Poker object, if such an object was found as specified by the Planning Poker id. Otherwise returns <code>NULL</code>
   */
  PlanningPoker getPlanningPokerGame(int planningPokerId);

  /** Retrieves all saved planning poker session from the database and saves them in the model*/
  void getAllPlanningPokersFromDb();


  /**
   * Applies the given UserRole to the given User, if the User is found to be a part of the users/clients currently connected in the Planning Poker game specified by the given game id.<br>
   * If the specified user already has another key role (i.e. the User is Product Owner, but we now assign them as Scrum Master), then this method will ensure the proper unassignment of the previous role,
   * as well as ensure that the specified Planning Poker object is also updated to reflect the new role assignments.
   * @param role The UserRole that should be applied to the specified user.
   * @param user The User, for which the specified UserRole should be applied.
   * @param planningPokerId The Game ID for the Planning Poker game, to which the specified User should be connected.
   * @return A NON-NULL User object, if such a User was in the Planning Poker game as specified by the Planning Poker id. Otherwise returns <code>NULL</code>
   */
  User applyPlanningPokerRole(UserRole role, User user, int planningPokerId);


  /** Broadcasts the Planning Poker game object with the specified game id, if such a game exists. Else it does nothing.<br>
   * If connection cannot be established with a client,this method will forward a request to the server to unregister the problematic client in order to reduce server lag and server crashing.
   * @param clientList A reference to the Map containing the list of connected clients to each Planning Poker game.
   * @param planningPokerId The Game ID for the Planning Poker game, inside which to find connected users and broadcast the skipped task list to.
   * @param server A reference to the server object responsible for removing clients where connectivity issues occur (disconnect, etc.).
   */
  void broadcastPlanningPokerObjUpdate(Map<Integer, ArrayList<ClientConnection_RMI>> clientList, ServerConnection_RMI server, int planningPokerId);

  /** Sets the product owner for the users planning poker session
   * @param user the user that is intended to become product owner
   * @param connectedClients all the clients that are connected to the server
   * @param serverRmi the server which has received the request*/
  void setProductOwner(User user, ArrayList<ClientConnection_RMI> connectedClients, Server_RMI serverRmi);


  /**
   * Removes the specified user from the Planning Poker game with the specified id, if such a user already exists in the specified game.<br>
   * If the specified user already has another key role (i.e. the User is Product Owner), then this method will ensure that such users are reverted to Developers,
   * as well as ensure that the specified Planning Poker object is also updated to reflect the new role assignments. This allows still connected users to grab the new vacant key roles.
   * @param user The User, whom should be removed from the specified game.
   * @param planningPokerId The Game ID for the Planning Poker game, to which the specified User should be connected.
   * @return True, if the specified user was successfully removed from the specified game. Else returns False.
   */
  boolean removeUserFromGame(User user, int planningPokerId);


  /** Verifies that connection with the clients who each own the users listed inside the Planning Poker game with the specified game id, can still be established.<br>
   * If a user is found inside the specified Planning Poker game object, with whom a matching clients User cannot be found,
   * then this method will ensure that these derelict users are removed from the game, and changes are broadcast to each connected client in the game.<br>
   * If connection cannot be established with a client,this method will forward a request to the server to unregister the problematic client in order to reduce server lag and server crashing.
   * @param clientList A reference to the Map containing the list of connected clients to each Planning Poker game.
   * @param planningPokerId The Game ID for the Planning Poker game, inside which to find connected users and broadcast the skipped task list to.
   * @param server A reference to the server object responsible for removing clients where connectivity issues occur (disconnect, etc.).
   */
  void verifyConnectedUsersIntegrity(Map<Integer, ArrayList<ClientConnection_RMI>> clientList, ServerConnection_RMI server, int planningPokerId);
}
