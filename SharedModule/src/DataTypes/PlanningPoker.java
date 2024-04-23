package DataTypes;

import DataTypes.States.PlanningPokerState;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class PlanningPoker implements Serializable
{
  private String connectionCode;
  private ArrayList<User> connectedUsers;
  private PlanningPokerState currentState;
  private PlanningPoker planningPoker;
  private String planningPokerID;
  private Chat chat;
  private static final Random random = new Random();

  public PlanningPoker()
  {
    connectedUsers = new ArrayList<>();
    chat = new Chat();
    generatePlanningPokerID();
    setPlanningPokerID(planningPokerID);
  }

  public Chat getChat()
  {
    return chat;
  }

  public ArrayList<User> getConnectedUsers()
  {
    return connectedUsers;
  }

  public void addUserToSession(User user)
  {
    connectedUsers.add(user);
  }

  public void setCurrentState(PlanningPokerState planningPokerState)
  {
    this.currentState = planningPokerState;
  }

  public PlanningPokerState getCurrentState(
      PlanningPokerState planningPokerState)
  {
    return planningPokerState;
  }

  public void declareFinalEffort(Task task)
  {
    //does nothing yet.
  }

  public void setPlanningPokerID(String planningPokerID)
  {
    this.planningPokerID = planningPokerID;
  }

  public String getPlanningPokerID()
  {
    return planningPokerID;
  }

  public void generatePlanningPokerID()
  {
    int randomNumber = random.nextInt(10000);
    this.planningPokerID = String.valueOf(randomNumber);
  }
}