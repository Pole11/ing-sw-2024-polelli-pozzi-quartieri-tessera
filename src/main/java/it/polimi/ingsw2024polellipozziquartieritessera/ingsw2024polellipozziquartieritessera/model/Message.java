package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;

import java.time.LocalDateTime;

public class Message {
    private static int lastId;
    private int id;
    private Player author;
    private LocalDateTime dateTime;

    public Message(Player author, LocalDateTime dateTime){
        Message.lastId ++;
        id = Message.lastId;
        this.author = author;
        this.dateTime = dateTime;
    }
    public void addMessage(Player player, String content){
        
    }

}