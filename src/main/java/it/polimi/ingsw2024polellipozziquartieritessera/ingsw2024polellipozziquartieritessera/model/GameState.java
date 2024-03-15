package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.card.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import java.util.HashMap;

public class GameState {

    private final HashMap<Integer, Card> cardsMap;
    private final Board mainBoard;
    private final Player[] players; //player[0] is blackPlayer
    private final Chat chat;
    private Player currentPlayer;
    private GamePhase currentGamePhase;
    private TurnPhase currentGameTurn;

    public GameState(Board mainBoard, HashMap cardsMap, Player[] players){
        this.cardsMap = cardsMap;
        this.mainBoard = mainBoard;
        this.players = players;
        currentPlayer = players[0];
        currentGamePhase = GamePhase.MAINPHASE;
        currentGameTurn = null;
    }

    //non ho messo i metodi ancora

}
