package DataTypes;

import DataTypes.States.PlanningPokerState;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlanningPoker implements Serializable
{
  //private String connectionCode; TODO: Remove line if unnecessary
  private ArrayList<User> connectedUsers;
  private PlanningPokerState currentState;
  //private PlanningPoker planningPoker; TODO: Remove line if unnecessary
  private String planningPokerID;
  private Chat chat;
  private static final Random random = new Random();
  private List<Task> taskList;

  public PlanningPoker()
  {
    connectedUsers = new ArrayList<>();
    chat = new Chat();
    taskList = new ArrayList<>();
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

  public void addUserToSession(User user) throws NullPointerException
  {
    if(user == null) {
      throw new NullPointerException();
    }

    if(!connectedUsers.contains(user)) {
      connectedUsers.add(user);
    }
  }

  //TODO: Do we need a removeUser method here?

  public List<Task> getTaskList() {
    return this.taskList;
  }

  public void setTaskList(List<Task> taskList) throws NullPointerException {
    if(taskList == null) {
      throw new NullPointerException();
    }
    this.taskList = taskList;
  }

  public void setCurrentState(PlanningPokerState planningPokerState)
  {
    this.currentState = planningPokerState;
  }

  public PlanningPokerState getCurrentState()
  {
    return this.currentState;
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