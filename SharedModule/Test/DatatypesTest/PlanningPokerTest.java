package DatatypesTest;

import DataTypes.Chat;
import DataTypes.PlanningPoker;
import DataTypes.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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


  /** Tests addConnectedUsers() method: Zombies: Zero/Null test */
  @Test public void addConnectedUsersThrowsNullPointerException_WhenAddingANullUser() {
    // Arrange:
    User user1 = null;
    //User user1 = new User("Test_User_One","Test_User_One_Password");

    // Act & Assert:
    assertThrows(NullPointerException.class, () -> planningPoker.addUserToSession(user1));
  }
}