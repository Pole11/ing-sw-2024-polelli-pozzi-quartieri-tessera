package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Chat {
    private ArrayList<Message> messages; // may be final (the reference only)

    public Chat(){
        messages = new ArrayList<Message>();
    }

    public void addMessage(int playerIndex, String content) {
        Message message = new Message(playerIndex, content);
        messages.addLast(message);
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public Message getLastMessage(){
        if (messages.isEmpty()){
            return null;
        }
        return messages.getLast();
    }
}