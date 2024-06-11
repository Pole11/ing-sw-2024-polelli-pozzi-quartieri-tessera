package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Color;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Populate;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Server;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class GameState {
    private final Server server;

    private final HashMap<Integer, Card> cardsMap;

    private final Board mainBoard;
    private final Chat chat;

    private final ArrayList<Player> players; //player[0] is blackPlayer
    private int currentPlayerIndex;

    private GamePhase currentGamePhase;
    private TurnPhase currentGameTurn;

    private final ArrayDeque<Event> eventQueue;
    private final Thread executeEvents;
    private boolean executeEventRunning;

    private final HashMap<Integer, Boolean> answered;
    private final Thread pingThread;
    private boolean pingRunning;
    ArrayList<Thread> playerThreads;

    private Thread timeoutThread;
    private GamePhase prevGamePhase;

    private final Thread saveStateThread;
    private boolean saveStateRunning;

    private int turnToPlay;


    // CONSTRUCTOR
    public GameState(Server server) {
        this.server = server;
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

        this.executeEventRunning = true;
        this.executeEvents = new Thread(this::executeEventsRunnable);
        this.executeEvents.start();

        this.playerThreads = new ArrayList<>();

        this.pingRunning = true;
        this.pingThread = new Thread(this::pingThreadRunnable);
        pingThread.start();

        this.answered = new HashMap<>();
        resetAnswered();

        this.saveStateRunning = true;
        this.saveStateThread = new Thread(this::saveStateThreadRunnable);
        //saveStateThread.start();

        //cambia in intMax
        this.turnToPlay = 1;

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

    public int getPlayersSize() {
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

    //ADDER/REMOVER

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
        return this.answered.values().stream().mapToInt(e -> e ? 1 : 0).sum();
    }

    private void executeEventsRunnable() {
        while (executeEventRunning) {
            Event event = null;
            synchronized (eventQueue) {
                while (eventQueue.isEmpty()) {
                    try {
                        eventQueue.wait();
                    } catch (InterruptedException e) {
                        System.out.println("interrupted");
                        return;
                    }
                }

                //System.out.println("exited from while");

                if (!executeEventRunning){
                    break;
                }

                event = eventQueue.remove();


            }
            event.execute();
        }
    }


    public ArrayList<VirtualView> singleClient(VirtualView client) {
        ArrayList<VirtualView> clients = new ArrayList<>();
        clients.add(client);
        return clients;
    }

    public ArrayList<VirtualView> otherClients(VirtualView client) {
        ArrayList<VirtualView> clients = new ArrayList<>();
        for (Player playerIterator : players) {
            if (playerIterator.isConnected() && !client.equals(playerIterator.getClient())) {
                clients.add(playerIterator.getClient());
            }
        }
        return clients;
    }

    public ArrayList<VirtualView> allConnectedClients() {
        return (ArrayList<VirtualView>) players.stream().filter(Player::isConnected).map(Player::getClient).collect(Collectors.toList());
    }

    public ArrayList<VirtualView> allClients() {
        return (ArrayList<VirtualView>) players.stream().map(Player::getClient).collect(Collectors.toList());
    }


    //DISCONNECTIONS

    public void pingThreadRunnable() {
        while (pingRunning) {
            for (int i = 0; i < playerThreads.size(); i++){
                int finalI = i;
                playerThreads.set(i, new Thread(() -> {
                    int j = finalI;
                    try {
                        //wait for the answer of players
                        Thread.sleep(1000*Config.WAIT_FOR_PING_TIME);
                        if (players.get(j).isConnected()){
                            System.out.println("client "  + j + " disconnected");
                            synchronized (players) {
                                players.get(j).setConnected(false);
                            }
                            playerDisconnected();
                        }
                    } catch (InterruptedException e) {

                    }
                }));
                playerThreads.get(i).start();
            }

            updatePlayersConnected();

            try {
                //wait to ping another time
                Thread.sleep(1000*Config.NEXT_PING_TIME);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void pingAnswer(VirtualView client) {
        synchronized (players) {
            playerThreads.get(getPlayerIndex(client)).interrupt();
            try {
                playerThreads.get(getPlayerIndex(client)).join();
            } catch (InterruptedException e) {}
        }
    }

    public void setPlayersConnected(int index, boolean connected) {
        synchronized (players){
            players.get(index).setConnected(connected);
        }
        //players.get(index).setClient(null);
    }

    private void manageReconnection(Player player) {
        restoreView(player.getClient());
        synchronized (players) {
            player.setConnected(true);
        }
        if (this.currentGamePhase.equals(GamePhase.TIMEOUT)) {
            this.timeoutThread.interrupt();
            currentGamePhase = prevGamePhase;
            prevGamePhase = null;
        }
    }


    public void restoreView(VirtualView client) {
        //send everything to client
    }


    public void playerDisconnected() {
        if (currentGamePhase.equals(GamePhase.NICKNAMEPHASE)) {
            //DA SCEGLIERE DA QUANDO IN POI FARLO, SE COSI VA BENE O DA MAIN
            return;
        }
        long numberConnected;
        synchronized (players) {
            numberConnected = players.stream().filter(Player::isConnected).count();
        }
        if (numberConnected <= 1) {
            this.prevGamePhase = currentGamePhase;
            this.currentGamePhase = GamePhase.TIMEOUT;
            System.out.println("Timeout for ending the game started");
            timeoutThread = new Thread(() -> {
                try {
                    Thread.sleep(1000*Config.TIMEOUT_TIME);
                    //if the timeout ends
                    currentGamePhase = GamePhase.FINALPHASE;
                    ArrayList<Integer> winners;
                    synchronized (players) {
                        winners = (ArrayList<Integer>) players.stream().filter(Player::isConnected).map(this::getPlayerIndex).collect(Collectors.toList());
                    }
                    synchronized (eventQueue) {
                        eventQueue.add(new GameEndedEvent(this, allConnectedClients(), winners));
                        eventQueue.notifyAll();
                    }
                    System.out.println("game ended after timeout expired");
                    restart();
                } catch (InterruptedException e) {
                }
            });
            timeoutThread.start();
        }
    }

    public void changeCurrentPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        //check for disconnetions
        for (int i = 0; i < players.size(); i++) {
            if (!getPlayer(getCurrentPlayerIndex()).isConnected()) {
                currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            } else {
                break;
            }
        }
        synchronized (eventQueue) {
            eventQueue.add(new UpdateCurrentPlayer(this, allConnectedClients(), currentPlayerIndex));
            eventQueue.notifyAll();
        }
        //TODO: capire se va bene decrementare di 1 in ogni caso
        if (currentGamePhase.equals(GamePhase.ENDPHASE)) {
            turnToPlay--;
        }
        playerDisconnected();

    }

    public void updatePlayersConnected() {
        synchronized (eventQueue){
            eventQueue.add(new PingEvent(this, allClients()));
            eventQueue.notifyAll();
        }
    }


    //PLAYERS MANAGING

    public Player getCurrentPlayer() {
        return players.get(this.currentPlayerIndex);
    }

    public Player getBlackPlayer() {
        return this.players.getFirst();
    }

    public Integer getPlayerIndex(VirtualView client) {
        int i = 0;
        for (Player playerIterator : this.players) {
            if (playerIterator.getClient().equals(client)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public Integer getPlayerIndex(Player player) {
        int i = 0;
        for (Player playerIterator : this.players) {
            if (playerIterator.equals(player)) {
                return i;
            }
            i++;
        }
        return -1;
    }


    public void addPlayer(String nickname, VirtualView client) {
        Player player = new Player(nickname, client, this);
        //if someone choose his nickname, is inside the match, even if he is temporanely disconnected
        //TODO: si potrebbe fare in modo che si possa iniziare una partita solo se tutti i giocaatori sono connessi, ma bisognerebbe anche kickare

        if (allClients().contains(client) && players.get(getPlayerIndex(client)).isConnected()) {
            synchronized (eventQueue) {
                eventQueue.add(new ErrorEvent(this, singleClient(client), "You already chose a nickname, you cannot change it"));
                eventQueue.notifyAll();
            }
            return;
        }


        for (int j = 0; j < players.size(); j++) {
            if (player.getNickname().equals(players.get(j).getNickname())) {
                //takes for granted that player connection is updated
                //if the player is not connected but the gameState doesn't know, the following code fails
                if (!players.get(j).isConnected()) {
                    players.get(j).setClient(client);
                    this.manageReconnection(players.get(j));
                    synchronized (eventQueue) {
                        //only to the client
                        eventQueue.add(new UpdateGamePhaseEvent(this, singleClient(client), this.currentGamePhase));
                        //to all the other clients says that a client reconnected
                        //DOVREBBE ESSERE FATTO IN PLAYER ma non so se funzioni
                        //eventQueue.add(new ConnectionInfoEvent(this, otherClients(player.getClient()), player, true));
                        eventQueue.notifyAll();
                    }
                } else {
                    if (allConnectedClients().size()<Config.MAX_PLAYERS){
                        synchronized (eventQueue) {
                            eventQueue.add(new ErrorEvent(this, singleClient(client), "The nickname is already used, please choose another one"));
                            eventQueue.notifyAll();
                        }
                    }
                }
                return;
            }
        }

        if (allConnectedClients().size() >= Config.MAX_PLAYERS) {
            synchronized (eventQueue) {
                eventQueue.add(new ErrorEvent(this, singleClient(client), "The game is full"));
                eventQueue.notifyAll();
            }
            return;
        }


        players.add(player);
        playerThreads.add(new Thread());
        synchronized (eventQueue) {
            eventQueue.add(new SendIndexEvent(this, singleClient(player.getClient()), getPlayerIndex(player)));
            eventQueue.add(new UpdateGamePhaseEvent(this, singleClient(player.getClient()), GamePhase.NICKNAMEPHASE));
            //to all the other clients gives the nickname so that they can execute their model
            eventQueue.add(new NicknameEvent(this, otherClients(player.getClient()), getPlayerIndex(player), player.getNickname()));
            for (Player playerIterator : players) {
                eventQueue.add(new NicknameEvent(this, singleClient(player.getClient()), getPlayerIndex(playerIterator), playerIterator.getNickname()));
            }
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


    public void startGame(VirtualView client) throws EmptyDeckException {
        ArrayList<VirtualView> clients = allConnectedClients();
        if (!clients.contains(client)) {
            synchronized (eventQueue) {
                eventQueue.add(new ErrorEvent(this, singleClient(client), "Please register before starting the game"));
                eventQueue.notifyAll();
            }
            return;
        }
        if (clients.size() < 2) {
            if (clients.contains(client)) {
                synchronized (eventQueue) {
                    eventQueue.add(new ErrorEvent(this, singleClient(client), "Number of player insufficient"));
                    eventQueue.notifyAll();
                }
            }
            return;
        }

        this.mainBoard.shuffleCards();
        this.mainBoard.initSharedGoldCards();
        this.mainBoard.initSharedResourceCards();
        updateMainBoard();
        this.initStarters(); // set the starters cards for every player
        System.out.println("Starting game");
        this.currentGamePhase.changePhase(this);
    }

    public void updateMainBoard() {
        synchronized (eventQueue) {
            eventQueue.add(new UpdateMainBoardEvent(this, allConnectedClients(), mainBoard));
            System.out.println("UPDATE DELLA MAIN BOARD");
            System.out.println(eventQueue);
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

    public void setStarterSide(int playerIndex, Side side) {
        Player player = this.players.get(playerIndex);
        if (this.answered.get(playerIndex)) {
            synchronized (eventQueue) {
                eventQueue.add(new ErrorEvent(this, singleClient(player.getClient()), "You already selected the starter card"));
                eventQueue.notifyAll();
            }
            return;
        }
        StarterCard playerStarterCard = player.getStarterCard();
        //doesn't call updateBoard because the boardMatrix already has the card (without the side)
        player.addToPlacedCardsMap(playerStarterCard.getId(), side);

        synchronized (eventQueue) {
            eventQueue.add(new UpdateStarterCardEvent(this, allConnectedClients(), playerIndex, playerStarterCard.getId(), side));
            eventQueue.notifyAll();
        }

        // add the initial elements of the starter card
        for (Element ele : player.getStarterCard().getUncoveredElements(side)) {
            int currentOccurrences = player.getAllElements().get(ele);
            player.addToAllElements(ele, currentOccurrences + 1);
        }


        this.answered.put(playerIndex, true);
        System.out.println(playerIndex + " chose his startercard, the number of answered is: " + numberAnswered());
        //notify him and all the others about the change
        if (numberAnswered() == players.size()) {
            System.out.println("everyone answered");
            this.currentGamePhase.changePhase(this);
            resetAnswered();
        }
    }

    public void setColor(int playerIndex, Color color) {//method called by view when the player chooses the side
        Player player = this.players.get(playerIndex);
        if (this.answered.get(playerIndex)) {
            synchronized (eventQueue) {
                eventQueue.add(new ErrorEvent(this, singleClient(player.getClient()), "You already selected the color"));
                eventQueue.notifyAll();
            }
            return;
        }

        for (int j = 0; j < players.size(); j++) {
            if (color.equals(players.get(j).getColor())) {
                synchronized (eventQueue) {
                    eventQueue.add(new ErrorEvent(this, singleClient(player.getClient()), "The color was already selected by another user, please select a new one"));
                    eventQueue.notifyAll();
                }
                return;

            }
        }
        player.setColor(color);
        this.answered.put(playerIndex, true);
        System.out.println(playerIndex + " chose his color, the number of answered is: " + numberAnswered());


        if (numberAnswered() == players.size()) {
            System.out.println("everyone answered");
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
            this.currentGamePhase.changePhase(this);
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
    public void setObjectives() {
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

        mainBoard.setSharedObjectiveCard(0, objectiveCard0);
        mainBoard.setSharedObjectiveCard(1, objectiveCard1);
        ArrayList<ObjectiveCard> sharedObjectives = new ArrayList<>();
        sharedObjectives.add(objectiveCard0);
        sharedObjectives.add(objectiveCard1);

        synchronized (eventQueue) {
            eventQueue.add(new UpdateSharedObjectiveEvent(this, allConnectedClients(), sharedObjectives));
            eventQueue.notifyAll();
        }
    }


    //called from controller
    public void setSecretObjective(int playerIndex, int cardIndex) {
        Player player = this.players.get(playerIndex);
        if (this.answered.get(playerIndex)) {
            synchronized (eventQueue) {
                eventQueue.add(new ErrorEvent(this, singleClient(player.getClient()), "You already selected the objective card"));
                eventQueue.notifyAll();
            }
            return;
        }

        if (cardIndex != 0 && cardIndex != 1) {
            synchronized (eventQueue) {
                eventQueue.add(new ErrorEvent(this, singleClient(player.getClient()), "The card chosen was not in the options list"));
                eventQueue.notifyAll();
            }
            return;
        }

        player.setObjectiveCard(player.getObjectiveCardOption(cardIndex));
        this.answered.put(playerIndex, true);
        System.out.println(playerIndex + " chose his objective, the number of ansewred is: " + numberAnswered());


        if (numberAnswered() == players.size()) {
            System.out.println("everyone answered");
            resetAnswered();
            this.currentGamePhase.changePhase(this);
            synchronized (eventQueue) {
                eventQueue.add(new UpdateCurrentPlayer(this, allConnectedClients(), currentPlayerIndex));
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
        //ANDRA MESSO NELLO STATE PATTERN
        synchronized (eventQueue) {
            eventQueue.add(new UpdateTurnPhaseEvent(this, allConnectedClients(), TurnPhase.DRAWPHASE));
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


    public void updateElements(Player player, CornerCard placingCard, Side placingCardSide) {
        for (Element ele : Element.values()) {
            if (player.getAllElements().get(ele) != null) {
                int currentOccurencies = player.getAllElements().get(ele);
                int newOccurencies = Collections.frequency(placingCard.getUncoveredElements(placingCardSide), ele);
                player.addToAllElements(ele, currentOccurencies + newOccurencies);
            }
        }
    }

    //ENDGAME

    public void checkGameEnded() {
        // 20 points?
        if (!currentGamePhase.equals(GamePhase.ENDPHASE)) {
            for (int i = 0; i < this.players.size(); i++) {
                if (this.players.get(i).getPoints() >= Config.POINTSTOENDPHASE) {
                    currentGamePhase = GamePhase.ENDPHASE;
                }
            }
            if (currentGamePhase.equals(GamePhase.ENDPHASE)) {
                //currentPlayer is already updated here, it's the one that plays next, right after the previous drew
                //currentPlayer = 0 => playerSize
                //currentPlayer = 1 => playerSize + playerSize - 1
                //currentPlayer = 2 => playerSize + playerSize - 2
                //currentPlayer = 3 => playerSize + playerSize - 3
                if (currentPlayerIndex == 0) {
                    this.turnToPlay = players.size();
                } else {
                    this.turnToPlay = 2 * players.size() - currentPlayerIndex;
                }
            }
        }

        // cards ended?
        if ((this.getMainBoard().isResourceDeckEmpty() && this.getMainBoard().isGoldDeckEmpty()) || turnToPlay == 0) {
            currentGamePhase = GamePhase.FINALPHASE;
            calculateFinalPoints();
            ArrayList<Integer> winners;
            try {
                winners = getWinnerPlayerIndex();
            } catch (GameIsNotEndedException e) {
                throw new RuntimeException(e);
            }
            synchronized (eventQueue) {
                eventQueue.add(new GameEndedEvent(this, allConnectedClients(), winners));
                eventQueue.notifyAll();
            }
            restart();
        }
    }


    void calculateFinalPoints() {
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
            if (this.players.get(i).getPoints() >= Config.POINTSTOENDPHASE) {
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
        if (!playerReachedPoints) {
            throw new GameIsNotEndedException("You called getWinnerPlayerIndex even if the game is not ended, no one is at 20 points");
        }

        // now winnerPlayerIndeces contains who has the maximum number of points
        // decide the real winner if there is a draw
        ArrayList<Integer> winnerObjectives = new ArrayList<>();
        if (winnerPlayerIndeces.size() > 1) {
            int mostObjectivesWins = -1;
            for (int i = 0; i < winnerPlayerIndeces.size(); i++) {
                if (this.players.get(i).getObjectivesWon() > mostObjectivesWins) {
                    winnerObjectives.clear();
                    winnerObjectives.add(i);
                    mostObjectivesWins = this.players.get(i).getObjectivesWon();
                } else if (this.players.get(i).getObjectivesWon() == mostObjectivesWins) {
                    winnerObjectives.add(i);
                }
            }
            winnerPlayerIndeces = winnerObjectives;
        }
        return (winnerPlayerIndeces);
    }

    private void restart(){
        pingRunning = false;
        executeEventRunning = false;
        saveStateRunning = false;
        executeEvents.interrupt();
        try {
            executeEvents.join();
        } catch (InterruptedException ignored) {}
        playerThreads.forEach(Thread::interrupt);
        pingThread.interrupt();
        saveStateThread.interrupt();
        server.restart();

        System.out.println(executeEvents.getState());
        System.out.println(pingThread.getState());
        playerThreads.forEach(e ->{
            System.out.println(e.getState());
        });
        Thread.currentThread().interrupt();


        System.out.println("---------GAME RESTARTED------");
    }

    private void saveStateThreadRunnable(){
        while (saveStateRunning){
            System.out.println("he");
            try {
                Thread.sleep(1000*Config.WAIT_FOR_SAVE_TIME);
            } catch (InterruptedException e) {
                break;
            }
            Populate.saveState(this);
            Populate.restoreState(this);
        }
    }


//------------ others --------------------

    // !!! to implement !!!
    public void saveGameState() {
    }

    // !!! optional !!!
    public int getTurnTime() {
        return 0;
    }
}