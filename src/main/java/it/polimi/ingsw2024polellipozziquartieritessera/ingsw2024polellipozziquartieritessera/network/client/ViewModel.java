package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.CornerPos;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.GamePhase;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.TurnPhase;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Chat;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Message;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events.UpdateBoardEvent;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ViewModel {
    private int playerIndex;
    private final HashMap<Integer,String> nicknamesMap;
    private final int[] objectives; // 0,1 are common - 2 is secret // se la carta non c'è viene posta a -1
    private GamePhase gamePhase;
    private TurnPhase turnPhase;
    private boolean gameStarted;
    private final HashMap<Integer,ArrayList<Integer>> handsMap; // mappa delle hands dei player
    private final HashMap<Integer,Side> placedSideMap; // side delle carte sulla board (unico per id)
    private final HashMap<Integer,Side> handsSideMap; // side delle carte in mano (unico per id)
    private final HashMap<Integer,ArrayList<ArrayList<Integer>>> boardsMap; // mappa delle board dei players
    private final int[] sharedCards; // se la carta non c'è viene inizializzata a -1
    private Chat chat;

    public ViewModel() {
        nicknamesMap = new HashMap<>();
        handsSideMap = new HashMap<>();
        placedSideMap = new HashMap<>();
        //contains also the secretOptions, when the objective is decided the [3] is -1
        objectives = new int[4];
        Arrays.fill(objectives, -1);
        sharedCards = new int[4];
        Arrays.fill(sharedCards, -1);
        gameStarted = false;
        handsMap = new HashMap<>();
        boardsMap = new HashMap<>();
    }

    // BASIC SETTER
    public void setPlayerIndex(int index) {
        this.playerIndex = index; }
    public void addedNickname(String nickname, int index) {
        this.nicknamesMap.put(index, nickname);
    }

    // GAME UPDATES
    public void setCommonObjectives(int objectiveCardId1, int objectiveCardId2) {
        objectives[0] = objectiveCardId1;
        objectives[1] = objectiveCardId2;
    }
    public void setSecretObjective(int objectiveCardId){
        objectives[2] = objectiveCardId;
    }
    public void setTurnPhase(TurnPhase turnPhase){
        this.turnPhase = turnPhase; }
    public void setGamePhase(GamePhase gamePhase){
        this.gamePhase = gamePhase;
    }

    // position are: resource [0,1], gold [2,3]
    public void addedSharedCard(int cardId, int position){
        sharedCards[position] = cardId;
    }

    public void removedSharedCard(int cardId, int position){
        sharedCards[position] = -1;
    }

    public void addedCardToHand(int playerIndex, int cardId) {
        if (!handsMap.containsKey(playerIndex)){
            handsMap.put(playerIndex, new ArrayList<>());
        }
        handsMap.get(playerIndex).add(cardId);
    }
    public void removedCardFromHand(int playerIndex, int cardId){
        handsMap.get(playerIndex).remove(cardId);
        handsSideMap.remove(cardId);
    }

    public void updateBoard(int playerIndex, int placingCardId, int tableCardId, CornerPos existingCornerPos, Side side){
        // for starter cards (initialization)
        if (!boardsMap.containsKey(playerIndex)){
            initializeBoard(playerIndex, placingCardId);
        }

        // for all the other placements
        else{
            placeCard(playerIndex, placingCardId, tableCardId, existingCornerPos);
        }

        // update placed card side
        placedSideMap.put(placingCardId, side);
    }

    public void setCardSide(int cardId, Side side){
        handsSideMap.replace(cardId, side);
    }

    // TODO: Sarebbe bello trasformare i messaggi per salvarsi solo l'id del player e non il player stesso
    // Da sistemare e rifare
    public void addedMessage(int playerIndex, String messageContent, LocalDateTime time){
        // String message = new Message(playerIndex, time, messageContent);
    }

    // GETTERS FOR CLI&GUI

    public int getPlayerIndex() {
        return playerIndex;
    }

    public HashMap<Integer, String> getNicknamesMap() {
        return nicknamesMap;
    }

    public String getNickname(int playerIndex) {
        return nicknamesMap.get(playerIndex);
    }

    public int[] getObjectives() {
        return objectives;
    }

    public int[] getCommonObjectiveCards() {
        int[] commonObjectives = new int[2];
        commonObjectives[0] = objectives[0];
        commonObjectives[1] = objectives[1];
        return commonObjectives;
    }

    public int getSecretObjectiveCard(){
        return objectives[2];
    }

    public GamePhase getGamePhase() {
        return gamePhase;
    }

    public TurnPhase getTurnPhase() {
        return turnPhase;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public Chat getChat() {
        return chat;
    }

    public HashMap<Integer, ArrayList<Integer>> getHandsMap() {
        return handsMap;
    }

    public ArrayList<Integer> getHand(int playerIndex){
        return handsMap.get(playerIndex);
    }

    public HashMap<Integer, Side> getHandsSideMap() {
        return handsSideMap;
    }

    public Side getHandCardSide(int cardId){
        return handsSideMap.get(cardId);
    }

    public HashMap<Integer, Side> getplacedSideMap() {
        return placedSideMap;
    }

    public Side getPlacedCardSide(int cardId){
        return placedSideMap.get(cardId);
    }

    public HashMap<Integer, ArrayList<ArrayList<Integer>>> getBoardsMap() {
        return boardsMap;
    }

    public ArrayList<ArrayList<Integer>> getPlayerBoard(int playerIndex){
        return boardsMap.get(playerIndex);
    }

    public int[] getSharedCards() {
        return sharedCards;
    }

    public int[] getSharedResourceCards() {
        return new int[] { sharedCards[0], sharedCards[1] };
    }

    public int[] getSharedGoldCards() {
        return new int[] { sharedCards[2], sharedCards[3] };
    }







    private void initializeBoard(int playerIndex, int placingCardId){
        this.boardsMap.get(playerIndex).clear();
        ArrayList<Integer> row = new ArrayList<>();
        row.add(placingCardId);
        this.boardsMap.get(playerIndex).add(row);
    }

    private void placeCard(int playerIndex, int placingCardId, int tableCardId, CornerPos tableCornerPos){
        ArrayList<ArrayList<Integer>> playerBoard = this.boardsMap.get(playerIndex);

        int rowIndex = -1;
        int colIndex = -1;

        // Find the coordinates of the existing card on the board
        for (int i = 0; i < playerBoard.size(); i++) {
            ArrayList<Integer> row = playerBoard.get(i);
            for (int j = 0; j < row.size(); j++) {
                if (row.get(j) == tableCardId) {
                    rowIndex = i;
                    colIndex = j;
                } else if (row.get(j) == placingCardId) {
                    // la carta è già piazzata, esci subito
                    return;
                }
            }
        }

        if (rowIndex == -1 && colIndex == -1) {
            // non ha trovato la carta, magari un'eccezione?
        }

        // Define the position of the new card
        switch(tableCornerPos) {
            case CornerPos.UPLEFT:
                rowIndex--;
                break;
            case CornerPos.UPRIGHT:
                colIndex++;
                break;
            case CornerPos.DOWNRIGHT:
                rowIndex++;
                break;
            case CornerPos.DOWNLEFT:
                colIndex--;
                break;
        }

        // Check if the new position is outside the bounds of the current matrix
        if (rowIndex < 0 || rowIndex >= playerBoard.size() || colIndex < 0 || colIndex >= playerBoard.getFirst().size()) {
            // Expand the matrix if necessary
            expandBoard(rowIndex, colIndex, playerBoard);
            if (rowIndex < 0){
                rowIndex ++;
            } else if (colIndex < 0){
                colIndex ++;
            }
        }
        // Place the new card at the specified position
        playerBoard.get(rowIndex).set(colIndex, placingCardId);
    }

    private void expandBoard(int rowIndex, int colIndex, ArrayList<ArrayList<Integer>> playerBoard){
        // Expand the matrix to include the new position (remember that this supports only one execute: row++/-- or col++/--)
        // Expand rows if needed
        int rowDim = playerBoard.getFirst().size();
        if (rowIndex < 0) {
            ArrayList<Integer> newRow = new ArrayList<>();
            for (int i = 0; i < rowDim; i++) {
                newRow.add(-1);
            }
            playerBoard.addFirst(newRow);
        } else if (rowIndex >= playerBoard.size()) {
            ArrayList<Integer> newRow = new ArrayList<>();
            for (int i = 0; i < rowDim; i++) {
                newRow.add(-1);
            }
            playerBoard.add(newRow);
        }
        // Expand columns if needed
        for (ArrayList<Integer> row : playerBoard) {
            if (colIndex < 0) {
                row.add(0, -1); // Adding -1 if there is no card
            } else if (colIndex >= row.size()) {
                row.add(-1); // Placeholder for empty cell
            }
        }
    }
}