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

    // Work on:
    // (Hvad sker der hvis man søger efter en besked vha. getMessage(null) som ikke findes?)
    // Equals metoden.
    // Copy metoden.
}