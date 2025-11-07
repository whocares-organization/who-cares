package applicationtest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.MockEmailServer;

class MockEmailServerTest {

    private MockEmailServer mockEmailServer;

    @BeforeEach
    void setUp() {
        mockEmailServer = new MockEmailServer();
    }

    @Test
    void sendsEmailStoresMessage() {
        mockEmailServer.sendEmail("user@example.com", "Hello!");

        List<String> messages = mockEmailServer.getSentMessages();
        assertEquals(1, messages.size());
        assertEquals("user@example.com: Hello!", messages.get(0));
    }

    @Test
    void storesMultipleMessages() {
        mockEmailServer.sendEmail("user1@example.com", "Message 1");
        mockEmailServer.sendEmail("user2@example.com", "Message 2");

        List<String> messages = mockEmailServer.getSentMessages();
        assertEquals(2, messages.size());
        assertTrue(messages.contains("user1@example.com: Message 1"));
        assertTrue(messages.contains("user2@example.com: Message 2"));
    }

    @Test
    void clearRemovesAllMessages() {
        mockEmailServer.sendEmail("user@example.com", "Hello!");
        mockEmailServer.clear();

        List<String> messages = mockEmailServer.getSentMessages();
        assertTrue(messages.isEmpty());
    }

    @Test
    void getSentMessagesInitiallyEmpty() {
        List<String> messages = mockEmailServer.getSentMessages();
        assertNotNull(messages);
        assertTrue(messages.isEmpty());
    }
}
