package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Color;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class GameState {
    private final HashMap<Integer, Card> cardsMap;

    private final Board mainBoard;
    private final Chat chat;

    private final ArrayList<Player> players; //player[0] is blackPlayer
    private int currentPlayerIndex;

    private GamePhase currentGamePhase;
    private TurnPhase currentGameTurn;

    private final ArrayDeque<Event> eventQueue;
    private Thread executeEvents;
    private final HashMap<Integer, Boolean> answered;

    private Thread timeoutThread;
    private GamePhase prevGamePhase;




    // CONSTRUCTOR
    public GameState() {
        this.cardsMap = new HashMap<>();

        this.mainBoard = new Board();
        this.chat = new Chat();

        this.players = new ArrayList<>();
        this.currentPlayerIndex = 0;

        this.currentGamePhase = GamePhase.NICKNAMEPHASE;
        this.currentGameTurn = TurnPhase.PLACINGPHASE;
        this.prevGamePhase = null;

        this.eventQueue = new ArrayDeque<>();

        this.timeoutThread = null;

        this.executeEvents = new Thread(this::executeEventsRunnable);
        this.executeEvents.start();
        this.answered = new HashMap<>();
        resetAnswered();

    }

    // GETTER
    public Board getMainBoard() {
        return mainBoard;
    }

    public Chat getChat() {
        return chat;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public Player getPlayer(int index) {
        return players.get(index);
    }

    public GamePhase getCurrentGamePhase() {
        return currentGamePhase;
    }

    public TurnPhase getCurrentGameTurn() {
        return currentGameTurn;
    }

    public Card getCard(int cardId) {
        return cardsMap.get(cardId);
    }

    public int getPlayersSize(){
        return this.players.size();
    }

    public ArrayDeque<Event> getEventQueue() {
        return eventQueue;
    }

    // SETTER
    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }

    public void setCurrentGamePhase(GamePhase currentGamePhase) {
        this.currentGamePhase = currentGamePhase;
    }

    public void setCurrentGameTurn(TurnPhase currentGameTurn) {
        this.currentGameTurn = currentGameTurn;
    }

    //ADDER

    public void addCardToCardsMap(int cardId, Card card) {
        this.cardsMap.put(cardId, card);
    }

    public void addToEventQueue(Event event) {
        this.eventQueue.add(event);
    }


    //EVENTQUEUE

    //even if there aren't always 4 players
    private void resetAnswered() {
        this.answered.put(0, false);
        this.answered.put(1, false);
        this.answered.put(2, false);
        this.answered.put(3, false);
    }

    private int numberAnswered() {
        //controllare se funziona
        return this.answered.values().stream().mapToInt(e-> e ? 1 : 0).sum();
    }

    private void executeEventsRunnable() {
        //non so se sia bello
        while (true){
            synchronized (eventQueue) {
                while (eventQueue.isEmpty()) {
                    try {
                        eventQueue.wait();
                    } catch (InterruptedException e) {
                    }
                }
                eventQueue.remove().execute();
            }
        }
    }


    public ArrayList<VirtualView> singleClient(VirtualView client){
        ArrayList<VirtualView> clients = new ArrayList<>();
        clients.add(client);
        return clients;
    }

    public ArrayList<VirtualView> otherClients(VirtualView client){
        ArrayList<VirtualView> clients = new ArrayList<>();
        for (Player playerIterator : players) {
            if (!client.equals(playerIterator.getClient())) {
                clients.add(playerIterator.getClient());
            }
        }
        return clients;
    }

    public ArrayList<VirtualView> allClients(){
        return (ArrayList<VirtualView>) players.stream().map(Player::getClient).collect(Collectors.toList());
    }


    //DISCONNECTIONS

    public void setPlayersConnected(int index, boolean connected){
        players.get(index).setConnected(connected);
    }


    private void manageReconnection(){
        if (this.currentGamePhase.equals(GamePhase.TIMEOUT)){
            this.timeoutThread.interrupt();
            currentGamePhase = prevGamePhase;
            prevGamePhase = null;
        }
        //updatePlayersConnected();
    }

    public void restoreView(){

    }


    public void startTimeout() throws InterruptedException {
        Thread.sleep(60*1000);
    }

    public void changeCurrentPlayer(){
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        //check for disconnetions
        int numberConnected = 0;
        for (int i = 0; i< players.size() && numberConnected <= 1; i++){
            if (!getPlayer(getCurrentPlayerIndex()).isConnected()){
                currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            } else {
                numberConnected ++;
            }
        }
        if (numberConnected == 1){
            this.prevGamePhase = currentGamePhase;
            this.currentGamePhase = GamePhase.TIMEOUT;
            timeoutThread = new Thread(() -> {
                try {
                    startTimeout();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            timeoutThread.start();
        } else if (numberConnected == 0){
            //chiedere specifica
        }
        synchronized (eventQueue){
            eventQueue.add(new UpdateCurrentPlayer(this, allClients(), currentPlayerIndex));
            eventQueue.notifyAll();
        }
    }

    //TODO: resilienza clients: bisogna mandare un ping a tutti i client e fare partire un timeout per ogni client, poi si aspetta una risposta da un client, che se non arriva si mette setta a disconesso, altrimenti si setta a connesso, questo ping va poi wrappato in un thread che esegue in modo periodico



    //PLAYERS MANAGING

    public Player getCurrentPlayer() {
        return players.get(this.currentPlayerIndex);
    }

    public Player getBlackPlayer(){
        return this.players.getFirst();
    }

    public Integer getPlayerIndex(VirtualView client) {
        int i = 0;
        for (Player playerIterator : this.players) {
            if (playerIterator.getClient().equals(client)) {
                return i;
            }
            i ++;
        }
        return -1;
    }

    public Integer getPlayerIndex(Player player) {
        int i = 0;
        for (Player playerIterator : this.players) {
            if (playerIterator.equals(player)) {
                return i;
            }
            i ++;
        }
        return -1;
    }


    public void addPlayer(String nickname, VirtualView client) {
        Player player = new Player(nickname, client, this);
        for (int j = 0; j < players.size(); j++) {
            if (player.getNickname().equals(players.get(j).getNickname())) {
                //takes for granted that player connection is updated
                //if the player is not connected but the gameState doesn't know, the following code fails
                if (!players.get(j).isConnected()){
                    this.manageReconnection();
                    synchronized (eventQueue){
                        //only to the client
                        this.restoreView();
                        eventQueue.add(new ChangePhaseEvent(this, singleClient(player.getClient()), this.currentGamePhase));
                        eventQueue.add(new MessageEvent(this, singleClient(player.getClient()), "you re-connected to the game"));
                        //to all the other clients says that a client reconnected
                        eventQueue.add(new ConnectionInfoEvent(this, otherClients(player.getClient()), player, true));
                        eventQueue.notifyAll();
                    }
                } else {
                    //only to the client
                    synchronized (eventQueue){
                        eventQueue.add(new ErrorEvent(this, singleClient(player.getClient()), "The nickname is already used, please choose another one"));
                        eventQueue.notifyAll();
                    }
                }
                return;
            }
        }
        players.add(player);
        synchronized (eventQueue){
            eventQueue.add(new SendIndexEvent(this, singleClient(player.getClient()), getPlayerIndex(player)));
            eventQueue.add(new ChangePhaseEvent(this, singleClient(player.getClient()), GamePhase.NICKNAMEPHASE));
            //to all the other clients gives the nickname so that they can execute their model
            eventQueue.add(new NicknameEvent(this, allClients(), player.getNickname()));
            eventQueue.notifyAll();
        }
    }


    //STARTING PHASE SETTERS


    // I: start game / set phase

    // II: initialize deck and shared cards / starter cards

    //III: let players choose the side

    // IV: let the player choose the color from the available colors
    // V: set hands and objectives for choice

    // VI: let the player choose the objective

    // VII: si potrebbe come da specifica scegliere a questo punto
    // un giocatore randomico per essere il blackPlayer (al momento sembra superflua)




    public void startGame() throws EmptyDeckException {
        this.mainBoard.shuffleCards();
        this.mainBoard.initSharedGoldCards();
        this.mainBoard.initSharedResourceCards();
        this.initStarters(); // set the starters cards for every player
        System.out.println("Starting game");
        this.currentGamePhase = GamePhase.CHOOSESTARTERSIDEPHASE;
        synchronized (eventQueue){
            //remember to changePhase
            eventQueue.add(new StartEvent(this, allClients()));
            eventQueue.add(new ChangePhaseEvent(this, allClients(), GamePhase.CHOOSESTARTERSIDEPHASE));
            eventQueue.notifyAll();
        }
    }

    public void initStarters() {
        // for every player set his starters (you have access to every player from the array players)

        Set<Integer> randomKeysSet = new HashSet<>(); // a set has no duplicates
        // Generate four different random numbers
        while (randomKeysSet.size() < players.size()) {
            int randomNumber = Config.firstStarterCardId + ThreadLocalRandom.current().nextInt(Config.STARTERQTY); // Adjust range as needed
            randomKeysSet.add(randomNumber);
        }

        ArrayList<Integer> randomKeysList = new ArrayList<>(randomKeysSet); // convert to a list to iterate
        for (int i = 0; i < randomKeysList.size(); i++) {
            StarterCard starterCard = (StarterCard) cardsMap.get((randomKeysList.get(i)));
            players.get(i).setStarterCard(starterCard); // we might pass it the map of all the cards
            players.get(i).initializeBoard(); // add the starter card to the board of the player
        }
    }

    public void setStarterSide(int playerIndex, Side side){
        Player player = this.players.get(playerIndex);
        if (this.answered.get(playerIndex)) {
            synchronized (eventQueue){
                eventQueue.add(new ErrorEvent(this, singleClient(player.getClient()), "You already selected the starter card"));
                eventQueue.notifyAll();
            }
            return;
        }
        StarterCard playerStarterCard = player.getStarterCard();
        //doesn't call updateBoard because the boardMatrix already has the card (without the side)
        player.addToPlacedCardsMap(playerStarterCard.getId(), side);

        synchronized (eventQueue){
            eventQueue.add(new UpdateStarterCardEvent(this, allClients(), playerIndex,  playerStarterCard.getId(), side));
        }

        // add the initial elements of the starter card
        for (Element ele : player.getStarterCard().getUncoveredElements(side)) {
            int currentOccurrences = player.getAllElements().get(ele);
            player.addToAllElements(ele, currentOccurrences + 1);
        }

        synchronized (eventQueue){
            eventQueue.add(new MessageEvent(this, singleClient(player.getClient()), "Thank you for the side of your starter card"));
            eventQueue.notifyAll();
        }
        this.answered.put(playerIndex, true);

        //notify him and all the others about the change
        if (numberAnswered() == players.size()){
            this.currentGamePhase = GamePhase.CHOOSECOLORPHASE;
            synchronized (eventQueue){
                eventQueue.add(new ChangePhaseEvent(this, allClients(), GamePhase.CHOOSECOLORPHASE));
                eventQueue.add(new MessageEvent(this, allClients(), "Everyone chose his side, now please select a valid color from one of the lists with the command CHOOSECOLOR [Blue, Green, Yellow, Red]"));
                eventQueue.notifyAll();
            }
            resetAnswered();
        }
    }

    public void setColor(int playerIndex, Color color) {//method called by view when the player chooses the side
        Player player = this.players.get(playerIndex);
        if (this.answered.get(playerIndex)) {
            synchronized (eventQueue){
                eventQueue.add(new ErrorEvent(this, singleClient(player.getClient()), "You already selected the color"));
                eventQueue.notifyAll();
            }
            return;
        }

        for (int j = 0; j < players.size(); j++) {
            if (color.equals(players.get(j).getColor())) {
                synchronized (eventQueue){
                    eventQueue.add(new ErrorEvent(this, singleClient(player.getClient()), "The color was already selected by another user, please select a new one"));
                    eventQueue.notifyAll();
                }
            }
        }
        player.setColor(color);
        synchronized (eventQueue){
            eventQueue.add(new MessageEvent(this, singleClient(player.getClient()), "Thank you for selecting the color"));
            eventQueue.notifyAll();
        }
        this.answered.put(playerIndex, true);

        if (numberAnswered() == players.size()) {
            resetAnswered();
            try {
                //send also the cards to the view
                this.setHands();
            } catch (EmptyDeckException e) {
                // if in this moment of the game the deck is empty it's a programming mistake
                throw new RuntimeException(e);
            }
            //send also the cards to the view
            this.setObjectives();
            this.currentGamePhase = GamePhase.CHOOSEOBJECTIVEPHASE;
            synchronized (eventQueue){
                eventQueue.add(new ChangePhaseEvent(this, allClients(), GamePhase.CHOOSEOBJECTIVEPHASE));
                eventQueue.add(new MessageEvent(this, allClients(), "Everyone chose his color, now please select one of the objective card from the selection with the command CHOOSEOBJECTIVE [0/1], to see your card use the command SHOWOBJECTIVE" ));
                eventQueue.notifyAll();
            }
        }
    }

    public void setHands() throws EmptyDeckException {
        // popolate hands for every player
        for (int i = 0; i < players.size(); i++) {
            GoldCard goldCard = mainBoard.drawFromGoldDeck();
            ResourceCard resourceCard1 = mainBoard.drawFromResourceDeck();
            ResourceCard resourceCard2 = mainBoard.drawFromResourceDeck();

            players.get(i).addToHandCardsMap(goldCard.getId(), Side.FRONT); // default is front side
            players.get(i).addToHandCardsMap(resourceCard1.getId(), Side.FRONT); // default is front side
            players.get(i).addToHandCardsMap(resourceCard2.getId(), Side.FRONT); // default is front side
        }
    }

    //set SecretObjectiveOptions and SharedObjectiveCards
    public void setObjectives(){
        Set<Integer> randomKeysSet = new HashSet<>(); // a set has no duplicates
        // Generate four different random numbers
        while (randomKeysSet.size() < 2 * players.size() + 2) { // 2 for each player and 2 shared
            int randomNumber = Config.firstObjectiveCardId + ThreadLocalRandom.current().nextInt(Config.OBJECTIVEQTY); // Adjust range as needed
            randomKeysSet.add(randomNumber);
        }

        ArrayList<Integer> randomKeysList = new ArrayList<>(randomKeysSet); // convert to a list to iterate
        for (int i = 0; i < players.size(); i++) {
            ObjectiveCard objectiveCard0 = (ObjectiveCard) cardsMap.get(randomKeysList.get(i));
            ObjectiveCard objectiveCard1 = (ObjectiveCard) cardsMap.get(randomKeysList.get(i + players.size()));
            ObjectiveCard[] objectiveCards = new ObjectiveCard[2];
            objectiveCards[0] = objectiveCard0;
            objectiveCards[1] = objectiveCard1;

            players.get(i).setSecretObjectiveCardOptions(objectiveCards);
        }

        ObjectiveCard objectiveCard0 = (ObjectiveCard) cardsMap.get(randomKeysList.get(randomKeysList.size() - 2));
        ObjectiveCard objectiveCard1 = (ObjectiveCard) cardsMap.get(randomKeysList.getLast());

        mainBoard.setSharedObjectiveCard(0,  objectiveCard0);
        mainBoard.setSharedObjectiveCard(1, objectiveCard1);
        ArrayList<ObjectiveCard> sharedObjectives = new ArrayList<>();
        sharedObjectives.add(objectiveCard0);
        sharedObjectives.add(objectiveCard1);

        synchronized (eventQueue){
            eventQueue.add(new UpdateSharedObjectiveEvent(this, allClients(), sharedObjectives));
            eventQueue.notifyAll();
        }
    }


    //called from controller
    public void setSecretObjective (int playerIndex, int cardIndex) {
        Player player = this.players.get(playerIndex);
        if (this.answered.get(playerIndex)) {
            synchronized (eventQueue){
                eventQueue.add(new ErrorEvent(this, singleClient(player.getClient()), "You already selected the objective card"));
                eventQueue.notifyAll();
            }
            return;
        }

        if (cardIndex != 0 && cardIndex != 1) {
            synchronized (eventQueue){
                eventQueue.add(new ErrorEvent(this, singleClient(player.getClient()), "The card chosen was not in the options list"));
                eventQueue.notifyAll();
            }
            return;
        }

        player.setObjectiveCard(player.getObjectiveCardOption(cardIndex));

        //considerare se spostare
        synchronized (eventQueue){
            eventQueue.add(new MessageEvent(this, singleClient(player.getClient()), "Thank you for selecting the objective card"));
            eventQueue.notifyAll();
        }
        this.answered.put(playerIndex, true);


        if (numberAnswered() == players.size()) {
            resetAnswered();
            currentGamePhase = GamePhase.MAINPHASE;
            synchronized (eventQueue){
                eventQueue.add(new ChangePhaseEvent(this, allClients(), GamePhase.MAINPHASE));
                eventQueue.add(new MessageEvent(this, singleClient(getCurrentPlayer().getClient()), "The preparation phase is over, it's your turn to play as first, use the command PLACECARD [placingCardId] [tableCardId] [tableCornerPos(Upright/Upleft/Downright/Downleft)] [placingCardSide(Front/Back)] to place your card"));
                eventQueue.add(new MessageEvent(this, otherClients(getCurrentPlayer().getClient()), "The preparation phase is over, it's now the turn of " + getCurrentPlayer().getNickname() + " to play as first"));
                eventQueue.notifyAll();
            }
        }
    }


    //GAMEPHASE



//----------------------------place Card--------------------------------

    public void placeCard(Player player, int placingCardId, int tableCardId, CornerPos tableCornerPos, CornerPos placingCornerPos, Side placingCardSide) throws PlacingOnHiddenCornerException {
        // check if the indirect corners are hidden
        CornerCard placingCard = (CornerCard) cardsMap.get(placingCardId);
        ArrayList<ArrayList<Integer>> playerBoard = player.getPlayerBoard();
        for (Integer row = 0; row < playerBoard.size(); row++) {
            for (Integer col = 0; col < playerBoard.get(row).size(); col++) {
                if (playerBoard.get(row).get(col).equals(tableCardId)) { // if the card is the one on the table
                    int placingRow = row;
                    int placingCol = col;

                    switch (tableCornerPos) {
                        case CornerPos.UPLEFT -> placingRow--;
                        case CornerPos.UPRIGHT -> placingCol++;
                        case CornerPos.DOWNRIGHT -> placingRow++;
                        case CornerPos.DOWNLEFT -> placingCol--;
                    }

                    // check if there is a card in the up left corner
                    if (placingRow - 1 >= 0 && placingRow - 1 < playerBoard.size() && placingCol >= 0 && placingCol < playerBoard.getFirst().size() && playerBoard.get(placingRow - 1).get(placingCol) != -1) { // -1 means empty
                        int matrixCardId = playerBoard.get(placingRow - 1).get(placingCol);
                        placeCardHelper(player, matrixCardId, placingCard, placingCardSide, CornerPos.DOWNRIGHT, CornerPos.UPLEFT);

                    }
                    // check if there is a card in the up right corner
                    if (placingRow >= 0 && placingRow < playerBoard.size() && placingCol + 1 >= 0 && placingCol + 1 < playerBoard.getFirst().size() && playerBoard.get(placingRow).get(placingCol + 1) != -1) { // -1 means empty
                        int matrixCardId = playerBoard.get(placingRow).get(placingCol + 1);
                        placeCardHelper(player, matrixCardId, placingCard, placingCardSide, CornerPos.DOWNLEFT, CornerPos.UPRIGHT);

                    }
                    // check if there is a card in the down right corner
                    if (placingRow + 1 >= 0 && placingRow + 1 < playerBoard.size() && placingCol >= 0 && placingCol < playerBoard.getFirst().size() && playerBoard.get(placingRow + 1).get(placingCol) != -1) { // -1 means empty
                        int matrixCardId = playerBoard.get(placingRow + 1).get(placingCol);
                        placeCardHelper(player, matrixCardId, placingCard, placingCardSide, CornerPos.UPLEFT, CornerPos.DOWNRIGHT);
                    }

                    // check if there is a card in the down left corner
                    if (placingRow >= 0 && placingRow < playerBoard.size() && placingCol - 1 >= 0 && placingCol - 1 < playerBoard.getFirst().size() && playerBoard.get(placingRow).get(placingCol - 1) != -1) { // -1 means empty
                        int matrixCardId = playerBoard.get(placingRow).get(placingCol - 1);
                        placeCardHelper(player, matrixCardId, placingCard, placingCardSide, CornerPos.UPRIGHT, CornerPos.DOWNLEFT);
                    }
                    break;
                }
            }
        }
        updateElements(player, placingCard, placingCardSide);
        //NON MI PIACE MESSA QUI PERCHE NON E' L'ULTIMA AZIONE DEL CONTROLLER, CERCARE DI CAPIRE COME MODIFIFCARE
        synchronized (eventQueue){
            eventQueue.add(new MessageEvent(this, singleClient(player.getClient()), "you placed your card, now you have to draw your card with DRAW [SHAREDGOLD1/SHAREDGOLD2/SHAREDRESOURCE1/SHAREDRESOURCE/DECKGOLD/DECKRESOURCE]"));
            eventQueue.add(new MessageEvent(this, otherClients(player.getClient()), getCurrentPlayer().getNickname() + "placed his card"));
            eventQueue.notifyAll();
        }
    }

    private void placeCardHelper(Player player, int matrixCardId, CornerCard placingCard, Side placingCardSide, CornerPos matrixCardCornerPos, CornerPos indirectPlacingCornerPos) throws PlacingOnHiddenCornerException {
        CornerCard matrixCard = (CornerCard) cardsMap.get(matrixCardId);
        Corner matrixCorner = matrixCard.getCorners(player.getBoardSide(matrixCardId)).get(matrixCardCornerPos.ordinal());
        Corner indirectPlacingCorner = placingCard.getCorners(placingCardSide).get(indirectPlacingCornerPos.ordinal());

        if (matrixCorner.getHidden()) {
            throw new PlacingOnHiddenCornerException("you are trying to place on an hidden corner");
        }

        // link the corners
        indirectPlacingCorner.setLinkedCorner(matrixCorner);
        indirectPlacingCorner.setCovered(false);// is false at default anyway
        matrixCorner.setLinkedCorner(indirectPlacingCorner);
        matrixCorner.setCovered(true);

        // remove elements of the table card that are covered
        Element cornerEle = matrixCorner.getElement();
        if (player.getAllElements().containsKey(cornerEle)) {
            int currentOccurencies = player.getAllElements().get(cornerEle);
            if (currentOccurencies > 0)
                player.removeFromAllElements(cornerEle);
        }

    }


    public void updateElements(Player player, CornerCard placingCard, Side placingCardSide){
        for (Element ele : Element.values()) {
            if (player.getAllElements().get(ele) != null) {
                int currentOccurencies = player.getAllElements().get(ele);
                int newOccurencies = Collections.frequency(placingCard.getUncoveredElements(placingCardSide), ele);
                player.addToAllElements(ele, currentOccurencies + newOccurencies);
            }
        }
    }

    //ENDGAME

    public boolean isGameEnded() {
        // 20 points?
        for (int i = 0; i < this.players.size(); i++) {
            if (this.players.get(i).getPoints() >= Config.POINTSTOENDPHASE) {
                return true;
            }
        }
        // cards ended?
        if (this.getMainBoard().isResourceDeckEmpty()) {
            return true;
        }
        return this.getMainBoard().isGoldDeckEmpty();
    }

    void calculateFinalPoints() throws CardNotPlacedException, WrongInstanceTypeException {
        ObjectiveCard sharedCard1 = this.mainBoard.getSharedObjectiveCard(0);
        ObjectiveCard sharedCard2 = this.mainBoard.getSharedObjectiveCard(1);

        for (int i = 0; i < this.players.size(); i++) {
            Player player = this.players.get(i);
            ObjectiveCard objCard = player.getObjectiveCard();

            int points = 0;

            points += objCard.calculatePoints(player);
            points += sharedCard1.calculatePoints(player);
            points += sharedCard2.calculatePoints(player);

            this.players.get(i).addPoints(points);
        }
    }

    public ArrayList<Integer> getWinnerPlayerIndex() throws GameIsNotEndedException {
        int maxPoints = 0;
        ArrayList<Integer> winnerPlayerIndeces = new ArrayList<>();
        boolean playerReachedPoints = false;
        for (int i = 0; i < this.players.size(); i++) {
            if (this.players.get(i).getPoints() >= Config.POINTSTOENDPHASE){
                playerReachedPoints = true;
            }
            if (this.players.get(i).getPoints() > maxPoints) {
                winnerPlayerIndeces = new ArrayList<>();
                winnerPlayerIndeces.add(i);
                maxPoints = this.players.get(i).getPoints();
            } else if (this.players.get(i).getPoints() == maxPoints) {
                winnerPlayerIndeces.add(i);
            }
        }
        if (!playerReachedPoints){
            throw new GameIsNotEndedException("You called getWinnerPlayerIndex even if the game is not ended, no one is at 20 points");
        }

        // now winnerPlayerIndeces contains who has the maximum number of points
        // decide the real winner if there is a draw
        ArrayList<Integer> winnerObjectives = new ArrayList<>();
        if (winnerPlayerIndeces.size() > 1) {
            int mostObjectivesWins = -1;
            for (int i=0; i<winnerPlayerIndeces.size(); i++) {
                if (this.players.get(i).getObjectivesWon() > mostObjectivesWins){
                    winnerObjectives.clear();
                    winnerObjectives.add(i);
                    mostObjectivesWins = this.players.get(i).getObjectivesWon();
                }
                else if (this.players.get(i).getObjectivesWon() == mostObjectivesWins){
                    winnerObjectives.add(i);
                }
            }
            winnerPlayerIndeces = winnerObjectives;
        }
        return(winnerPlayerIndeces);
    }


//------------ others --------------------

    // !!! to implement !!!
    public void saveGameState(){
    }

    // !!! optional !!!
    public int getTurnTime(){
        return 0;
    }
}


/*

    private void playTurn() throws CardNotPlacedException, WrongInstanceTypeException {
        // notify current player to place card (notify all changes)
        // wait
        this.setCurrentGameTurn(TurnPhase.DRAWPHASE);
        // notify current player to draw a card (notify all changes)
        //wait

        // Base Case: game ended
        if (isGameEnded()){
            this.setCurrentGamePhase(GamePhase.ENDPHASE);
            this.setCurrentGameTurn(TurnPhase.PLACINGPHASE);
            playEndTurn();
        }

        // play next turn
        else{
            // execute current player
            this.setCurrentPlayerIndex((this.getCurrentPlayerIndex()+1) % this.players.size());
            this.setCurrentGameTurn(TurnPhase.PLACINGPHASE);

            playTurn();
        }
    }

    private void playEndTurn() throws CardNotPlacedException, WrongInstanceTypeException {
        // BASE CASE:
        if (this.players.get(getCurrentPlayerIndex()) == this.getBlackPlayer()){
            if (isLastTurn) {
                this.setCurrentGamePhase(GamePhase.FINALPHASE);
                calculateFinalPoints();
            }
            else {
                isLastTurn = true;
            }
        }

        // Recursive call of the EndPhaseTurn
        else{
            // TODO: 17/04/2024 network place methods
            // notify current player to place card (notify all changes)
            // wait
            this.setCurrentGameTurn(TurnPhase.DRAWPHASE);

            if(this.getMainBoard().getGoldDeck().isEmpty() && this.getMainBoard().getResourceDeck().isEmpty() && this.getMainBoard().getSharedGoldCards().length == 0 && this.getMainBoard().getSharedResourceCards().length == 0){
                ; //there are no card to be drawn, the game continues without drawing
            }
            else{
                // TODO: 17/04/2024 network draw methods
                // notify current player to draw a card (notify all changes)
                //wait
            }
            // play next turn
            // execute current player
            this.setCurrentPlayerIndex((this.getCurrentPlayerIndex()+1) % this.players.size());
            this.setCurrentGameTurn(TurnPhase.PLACINGPHASE);

            playEndTurn();
        }
    }
 */