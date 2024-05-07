package DatatypesTest;

import DataTypes.PlanningPoker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlanningPokerTest
{

  @BeforeEach void setUp()
  {
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
}