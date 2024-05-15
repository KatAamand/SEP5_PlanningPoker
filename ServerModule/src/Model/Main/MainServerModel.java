package Model.Main;

import DataTypes.PlanningPoker;
import DataTypes.User;
import DataTypes.UserRoles.Role;
import DataTypes.UserRoles.UserRole;
import Networking.ClientConnection_RMI;
import Networking.ServerConnection_RMI;
import Networking.Server_RMI;
import Util.PropertyChangeSubject;

import java.util.ArrayList;
import java.util.Map;

public interface MainServerModel extends PropertyChangeSubject
{
  boolean validatePlanningPoker(int planningPokerID);
  PlanningPoker createPlanningPoker();
  PlanningPoker getPlanningPokerGame(int planningPokerId);
  void getAllPlanningPokersFromDb();
  User applyPlanningPokerRole(UserRole role, User user, int planningPokerId);
  void broadcastPlanningPokerObjUpdate(Map<Integer, ArrayList<ClientConnection_RMI>> clientList, ServerConnection_RMI server, int planningPokerId);
  void setProductOwner(User user, ArrayList<ClientConnection_RMI> connectedClients, Server_RMI serverRmi);
}
