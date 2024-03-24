package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;

import java.util.ArrayList;

public class Chat {
    private ArrayList<Message> messages;
    private int lastId;

    public Chat(){
        messages = new ArrayList<Message>();
    }

    public int getLastId(){
        return lastId;
    }

    public void addMessage(Player player, String content){
        
    }
}