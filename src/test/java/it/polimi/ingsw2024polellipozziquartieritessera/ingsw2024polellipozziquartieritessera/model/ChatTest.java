package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ChatTest {
    private Chat chat;

    @Test
    public void testAddMessage() {
        chat = new Chat();
        chat.addMessage(1, "Hello, world!");
        ArrayList<Message> messages = chat.getMessages();

        assertEquals(1, messages.size());
        assertEquals(1, messages.get(0).getAuthor());
        assertEquals("Hello, world!", messages.get(0).getContent());
    }

    @Test
    public void testGetLastMessage() {
        chat = new Chat();
        assertNull(chat.getLastMessage());

        chat.addMessage(1, "First message");
        chat.addMessage(2, "Second message");

        Message lastMessage = chat.getLastMessage();
        assertNotNull(lastMessage);
        assertEquals(2, lastMessage.getAuthor());
        assertEquals("Second message", lastMessage.getContent());
    }
}
