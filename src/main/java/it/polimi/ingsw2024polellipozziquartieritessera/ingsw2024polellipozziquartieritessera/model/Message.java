package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;

import java.time.LocalDateTime;
import java.lang.String;

public class Message {
    private final int id;
    private static int maxId = 0;
    private final int author;
    private final String content;

    public Message(int playerIndex, String content){
        maxId++;
        this.author = playerIndex;
        this.content = content;
        this.id = maxId;
    }
    public int getId() {
        return id;
    }

    public int getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}