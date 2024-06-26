package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;

/**
 * The Message class represents a single message in the chat.
 * Each message has a unique identifier, an author, and content.
 */
public class Message {
    private static int maxId = 0;
    private final int id;
    private final int author;
    private final String content;

    /**
     * Constructs a new Message with the specified author and content.
     * The message ID is automatically assigned.
     *
     * @param playerIndex the index of the player who authored the message
     * @param content     the content of the message
     */
    public Message(int playerIndex, String content) {
        maxId++;
        this.author = playerIndex;
        this.content = content;
        this.id = maxId;
    }

    /**
     * Returns the unique identifier of the message.
     *
     * @return the message ID
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the index of the player who authored the message.
     *
     * @return the player index
     */
    public int getAuthor() {
        return author;
    }

    /**
     * Returns the content of the message.
     *
     * @return the message content
     */
    public String getContent() {
        return content;
    }
}