package DatatypesTest;

import DataTypes.PlanningPoker;
import DataTypes.User;
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
        PlanningPoker poker = new PlanningPoker();
        user.setPlanningPoker(poker);
        assertEquals(poker, user.getPlanningPoker());
    }

    @Test
    void testUserAddedToPokerSessionViaSetPlanningPoker()
    {
        PlanningPoker poker = new PlanningPoker();
        user.setPlanningPoker(poker);
        assertEquals(user, poker.getConnectedUsers().get(0));
    }
}