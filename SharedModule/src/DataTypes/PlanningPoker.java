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
  private String adminOverridePassword;

  public PlanningPoker(int planningPokerId)
  {
    connectedUsers = new ArrayList<>();
    chat = new Chat();
    taskList = new ArrayList<>();
    setPlanningPokerID(planningPokerId);
    setScrumMaster(null);
    setProductOwner(null);
    setAdminOverridePassword("admin"); // TODO: Implement more security around this, and enable the Scrum Master to set a custom password on game creation.
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

    boolean userNotFound = true;
    for (User existingUser : connectedUsers) {
      if(existingUser.getUsername().equals(user.getUsername())) {
        userNotFound = false;
        break;
      }
    }

    if(userNotFound) {
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

  public String getAdminOverridePassword() {
    return this.adminOverridePassword;
  }

  public void setAdminOverridePassword(String pswd) {
    this.adminOverridePassword = pswd;
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