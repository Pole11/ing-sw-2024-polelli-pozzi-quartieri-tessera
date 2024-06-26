package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;

import java.util.ArrayList;

/**
 * The Chat class represents a chat that contains a list of messages.
 */
public class Chat {
    private ArrayList<Message> messages; // may be final (the reference only)

    /**
     * Constructs an empty Chat.
     */
    public Chat() {
        messages = new ArrayList<Message>();
    }

    /**
     * Adds a message to the chat.
     *
     * @param playerIndex the index of the player sending the message
     * @param content     the content of the message
     */
    public void addMessage(int playerIndex, String content) {
        Message message = new Message(playerIndex, content);
        messages.add(message);
    }

    /**
     * Returns the list of messages in the chat.
     *
     * @return the list of messages
     */
    public ArrayList<Message> getMessages() {
        return messages;
    }

    /**
     * Returns the last message in the chat.
     *
     * @return the last message, or null if the chat is empty
     */
    public Message getLastMessage() {
        if (messages.isEmpty()) {
            return null;
        }
        return messages.get(messages.size() - 1);
    }
}