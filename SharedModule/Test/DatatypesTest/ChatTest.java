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

    @Test
    public void testOutSideOfBoundaryGetMessageIndexMinusOne()
    {
        assertThrows(IndexOutOfBoundsException.class, ()-> {
            Message testMessage = new Message("test");
            chat.addMessage(testMessage);
            chat.getMessage(-1);
        });
    }

    @Test
    public void testEqualsMethodDifferentiatesTwoDifferentObjects()
    {
        chat.addMessage(new Message("Test1"));

        Chat chat2 = new Chat();
        chat2.addMessage(new Message("Test2"));

        assertFalse(chat.equals(chat2));
    }

    @Test
    public void testEqualsMethodOnTwoEqualObjects()
    {
        Chat chat2 = new Chat();

        chat2.addMessage(new Message("test"));
        chat.addMessage(new Message("test"));

        assertTrue(chat.equals(chat2));
    }

    @Test
    public void testClearMethodClearsChat()
    {
        chat.addMessage(new Message("Test"));
        chat.addMessage(new Message("Test1"));
        chat.addMessage(new Message("Test2"));
        chat.addMessage(new Message("Test3"));
        chat.clear();
        assertThrows(IndexOutOfBoundsException.class, () -> {
            chat.getMessage(0);
        });
    }

    @Test
    public void testCopyMethodMakesACopyOfObject()
    {
        chat.addMessage(new Message("Test"));
        Chat copiedChat = chat.copy();

        assertTrue(chat.equals(copiedChat));

    }
}