package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.card.*;
import java.util.HashMap;

public class Player {
    int points;
    String nickname;
    ObjectiveCard objectiveCard;
    StarterCard starterCard;
    Color color;
    HashMap<Resource, Integer> resources;
    HashMap<Item, Integer> items;
    HashMap<Integer, Boolean> board;
    HashMap<Integer, Boolean> hand;

    public Player(String nickname, Color color){
        points = 0;
        this.nickname = nickname;
        ObjectiveCard = null;
        StarterCard = null;
        Color = this.color;
        resources = new HashMap<Resource, Integer>();
        items = new HashMap<Item, Integer>();
        board = new HashMap<Integer, Boolean>();
        hand = new HashMap<Integer, Boolean>();
    }
}







