package DatatypesTest;

import DataTypes.Chat;
import DataTypes.Message;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.naming.NoInitialContextException;

import static org.junit.jupiter.api.Assertions.*;

class ChatTest {

    private Chat chat;

    @BeforeEach
    void setUp() {
        chat = new Chat();
    }

    @AfterEach
    void tearDown() {
        chat.clear();
    }

    @Test
    public void testAddMessageActuallyAddsMessageToChatHistory()
    {
        chat.addMessage(new Message("test"));
        assertEquals("test", chat.getMessage(0).getMessage());
    }

    @Test
    public void testRemoveMessageActuallyRemovesMessage()
    {
        assertThrows(IndexOutOfBoundsException.class, ()-> {
            Message testMessage = new Message("test");
            chat.addMessage(testMessage);
            chat.removeMessage(testMessage);
            chat.getMessage(0);
        });
    }

    @Test
    public void testGetMessageDoesntFindAnyMessageInChatHistory()
    {
        assertThrows(NullPointerException.class, ()-> {
            Message testMessage = new Message("test");
            chat.addMessage(testMessage);
            chat.removeMessage(testMessage);
            chat.getMessage(testMessage);
        });
    }

    @Test
    public void testGetMessageFromIllegalIndex()
    {
        assertThrows(IndexOutOfBoundsException.class, ()-> {
            Message testMessage = new Message("test");
            chat.addMessage(testMessage);
            chat.getMessage(15);
        });
    }

    // Work on:
    // Boundary: -1, 0, size(), size()-1
    // Equals metoden. 2 forskellige chat objekter.
    // Clear metoden.
    // Copy metoden.
}