package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;

import java.time.LocalDateTime;
import java.lang.String;

public class Message {
    private final int id;
    private static int maxId = 0;
    private final Player author;
    private final LocalDateTime dateTime;
    private final String content;

    public Message(Player author, LocalDateTime dateTime, String content){
        maxId++;
        this.author = author;
        this.dateTime = dateTime;
        this.content = content;
        this.id = maxId;
    }
    public int getId() {
        return id;
    }

    public Player getAuthor() {
        return author;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getContent() {
        return content;
    }
}