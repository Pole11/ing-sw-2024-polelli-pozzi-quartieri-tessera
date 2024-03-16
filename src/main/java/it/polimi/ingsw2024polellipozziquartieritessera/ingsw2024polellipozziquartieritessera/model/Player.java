package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.card.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
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
        objectiveCard = null;
        starterCard = null;
        this.color = color;
        resources = new HashMap<Resource, Integer>();
        items = new HashMap<Item, Integer>();
        board = new HashMap<Integer, Boolean>();
        hand = new HashMap<Integer, Boolean>();
    }

    public int getPoints() {
        return points;
    }

    public String getNickname() {
        return nickname;
    }

    public ObjectiveCard getObjectiveCard() {
        return objectiveCard;
    }

    public StarterCard getStarterCard() {
        return starterCard;
    }

    public Color getColor() {
        return color;
    }

    public HashMap<Resource, Integer> getResources() {
        return resources;
    }

    public HashMap<Item, Integer> getItems() {
        return items;
    }

    public HashMap<Integer, Boolean> getBoard() {
        return board;
    }

    public HashMap<Integer, Boolean> getHand() {
        return hand;
    }
}







