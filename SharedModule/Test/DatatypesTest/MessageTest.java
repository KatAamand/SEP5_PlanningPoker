package DatatypesTest;

import DataTypes.Message;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void testCopyMethodMakesNewObject()
    {
        Message testMessage1 = new Message("test");
        Message copiedMessage = testMessage1.copy();
        assertEquals("test", copiedMessage.getMessage());
    }

    @Test
    public void testEqualsOnTwoDifferentObjects()
    {
        Message message1 = new Message("test");
        Message message2 = new Message("test2");

        assertFalse(message2.equals(message1));
    }

    @Test
    public void testEqualsOnTwoSameObjects()
    {
        Message message1 = new Message("test");
        Message message2 = new Message("test");

        assertTrue(message2.equals(message1));
    }

    @Test
    public void testWhenMessageStringIsNull()
    {
        Message message = new Message(null);
        assertThrows(NullPointerException.class, () -> {
            message.getMessage();
        });
    }

    // Work on:
    // (Hvad sker der hvis man søger efter en besked vha. getMessage(null) som ikke findes?)
    // Equals metoden.
    // Copy metoden.
}