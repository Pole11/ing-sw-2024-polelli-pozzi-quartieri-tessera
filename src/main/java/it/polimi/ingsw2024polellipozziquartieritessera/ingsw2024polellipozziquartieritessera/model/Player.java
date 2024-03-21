package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.card.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import java.util.HashMap;


public class Player {
    int points;
    String nickname;
    ObjectiveCard objectiveCard; // it is the secret objective
    StarterCard starterCard; // it is the most important card because it is used to create all the composition of the cards
    Color color;
    HashMap<Element, Integer> elements;
    HashMap<Integer, Boolean> board; // true if the card is in the board of the player
    HashMap<Integer, Boolean> hand; // true if the card id is in the hand of the player

    public Player(String nickname, Color color){
        points = 0;
        this.nickname = nickname;
        objectiveCard = null;
        starterCard = null;
        this.color = color;
        elements = new HashMap<Element, Integer>();
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

    public HashMap<Element, Integer> getResources() {
        return elements;
    }

    public HashMap<Integer, Boolean> getBoard() {
        return board;
    }

    public HashMap<Integer, Boolean> getHand() {
        return hand;
    }
}







