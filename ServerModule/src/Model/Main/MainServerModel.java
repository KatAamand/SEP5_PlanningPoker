package Model.Main;

import DataTypes.PlanningPoker;
import DataTypes.User;
import DataTypes.UserRoles.Role;
import DataTypes.UserRoles.UserRole;
import Networking.ClientConnection_RMI;
import Networking.ServerConnection_RMI;
import Util.PropertyChangeSubject;

import java.util.ArrayList;
import java.util.Map;

public interface MainServerModel extends PropertyChangeSubject
{
  boolean validatePlanningPoker(String planningPokerID);
  PlanningPoker createPlanningPoker();
  PlanningPoker getPlanningPokerGame(String planningPokerId);
  void getAllPlanningPokersFromDb();
  User applyPlanningPokerRole(UserRole role, User user, String gameId);
  void broadcastPlanningPokerObjUpdate(Map<String, ArrayList<ClientConnection_RMI>> clientList, ServerConnection_RMI server, String gameId);
}
