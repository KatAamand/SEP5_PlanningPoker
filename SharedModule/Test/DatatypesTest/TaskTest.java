package DatatypesTest;

import DataTypes.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    /** Tests Task Constructor:
     * Zombies: Zero/Null & Exception test */
    @Test public void constructorThrowsNullPointerExceptionWhenPassingNullHeaderArgumentAndStringDescriptionArgument(){
        // Arrange
        String header = null;
        String description = "testDescription";

        // Act and Assert
        assertThrows(NullPointerException.class, () -> new Task(header, description));
    }


    /** Tests Task Constructor:
     * Zombies: Zero/Null & Exception test */
    @Test public void constructorThrowsNullPointerExceptionWhenPassingStringHeaderArgumentAndNullDescriptionArgument(){
        // Arrange
        String header = "testHeaderName";
        String description = null;

        // Act and Assert
        assertThrows(NullPointerException.class, () -> new Task(header, description));
    }


    /** Tests Task Constructor:
     * Zombies: One test */
    @Test public void constructorInitializesTaskProperly(){
        // Arrange
        String header = "testHeaderName";
        String description = "testDescription";

        // Act
        Task task = new Task(header, description);

        // Assert
        boolean headerIsEqualToArgument = task.getTaskHeader().equals(header);
        boolean descriptIsEqualToArgument = task.getDescription().equals(description);
        boolean result = headerIsEqualToArgument && descriptIsEqualToArgument;

        assertTrue(result);
    }


    /** Tests Task setTask() && getTask() method:
     * Zombies: Zero/Null & Exception test */
    @Test public void setTaskNameUpdatesTaskNameWhenReceivingNullArgument() {
        // Arrange:
        String header = null;

        // Act & Assert:
        Task task = new Task("", "");
        assertThrows(NullPointerException.class, () -> task.setTaskHeader(header));
    }


    /** Tests Task setTask() && getTask() method:
     * Zombies: One test */
    @Test public void setTaskNameUpdatesTaskNameWhenReceivingStringArgument() {
        // Arrange:
        String header = "testHeaderName";

        // Act:
        Task task = new Task("", "");
        task.setTaskHeader(header);

        // Assert:
        assertEquals(header, task.getTaskHeader());
    }


    /** Tests Task setDescription() && getDescription() method:
     * Zombies: Zero/Null & Exception test */
    @Test public void setTaskDescriptionUpdatesTaskDescriptionWhenReceivingNullArgument() {
        // Arrange:
        String description = null;

        // Act & Assert:
        Task task = new Task("", "");
        assertThrows(NullPointerException.class, () -> task.setDescription(description));
    }


    /** Tests Task setDescription() && getDescription() method:
     * Zombies: One test */
    @Test public void setTaskDescriptionUpdatesTaskDescriptionWhenReceivingStringArgument() {
        // Arrange:
        String description = "testDescription";

        // Act:
        Task task = new Task("", "");
        task.setDescription(description);

        // Assert:
        assertEquals(description, task.getDescription());
    }



    /** Tests Task setFinalEffort() && getFinalEffort() method:
     * Zombies: Zero/Null & Exception test */
    @Test public void setTaskFinalEffortUpdatesTaskFinalEffortWhenReceivingNullArgument() {
        // Arrange:
        Task task = new Task("TestHeader", "TestDescription");
        String effort = null;

        // Act & Assert:
        assertThrows(NullPointerException.class, () -> task.setFinalEffort(effort));
    }


    /** Tests Task getFinalEffort() method:
     * Zombies: Zero/Null & Exception test */
    @Test public void taskFinalEffortIsInitializedAsStringValue_Undefined() {
        // Arrange:
        Task task = new Task("TestHeader", "TestDescription");

        // Act:
        String effort = task.getFinalEffort();

        // Assert:
        assertEquals("Undefined", effort);
    }


    /** Tests Task setFinalEffort() && getFinalEffort() method:
     * Zombies: One test */
    @Test public void setTaskFinalEffortUpdatesTaskFinalEffortWhenReceivingStringArgument() {
        // Arrange:
        Task task = new Task("TestHeader", "TestDescription");
        //TODO: Update this test when proper effort values have been identified and implemented in the code:
        String effort = "THIS_IS_A_LEGAL_EFFORT_VALUE"; // <- Update/Refactor this line.

        // Act:
        task.setFinalEffort(effort);

        // Assert:
        assertEquals(effort, task.getFinalEffort());
    }


    /** Tests Task setFinalEffort() method:
     * Zombies: Exception test */
    /*@Test public void setTaskFinalEffortThrowsIllegalArgumentExceptionWhenPassingIllegalEffortAsArgument() {
        // Arrange:
        Task task = new Task("TestHeader", "TestDescription");

        //TODO: Update this test when proper effort values have been identified and implemented in the code:
        String effort = "THIS_IS_NOT_A_LEGAL_EFFORT_VALUE"; // <- Update/Refactor this line.

        // Act & Assert:
        assertThrows(IllegalArgumentException.class, () -> task.setFinalEffort(effort));
    }*/


    /** Tests Task equals() method:
     * Zombies: Zero/Null test */
    @Test public void equalsReturnsFalseWhenComparingTaskObjectWithNullObject() {
        // Arrange:
        Task task1 = new Task("TestHeader", "TestDescription");
        Task task2 = null;

        // Act:
        boolean equals = task1.equals(task2);

        // Assert:
        assertFalse(equals);
    }


    /** Tests Task equals() method:
     * Zombies: One test */
    @Test public void equalsReturnsFalseWhenComparingTaskObjectWithDifferentTaskObject() {
        // Arrange:
        Task task1 = new Task("TestHeader1", "TestDescription1");
        Task task2 = new Task("TestHeader2", "TestDescription2");

        // Act:
        boolean equals = task1.equals(task2);

        // Assert:
        assertFalse(equals);
    }


    /** Tests Task equals() method:
     * Zombies: One test */
    @Test public void equalsReturnsTrueWhenComparingTaskObjectWithSameTaskObject() {
        // Arrange:
        Task task1 = new Task("TestHeader1", "TestDescription1");

        // Act:
        boolean equals = task1.equals(task1);

        // Assert:
        assertTrue(equals);
    }


    /** Tests Task equals() method:
     * Zombies: One test */
    @Test public void equalsReturnsTrueWhenComparingTaskObjectWithSimilarTaskObject() {
        // Arrange:
        Task task1 = new Task("TestHeader1", "TestDescription1");
        Task task2 = new Task("TestHeader1", "TestDescription1");

        // Act:
        boolean equals = task1.equals(task2);

        // Assert:
        assertTrue(equals);
    }


    /** Tests Task copy() method: Zombies: One test */
    @Test public void copyReturnsCopyOfTaskObject() {
        // Arrange:
        Task task1 = new Task("TestHeader1", "TestDescription1");

        // Act:
        Task task2 = task1.copy();

        // Assert:
        boolean tasksAreDifferentObjects = task1 != task2;
        boolean tasksHaveIdenticalContents = task1.equals(task2);

        assertTrue(tasksAreDifferentObjects && tasksHaveIdenticalContents);
    }
}