package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import java.util.HashMap;

public class GameState {

    private final HashMap<Integer, Card> cardsMap; // map id and card
    private final Board mainBoard;
    private final Player[] players; //player[0] is blackPlayer
    private final Chat chat;
    private int currentPlayerIndex;
    private GamePhase currentGamePhase;
    private TurnPhase currentGameTurn;

    public GameState(HashMap cardsMap, Player[] players) throws NotUniquePlayerException, NotUniquePlayerNicknameException, NotUniquePlayerColorException {
        this.cardsMap = cardsMap;
        this.mainBoard = new Board();
        this.players = players;
        this.currentPlayerIndex = 0;
        this.currentGamePhase = GamePhase.MAINPHASE;
        this.currentGameTurn = null;
        this.chat = new Chat();

        if (!NicknameAndColorsAreValid()) {
            throw new NotUniquePlayerException("While creating the GameState Object I encountered a problem regarding the creation of players with the same nickname AND the same color");
        }
        if (!NicknamesAreValid()) {
            throw new NotUniquePlayerNicknameException("While creating the GameState Object I encountered a problem regarding the creation of players with the same nickname");
        }
        if (!ColorsAreValid()) {
            throw new NotUniquePlayerColorException("While creating the GameState Object I encountered a problem regarding the creation of players with the same color");
        }
    }

    private boolean NicknamesAreValid() {
        // check if players are unique (by nickname and color)
        for (int i = 0; i < players.length; i++) {
            for (int j = i+1; j < players.length; j++) {
                if (players[i].getNickname().equals(players[j].getNickname())) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean ColorsAreValid() {
        // check if players are unique (by nickname and color)
        for (int i = 0; i < players.length; i++) {
            for (int j = i+1; j < players.length; j++) {
                if (players[i].getColor().equals(players[j].getColor())) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean NicknameAndColorsAreValid() {
        // check if players are unique (by nickname and color)
        for (int i = 0; i < players.length; i++) {
            for (int j = i+1; j < players.length; j++) {
                if (players[i].getNickname().equals(players[j].getNickname()) && players[i].getColor().equals(players[j].getColor())) {
                    return false;
                }
            }
        }
        return true;
    }

    public Board getMainBoard() {
        return new Board();
    }

    public Player getCurrentPlayer() {
        return this.getPlayers()[this.currentPlayerIndex];
    }

    public Player[] getPlayers() {
        return this.players;
    }
}
