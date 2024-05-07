package DatatypesTest;

import DataTypes.Chat;
import DataTypes.PlanningPoker;
import DataTypes.Task;
import DataTypes.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlanningPokerTest
{
  private PlanningPoker planningPoker;

  @BeforeEach void setUp()
  {
    planningPoker = new PlanningPoker();
  }

  @AfterEach void tearDown()
  {
  }

  @Test
  void testGeneratePlanningPokerIDGeneratesWithinBounds()
  {
    PlanningPoker planningPokerTest = new PlanningPoker();
    for (int i = 0; i < 1000; i++) {
      planningPokerTest.generatePlanningPokerID();
      int generatedID = Integer.parseInt(planningPokerTest.getPlanningPokerID());
      assertTrue(0 < generatedID && generatedID < 10000);
    }
  }


  /** Tests Constructor: Zombies: Zero/One test, all essential parameters are initialized */
  @Test public void constructorInitializesAllClassDataFields() {
    // Act:
    boolean connectedUsersInitialized = planningPoker.getConnectedUsers() != null;
    boolean chatInitialized = planningPoker.getChat() != null;
    boolean taskListInitialized = planningPoker.getTaskList() != null;
    boolean gameIdWasGenerated = planningPoker.getPlanningPokerID() != null;

    boolean result = connectedUsersInitialized && chatInitialized && taskListInitialized && gameIdWasGenerated;

    // Assert:
    assertTrue(result);
  }


  /** Tests getChat() method: Zombies: Zero/Null test, the returned chat is initialized and a Chat Object */
  @Test public void getChatReturnsInitializedChatObject() {
    // Act:
    boolean chatInitialized = planningPoker.getChat() != null;
    boolean chatIsChatObject = planningPoker.getChat() instanceof Chat;

    // Assert:
    assertTrue(chatInitialized && chatIsChatObject);
  }


  /** Tests getConnectedUsers() method: Zombies: Zero/Null test, the returned list is initialized and an instance of List */
  @Test public void getConnectedUsersReturnsInitializedUserList() {
    // Act:
    boolean connectedUsersInitialized = planningPoker.getConnectedUsers() != null;
    boolean userListIsAList = planningPoker.getConnectedUsers() instanceof List;

    // Assert:
    assertTrue(connectedUsersInitialized && userListIsAList);
  }


  /** Tests addConnectedUsers() method: Zombies: Zero/Null & Exception test */
  @Test public void addConnectedUsersThrowsNullPointerException_WhenAddingANullUser() {
    // Arrange:
    User user1 = null;

    // Act & Assert:
    assertThrows(NullPointerException.class, () -> planningPoker.addUserToSession(user1));
  }


  /** Tests addConnectedUsers() & getConnectedUsers() method: Zombies: One test */
  @Test public void addConnectedUsers_IsPossibleWith1User() {
    // Arrange:
    User user1 = new User("Test_User_One","Test_User_One_Password");

    // Act:
    planningPoker.addUserToSession(user1);
    boolean user1WasAddedToGame = planningPoker.getConnectedUsers().contains(user1);

    // Assert:
    assertTrue(user1WasAddedToGame);
  }


  /** Tests addConnectedUsers() & getConnectedUsers() method: Zombies: Many test */
  @Test public void addConnectedUsers_IsPossibleWith10Users() {
    // Arrange:
    User user1 = new User("Test_User_One","Test_User_Two_Password");
    User user2 = new User("Test_User_Two","Test_User_Two_Password");
    User user3 = new User("Test_User_Three","Test_User_Three_Password");
    User user4 = new User("Test_User_Four","Test_User_Four_Password");
    User user5 = new User("Test_User_Five","Test_User_Five_Password");
    User user6 = new User("Test_User_Six","Test_User_Six_Password");
    User user7 = new User("Test_User_Seven","Test_User_Seven_Password");
    User user8 = new User("Test_User_Eight","Test_User_Eight_Password");
    User user9 = new User("Test_User_Nine","Test_User_Nine_Password");
    User user10 = new User("Test_User_Ten","Test_User_Ten_Password");

    // Act;
    planningPoker.addUserToSession(user1);
    boolean user1WasAddedToGame = planningPoker.getConnectedUsers().contains(user1);

    planningPoker.addUserToSession(user2);
    boolean user2WasAddedToGame = planningPoker.getConnectedUsers().contains(user2);

    planningPoker.addUserToSession(user3);
    boolean user3WasAddedToGame = planningPoker.getConnectedUsers().contains(user3);

    planningPoker.addUserToSession(user4);
    boolean user4WasAddedToGame = planningPoker.getConnectedUsers().contains(user4);

    planningPoker.addUserToSession(user5);
    boolean user5WasAddedToGame = planningPoker.getConnectedUsers().contains(user5);

    planningPoker.addUserToSession(user6);
    boolean user6WasAddedToGame = planningPoker.getConnectedUsers().contains(user6);

    planningPoker.addUserToSession(user7);
    boolean user7WasAddedToGame = planningPoker.getConnectedUsers().contains(user7);

    planningPoker.addUserToSession(user8);
    boolean user8WasAddedToGame = planningPoker.getConnectedUsers().contains(user8);

    planningPoker.addUserToSession(user9);
    boolean user9WasAddedToGame = planningPoker.getConnectedUsers().contains(user9);

    planningPoker.addUserToSession(user10);
    boolean user10WasAddedToGame = planningPoker.getConnectedUsers().contains(user10);

    boolean result = user1WasAddedToGame && user2WasAddedToGame
        && user3WasAddedToGame && user4WasAddedToGame
        && user5WasAddedToGame && user6WasAddedToGame
        && user7WasAddedToGame && user8WasAddedToGame
        && user9WasAddedToGame && user10WasAddedToGame;

    // Assert:
    assertTrue(result);
  }


  /** Tests getTaskList() method: Zombies: Zero/Null test, the returned list is initialized and an instance of List */
  @Test public void getTaskListReturnsInitializedTaskList() {
    // Act:
    boolean taskListInitialized = planningPoker.getTaskList() != null;
    boolean taskListIsAList = planningPoker.getTaskList() instanceof List;

    // Assert:
    assertTrue(taskListInitialized && taskListIsAList);
  }


  /** Tests setTaskList() method: Zombies: Zero/Null & Exception test */
  @Test public void setTaskListThrowsNullPointerException_WhenProvidingNullArgument() {
    // Arrange:
    List<Task> taskList = null;

    // Act & Assert:
    assertThrows(NullPointerException.class, () -> planningPoker.setTaskList(taskList));
  }


  /** Tests getTaskList() & setTaskList() method: Zombies: One test */
  @Test public void setTaskList_IsPossibleWithProvidingAnEmptyTaskList() {
    // Arrange:
    List<Task> taskList = new ArrayList<>();

    // Act:
    planningPoker.setTaskList(taskList);
    boolean taskListWasSet = planningPoker.getTaskList().equals(taskList);

    // Assert:
    assertTrue(taskListWasSet);
  }


  /** Tests getTaskList() & setTaskList() method: Zombies: One test */
  @Test public void setTaskList_IsPossibleWithProvidingAnNonEmptyTaskList() {
    // Arrange:
    List<Task> taskList = new ArrayList<>();
    taskList.add(new Task("Test1", "TestDesc1"));
    taskList.add(new Task("Test2", "TestDesc2"));
    taskList.add(new Task("Test3", "TestDesc3"));

    // Act:
    planningPoker.setTaskList(taskList);
    boolean taskListWasSet = planningPoker.getTaskList().equals(taskList);

    // Assert:
    assertTrue(taskListWasSet);
  }
}