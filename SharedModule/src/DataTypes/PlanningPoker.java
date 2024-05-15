package DataTypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PlanningPoker implements Serializable
{
  private ArrayList<User> connectedUsers;
  private int planningPokerID;
  private Chat chat;
  private List<Task> taskList;
  private User scrumMaster;
  private User productOwner;

  public PlanningPoker(int planningPokerId)
  {
    connectedUsers = new ArrayList<>();
    chat = new Chat();
    taskList = new ArrayList<>();
    setPlanningPokerID(planningPokerId);
    setScrumMaster(null);
    setProductOwner(null);
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
    if (user == null)
    {
      throw new NullPointerException();
    }

    if (!connectedUsers.contains(user))
    {
      connectedUsers.add(user);
    }
  }

  public User getScrumMaster()
  {
    return scrumMaster;
  }

  public void setScrumMaster(User scrumMaster)
  {
    this.scrumMaster = scrumMaster;
  }

  public User getProductOwner()
  {
    return productOwner;
  }

  public void setProductOwner(User productOwner)
  {
    this.productOwner = productOwner;
  }

  //TODO: Do we need a removeUser method here?

  public List<Task> getTaskList()
  {
    return this.taskList;
  }

  public void setTaskList(List<Task> taskList) throws NullPointerException
  {
    if (taskList == null)
    {
      throw new NullPointerException();
    }
    this.taskList = taskList;
  }

  public void setPlanningPokerID(int planningPokerID)
  {
    this.planningPokerID = planningPokerID;
  }

  public int getPlanningPokerID()
  {
    return planningPokerID;
  }
}