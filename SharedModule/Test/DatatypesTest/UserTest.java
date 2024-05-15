package DatatypesTest;

import DataTypes.PlanningPoker;
import DataTypes.User;
import DataTypes.UserRoles.ConcreteRoles.Developer;
import DataTypes.UserRoles.ConcreteRoles.ScrumMaster;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    private User user;

    @BeforeEach
    void setUp() {
        user = new User("Test", "test");
    }

    @AfterEach
    void tearDown() {
        
    }

    @Test
    void testSetAndGetPlanningPoker()
    {
        PlanningPoker poker = new PlanningPoker(101);
        user.setPlanningPoker(poker);
        assertEquals(poker, user.getPlanningPoker());
    }

    @Test
    void testUserAddedToPokerSessionViaSetPlanningPoker()
    {
        PlanningPoker poker = new PlanningPoker(101);
        user.setPlanningPoker(poker);
        assertEquals(user, poker.getConnectedUsers().get(0));
    }

    @Test
    void testEqualsMethodOnTwoDifferentObjects()
    {
        User user2 = new User("test2", "test2");
        assertFalse(user.equals(user2));
    }

    @Test
    void testEqualsOnTwoSameObjects()
    {
        User user2 = new User("Test", "test");
        assertTrue(user2.equals(user));
    }

    @Test public void setRoleToBeNullThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> user.setRole(null));
    }

    @Test public void setRoleToBeDeveloperIsPossible() {
        user.setRole(new ScrumMaster());
        assertInstanceOf(ScrumMaster.class, user.getRole());
    }

    @Test public void userIsInitializedAsADeveloper() {
      assertInstanceOf(Developer.class, user.getRole());
    }

    @Test
    void testCopyMethodMakesSameObject()
    {
        User copiedUser = user.copy();
        assertTrue(user.equals(copiedUser));
    }

    // Work on:
    // Getters and Setters
    // Equals metoden.
    // Copy metoden.
}