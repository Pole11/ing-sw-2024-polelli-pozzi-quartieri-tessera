package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;

import java.time.LocalDateTime;
import java.lang.String;

public class Message {
    private static int lastId;
    private int id;
    private Player author;
    private LocalDateTime dateTime;
    private String content;

    public Message(Player author, LocalDateTime dateTime, String content){
        Message.lastId++;
        this.id = Message.lastId;
        this.author = author;
        this.dateTime = dateTime;
        this.content = content;
    }

    public static int getLastId() {
        return lastId;
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