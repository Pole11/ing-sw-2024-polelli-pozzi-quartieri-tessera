package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.card.*;
import java.util.HashMap;

public class GameState {

    private HashTable<Integer, Card> cardsMap;
    private Board mainBoard;
    private Player[] players; //player[0] is blackPlayer
    private Player currentPlayer;
    private GamePhase currentGamePhase;
    private GameTurn currentGameTurn;

    public GameState(Board mainBoard){
        cardsMap = new HashTable<Integer, Card>();
        this.mainBoard = mainBoard;
        players = new Player[Config.N_PLAYERS];
        currentPlayer = players[0];
        currentGamePhase = GamePhase.mainPhase;
        currentGameTurn = null;
    }
}
