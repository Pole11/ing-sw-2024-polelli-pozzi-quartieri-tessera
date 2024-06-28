package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;

import com.google.gson.Gson;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Color;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.events.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.InternetCheck;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Populate;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * GameState Class
 */
public class GameState {
    private final Server server;

    /**
     * Map of all the cards, that matches an ID with the proper card
     */
    private final HashMap<Integer, Card> cardsMap;
    /**
     * Main board of the game, that contains all deck and shared cards
     */
    private final Board mainBoard;
    /**
     * Chat of the game, that contains all messages
     */
    private Chat chat;
    /**
     * List of all the playing players, player[0] is black Player and the first to start
     */
    private ArrayList<Player> players;
    /**
     * Index of the current playing player
     */
    private int currentPlayerIndex;
    /**
     * Current game phase
     */
    private GamePhase currentGamePhase;
    /**
     * Current turn phase
     */
    private TurnPhase currentGameTurn;
    /**
     * Queue o all the events that the clint could do (for synchronization)
     */
    private final ArrayDeque<Event> eventQueue;
    /**
     * Thread that reads and manages the queque
     */
    private final Thread executeEvents;
    /**
     * Flag for executing executeEvents
     */
    private boolean executeEventRunning;
    /**
     * Map that set if a player has answered to the request or not (useful for starting phases of the game)
     */
    private HashMap<Integer, Boolean> answered;
    /**
     * Thread that manages pings
     */
    private final Thread pingThread;
    /**
     * Flag for executing pingThread
     */
    private boolean pingRunning;
    /**
     * Thread list that manages all the players, one for each player
     */
    private ArrayList<Thread> playerThreads;
    /**
     * Thread that manages timeout for curent player
     */
    private Thread timeoutThread;
    /**
     * Saving of the previous game phase
     */
    private GamePhase prevGamePhase;

    /**
     * Number of turns left to play
     */
    private int turnToPlay;
    /**
     * List of all UpdateBoardEvent sent to all the clients in the game, it is used by restoreView
     */
    private ArrayList<UpdateBoardEvent> placedEventList;
    /**
     * List of threads, one for each player, that execute all the events for that player
     */
    private ArrayList<Thread> executingThreads;
    /**
     * List of queues, one for each player, that contains all the events for that player
     */
    private ArrayList<ArrayDeque<Event>> allEventQueues ;
    /**
     * GameState Constructor
     */
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


        this.playerThreads = new ArrayList<>();

        this.pingRunning = true;
        this.pingThread = new Thread(this::pingThreadRunnable);


        this.answered = new HashMap<>();
        resetAnswered();

        this.turnToPlay = Integer.MAX_VALUE;
        this.placedEventList = new ArrayList<>();
        this.allEventQueues = new ArrayList<>();
        this.executingThreads = new ArrayList<>();
    }

    public void startThreads(){
        this.executeEvents.start();
        this.pingThread.start();
    }



    // GETTER

    public HashMap<Integer, Boolean> getAnswered(){
        return new HashMap<>(answered);
    }

    public int getTurnToPlay(){
        return turnToPlay;
    }

    public GamePhase getPrevGamePhase(){
        return prevGamePhase;
    }

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

    public ArrayList<UpdateBoardEvent> getPlacedEventList(){
        return new ArrayList(placedEventList);
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

    public void setAnswered(Integer index, boolean ans){
        answered.put(index, ans);
    }

    public void setPrevGamePhase(GamePhase gamePhase){
        this.prevGamePhase = gamePhase;
    }

    public void setTurnToPlay(int turnToPlay) {
        this.turnToPlay = turnToPlay;
    }

    //ADDER/REMOVER

    /**
     * Add a card to the gamestate cards
     * @param cardId Card Identifier
     * @param card Card object
     */
    public void addCardToCardsMap(int cardId, Card card) {
        this.cardsMap.put(cardId, card);
    }

    /**
     * Add event to the queue
     * @param event Event to add
     */
    public void addToEventQueue(Event event) {
        this.eventQueue.add(event);
    }

    //EVENTQUEUE

    /**
     * Resets the number of answers (even if there aren't always 4 players)
     */
    public void resetAnswered() {
        this.answered.put(0, false);
        this.answered.put(1, false);
        this.answered.put(2, false);
        this.answered.put(3, false);
    }

    /**
     * Get the number of player that has answered the request
     * @return Number of answers
     */
    public int numberAnswered() {
        return this.answered.values().stream().mapToInt(e -> e ? 1 : 0).sum();
    }

    /**
     * Run the event queue of events
     */
    private void executeEventsRunnable()  {
        while (executeEventRunning) {
            Event event = null;
            synchronized (eventQueue) {
                while (eventQueue.isEmpty()) {
                    try {
                        eventQueue.wait();
                    } catch (InterruptedException e) {
                        System.out.println("GAMESTATE: execute events interrupted");
                        return;
                    }
                }

                event = eventQueue.remove();

            }

            for (VirtualView client : event.getClients()){
                List<VirtualView> clientsTmp = players.stream().map(Player::getClient).toList();
                if (clientsTmp.contains(client)){
                    int index = getPlayerIndex(client);
                    synchronized (allEventQueues.get(index)){
                        ArrayList<VirtualView> clients = new ArrayList<>();
                        clients.add(client);
                        Event cloned;
                        try {
                            cloned = event.clone();
                        } catch (CloneNotSupportedException e) {
                            throw new RuntimeException(e);
                        }
                        cloned.setClients(clients);
                        allEventQueues.get(index).add(cloned);
                        allEventQueues.get(index).notifyAll();
                    }
                } else {
                    System.out.println(event);
                    new Thread(event::execute).start();
                }

            }

            Populate.saveState(this);
        }
    }

    /**
     * Run the event queue of events for the specific player
     * @param index index of the player
     */
    public void executingThreadsRunnable(int index){
        while (true){
            Event event = null;
            synchronized (allEventQueues.get(index)) {
                while (allEventQueues.get(index).isEmpty()) {
                    try {
                        allEventQueues.get(index).wait();
                    } catch (InterruptedException e) {
                        System.out.println("execute events interrupted");
                        return;
                    }
                }
                event = allEventQueues.get(index).remove();
            }
            event.execute();

        }
    }

    /**
     * Trasform a client to a list
     * @param client Selected client
     * @return List of the single client
     */
    public ArrayList<VirtualView> singleClient(VirtualView client) {
        ArrayList<VirtualView> clients = new ArrayList<>();
        if (client != null && getPlayer(getPlayerIndex(client)).isConnected()){
            clients.add(client);
        }
        return clients;
    }

    /**
     * Get a list of all the cient different from the selected one
     * @param client Client to exclude
     * @return List of the other clients
     */
    public ArrayList<VirtualView> otherClients(VirtualView client) {
        ArrayList<VirtualView> clients = new ArrayList<>();
        for (Player playerIterator : players) {
            if (playerIterator.isConnected() && !client.equals(playerIterator.getClient())) {
                if (playerIterator.getClient() != null){
                    clients.add(playerIterator.getClient());
                }
            }
        }
        return clients;
    }

    /**
     * Get all the clients in the gamestate
     * @return List of all clients
     */
    public ArrayList<VirtualView> allConnectedClients() {
        return (ArrayList<VirtualView>) players.stream().filter(Player::isConnected).filter(e -> e.getClient() != null).map(Player::getClient).collect(Collectors.toList());
    }

    public ArrayList<VirtualView> allClients() {
        return (ArrayList<VirtualView>) players.stream().filter(e -> e.getClient() != null).map(Player::getClient).collect(Collectors.toList());
    }


    //DISCONNECTIONS
    /**
     * Run the ping thread
     */
    public void pingThreadRunnable() {
        while (pingRunning) {
            for (int i = 0; i < playerThreads.size(); i++){
                int finalI = i;
                if (!playerThreads.get(i).isAlive()){
                    playerThreads.set(i, new Thread(() -> {
                        int j = finalI;
                        try {
                            //wait for the answer of players
                            Thread.sleep(1000*Config.WAIT_FOR_PING_TIME);
                            if (players.get(j).isConnected()){
                                synchronized (players) {
                                    players.get(j).setConnected(false);
                                }
                                playerDisconnected(j);
                            }
                        } catch (InterruptedException e) {
                        }
                    }));
                    playerThreads.get(i).start();
                }

            }

            updatePlayersConnected();

            try {
                //wait to ping another time
                Thread.sleep(1000*Config.NEXT_PING_TIME);
            } catch (InterruptedException e) {
                pingRunning = false;
                break;
            }
        }
    }

    public void addPlayerThread(int index){
        playerThreads.add(new Thread());
        allEventQueues.add(new ArrayDeque<Event>());
        Thread thread = new Thread(()->{
            executingThreadsRunnable(index);
        });
        executingThreads.add(thread);
        executingThreads.getLast().start();
    }



    /**
     * Get the ping response from a player
     * @param client The client who has responded
     */
    public void pingAnswer(VirtualView client) {
        //System.out.println(getPlayerIndex(client));
        synchronized (players) {
            playerThreads.get(getPlayerIndex(client)).interrupt();
            try {
                playerThreads.get(getPlayerIndex(client)).join();
            } catch (InterruptedException e) {
                System.err.println("playerThread interrupted from outside");
            }
        }
    }

    /**
     * Set the connection of a player (null or connected)
     * @param index Player index
     * @param connected Connection status
     */
    public void setPlayersConnected(int index, boolean connected) {
        synchronized (players){
            players.get(index).setConnected(connected);
        }
        //players.get(index).setClient(null);
    }

    /**
     * Manage the reconnection of a player
     * @param player Player to be reconnected
     */
    public void manageReconnection(Player player) {
        if (this.currentGamePhase.equals(GamePhase.TIMEOUT)) {
            if(this.timeoutThread != null) {
                this.timeoutThread.interrupt();
            }
            currentGamePhase = prevGamePhase;
            prevGamePhase = null;
            players.stream().forEach(e-> {
                if (!e.isConnected() && currentPlayerIndex == getPlayerIndex(e)){
                    currentGameTurn = TurnPhase.DRAWPHASE;
                    changeCurrentPlayer();
                }
            });
        }
        restoreView(player.getClient());
        synchronized (players) {
            player.setConnected(true);
        }
    }

    public void addPlacedEvent(UpdateBoardEvent event){
        placedEventList.add(event);
    }
    
    
    /**
     * View restoration for reconnection
     * @param client Client to be reconnected
     */
    public void restoreView(VirtualView client) {
        //send everything to client
        ArrayList<VirtualView> clients = new ArrayList<>();
        clients.add(client);
        Player reconnectingPlayer = getPlayer(getPlayerIndex(client));

        synchronized (eventQueue) {
            eventQueue.add(new RedirectOutEvent(this, clients, true));
            GamePhase gamePhase = currentGamePhase;
            if (currentGamePhase.equals(GamePhase.TIMEOUT)){
                gamePhase = prevGamePhase;
            }
            //send player index
            if (reconnectingPlayer.getNickname() != null) {
                eventQueue.add(new SendIndexEvent(this, clients, getPlayerIndex(client)));
            }
            //for every player, send nickname, isConnected, color, points, hands
            for (int i = 0; i < players.size(); i++) {
                Player currentPlayer = players.get(i);
                eventQueue.add(new NicknameEvent(this, clients, i, currentPlayer.getNickname()));
                if (i != getPlayerIndex(client)){
                    eventQueue.add(new ConnectionInfoEvent(this, clients, currentPlayer, currentPlayer.isConnected()));
                }
                if (currentPlayer.getColor() != null) {
                    eventQueue.add((new UpdateColorEvent(this, clients, currentPlayer, currentPlayer.getColor())));
                }
                //send hands
                if (gamePhase.ordinal() >= GamePhase.CHOOSEOBJECTIVEPHASE.ordinal()) {
                    Set<Integer> handCardsSet = currentPlayer.getHandCardsMap().keySet();
                    for (Integer k : handCardsSet) {
                        eventQueue.add(new UpdateAddHandEvent(this, clients, currentPlayer, k));
                    }
                }
                //send starter
                if (currentPlayer.getStarterCard() != null) {
                    Side starterSide = currentPlayer.getPlacedCardSide(currentPlayer.getStarterCard().getId());
                    if (i == getPlayerIndex(client)){
                        eventQueue.add(new UpdateStarterCardEvent(this, clients, getPlayerIndex(client), currentPlayer.getStarterCard().getId(), null));
                    }
                    eventQueue.add(new UpdateStarterCardEvent(this, clients, i, currentPlayer.getStarterCard().getId(), starterSide));
                }
                HashMap<Element, Integer> elements = currentPlayer.getAllElements();
                int finalI = i;
                elements.keySet().stream().forEach(e->{
                    eventQueue.add(new UpdateElementsEvent(this, clients, finalI, e, elements.get(e)));
                });
                //eventQueue.add(new UpdatePointsEvent(this, clients, currentPlayer, currentPlayer.getPoints()));
            }


            //send common objectives
            if (gamePhase.ordinal() >= GamePhase.CHOOSEOBJECTIVEPHASE.ordinal()) {
                ArrayList<ObjectiveCard> sharedObjectives = new ArrayList<>();
                sharedObjectives.add(mainBoard.getSharedObjectiveCard(0));
                sharedObjectives.add(mainBoard.getSharedObjectiveCard(1));
                if (sharedObjectives.get(0) != null && sharedObjectives.get(1) != null){
                    eventQueue.add((new UpdateSharedObjectiveEvent(this, clients, sharedObjectives)));
                }
            }
            //send correct secret objective
            ArrayList<ObjectiveCard> secretObjectives = new ArrayList<>();
            //if the secret objective is already choosen
            if (reconnectingPlayer.getObjectiveCard() != null) {
                secretObjectives.add(reconnectingPlayer.getObjectiveCard());
                eventQueue.add(new UpdateSecretObjectiveEvent(this, clients, secretObjectives));
            } else if (reconnectingPlayer.getObjectiveCardOptions()[0] == null && reconnectingPlayer.getObjectiveCardOptions()[1] == null) { }  //if the secret objective options is not already given
            else { //the player has to choose between two secrets
                secretObjectives.add(reconnectingPlayer.getObjectiveCardOption(0));
                secretObjectives.add(reconnectingPlayer.getObjectiveCardOption(1));
                eventQueue.add(new UpdateSecretObjectiveEvent(this, clients, secretObjectives));
            }
            // send turnPhase if exists
            if (gamePhase.ordinal() >= GamePhase.MAINPHASE.ordinal()) {
                eventQueue.add(new UpdateTurnPhaseEvent(this, clients, currentGameTurn));
            }
            // send current player
            if (gamePhase.ordinal() >= GamePhase.MAINPHASE.ordinal()) {
                eventQueue.add(new UpdateCurrentPlayerEvent(this, clients, currentPlayerIndex));
            }


            // for every placingCardEvent, place a card in the boardsMap and in placedOrderCardMap
            placedEventList.stream().forEach(e -> {
                e.setClients(clients); //events in placedEventList have the attribute 'clients' empty, it needs to be setted
                eventQueue.add(e);
            });
            // send mainBoard
            if(gamePhase.ordinal() >= GamePhase.CHOOSESTARTERSIDEPHASE.ordinal()) {
                eventQueue.add(new UpdateMainBoardEvent(this, clients, mainBoard));
            }

            //send points
            for(int i = 0; i < players.size(); i++){
                Player currentPlayer = players.get(i);
                eventQueue.add(new UpdatePointsEvent(this, clients, currentPlayer, currentPlayer.getPoints()));
            }
            eventQueue.add(new RedirectOutEvent(this, clients, false));
            eventQueue.notifyAll();

        }
    }

    /**
     * Set and verify the disconnection of a player, and manage the thread call
     * @param index index of the player to be reconnected
     */
    public synchronized void playerDisconnected(int index) {
        if (currentGamePhase.equals(GamePhase.NICKNAMEPHASE)) {
            return;
        }

        long numberConnected;
        synchronized (players) {
            numberConnected = players.stream().filter(Player::isConnected).count();
        }
        //start timeout phase
        if (numberConnected <= 1 && !currentGamePhase.equals(GamePhase.TIMEOUT)) {
            //check if the server is connected
            if (InternetCheck.isConnectedToInternet()){
                this.prevGamePhase = currentGamePhase;
                this.currentGamePhase = GamePhase.TIMEOUT;
                synchronized (eventQueue){
                    eventQueue.add(new UpdateGamePhaseEvent(this, allConnectedClients(), currentGamePhase));
                    eventQueue.notifyAll();
                }
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
                        System.out.println("I am timeout ended");
                        gameEnded(winners);
                        System.out.println(allConnectedClients());
                        if (allConnectedClients().size() == 0) {
                            server.restart();
                            Thread.currentThread().interrupt();
                            System.out.println("---------GAME RESTARTED------");
                        }
                        System.out.println("game ended after timeout expired");
                    } catch (InterruptedException e) {
                        System.out.println("timout ended");
                    }
                });
                timeoutThread.start();
            }
        } else {
            //automatically plays for the player disconnected
            switch (currentGamePhase) {
                case CHOOSESTARTERSIDEPHASE -> setStarterSide(index, Side.BACK);
                //choose between the availables
                case CHOOSECOLORPHASE ->
                        setColor(index, Arrays.stream(Color.values()).filter(e -> !players.stream().map(Player::getColor).toList().contains(e)).findFirst().get());
                case CHOOSEOBJECTIVEPHASE -> setSecretObjective(index, 0);
            }
            if (numberAnswered() == players.size()) {
                currentGamePhase.changePhase(this);
                resetAnswered();
            }
        }
        if (currentPlayerIndex == index){
            if (currentGamePhase.ordinal() >= GamePhase.MAINPHASE.ordinal()) {
                if (currentGameTurn.equals(TurnPhase.DRAWPHASE)){
                    ResourceCard newResourceCard = null;
                    GoldCard newGoldCard = null;
                    try {
                        newResourceCard = getMainBoard().drawFromResourceDeck();
                    } catch (EmptyDeckException emptyResourceDeck) {
                        emptyResourceDeck.printStackTrace();
                        checkGameEnded();
                        try {
                            newResourceCard = getMainBoard().drawSharedResourceCard(1);
                        } catch (EmptyMainBoardException emptyResourceShared1) {
                            emptyResourceShared1.printStackTrace();
                            checkGameEnded();
                        }
                    }
                    Player currentPlayer = getPlayer(index);

                    if (newResourceCard != null) currentPlayer.addToHandCardsMap(newResourceCard.getId(), Side.FRONT);
                    else if(newGoldCard != null) currentPlayer.addToHandCardsMap(newGoldCard.getId(), Side.FRONT);
                    else return;

                    synchronized (eventQueue){
                        eventQueue.add(new UpdateMainBoardEvent(this, allConnectedClients(), mainBoard));
                        eventQueue.notifyAll();
                    }
                }
                currentGameTurn = TurnPhase.DRAWPHASE;
                changeCurrentPlayer();
            }
        }
        System.out.println("GAMESTATE: currente game turn is " + currentGameTurn);
    }

    /**
     * Change the current playing player
     */
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
        this.currentGameTurn.changePhase(this);

        synchronized (eventQueue) {
            eventQueue.add(new UpdateCurrentPlayerEvent(this, allConnectedClients(), currentPlayerIndex));
            eventQueue.notifyAll();
        }
        if (currentGamePhase.equals(GamePhase.ENDPHASE)) {
            if (turnToPlay <= 0) checkGameEnded();
            turnToPlay--;
        }
    }
    /**
     * Send the ping to verify connection
     */
    public void updatePlayersConnected() {
        synchronized (eventQueue){
            eventQueue.add(new PingEvent(this, allConnectedClients()));
            eventQueue.notifyAll();
        }
    }


    //PLAYERS MANAGING

    /**
     * Get the current playing player
     * @return Current player
     */
    public Player getCurrentPlayer() {
        return players.get(this.currentPlayerIndex);
    }

    /**
     * Get the player index of the requested client
     * @param client Virtualview of the client
     * @return Player index
     */
    public Integer getPlayerIndex(VirtualView client) {
        int i = 0;
        for (Player playerIterator : this.players) {
            if (playerIterator.getClient() != null && playerIterator.getClient().equals(client)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    /**
     * Get the player index of the requested player
     * @param player Selected player
     * @return Player index
     */
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

    /**
     * Add a player to the game
     * @param nickname Nickname of the player
     * @param client VirtualView of the client
     */
    public void addPlayer(String nickname, VirtualView client) {
        Player player = new Player(nickname, client, this);
        //if someone choose his nickname, he is inside the match, even if he is temporanely disconnected
        if (allConnectedClients().contains(client)) {
            synchronized (eventQueue) {
                eventQueue.add(new ErrorEvent(this, singleClient(client), "You already chose a nickname, you cannot change it"));
                eventQueue.notifyAll();
            }
            return;
        }

        //checks for reconnections or same nickname exception
        for (int j = 0; j < players.size(); j++) {

            if (player.getNickname().equals(players.get(j).getNickname())) {
                //takes for granted that player connection is updated
                //if the player is not connected but the gameState doesn't know, the following code fails
                if (!players.get(j).isConnected()) {
                    playerThreads.get(j).interrupt();
                    players.get(j).setClient(client);
                    this.manageReconnection(players.get(j));
                    synchronized (eventQueue) {
                        eventQueue.add(new UpdateGamePhaseEvent(this, singleClient(client), this.currentGamePhase));
                        eventQueue.notifyAll();
                    }
                } else {
                    if (allConnectedClients().size()<Config.MAX_PLAYERS){
                            synchronized (eventQueue) {
                            ArrayList<VirtualView> clients = new ArrayList<>();
                            clients.add(client);
                            eventQueue.add(new ErrorEvent(this, clients, "The nickname is already used, please choose another one"));
                            eventQueue.notifyAll();
                        }
                    }
                }
                return;
            }
        }

        if (players.size() >= Config.MAX_PLAYERS) {
            synchronized (eventQueue) {
                ArrayList<VirtualView> clients = new ArrayList<>();
                clients.add(client);
                eventQueue.add(new ErrorEvent(this, clients, "The game is full"));
                eventQueue.notifyAll();
            }
            return;
        }


        if (currentGamePhase.equals(GamePhase.NICKNAMEPHASE)){
            players.add(player);
            playerThreads.add(new Thread());
            allEventQueues.add(new ArrayDeque<Event>());
            Thread thread = new Thread(()->{
                executingThreadsRunnable(getPlayerIndex(player));
            });
            executingThreads.add(thread);
            executingThreads.getLast().start();
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
            System.out.println("GAMESTATE: " + nickname + " connected");
        } else {
            ArrayList<VirtualView> clients = new ArrayList<>();
            clients.add(client);
            synchronized (eventQueue) {
                eventQueue.add(new ErrorEvent(this, clients, "the game is already started"));
                eventQueue.notifyAll();
            }
        }
    }

    /**
     * Add a player to the list of players
     * @param player player to be added
     */
    public void addPlayer(Player player){
        players.add(player);
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

    /**
     * Function to start the game
     * @param client Client that started the game
     * @throws EmptyDeckException Deck it's not initialized
     */
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
        System.out.println("GAMESTATE: Starting game");
        this.currentGamePhase.changePhase(this);
    }

    /**
     * Add events to the queue for updating mainBoard in the views
     */
    public void updateMainBoard(){
        synchronized (eventQueue) {
            eventQueue.add(new UpdateMainBoardEvent(this, allConnectedClients(), mainBoard));
            eventQueue.notifyAll();
        }
    }

    /**
     * Initialization of starter cards
     */
    public void initStarters() {
        // for every player set his starters (you have access to every player from the array players)

        Set<Integer> randomKeysSet = new HashSet<>(); // a set has no duplicates
        // Generate four different random numbers
        while (randomKeysSet.size() < players.size()) {
            int randomNumber = Global.firstStarterCardId + ThreadLocalRandom.current().nextInt(Config.STARTERQTY); // Adjust range as needed
            randomKeysSet.add(randomNumber);
        }

        ArrayList<Integer> randomKeysList = new ArrayList<>(randomKeysSet); // convert to a list to iterate
        for (int i = 0; i < randomKeysList.size(); i++) {
            StarterCard starterCard = (StarterCard) cardsMap.get((randomKeysList.get(i)));
            players.get(i).setStarterCard(starterCard); // we might pass it the map of all the cards
            players.get(i).initializeBoard(); // add the starter card to the board of the player
        }
    }

    /**
     * Set player choice for starter card side
     * @param playerIndex Player who wants to set the side
     * @param side Side chosen
     */
    public void setStarterSide(int playerIndex, Side side) {
        players.stream().filter(e -> !e.isConnected() && !answered.get(getPlayerIndex(e))).forEach(e->{
            e.addToPlacedCardsMap(e.getStarterCard().getId(), Side.BACK);
            updateElements(e, e.getStarterCard(), Side.BACK);
            synchronized (eventQueue) {
                eventQueue.add(new UpdateStarterCardEvent(this, allConnectedClients(), getPlayerIndex(e), e.getStarterCard().getId(), Side.BACK));
                eventQueue.notifyAll();
            }
            this.answered.put(getPlayerIndex(e), true);
            System.out.println(e.getNickname() + " automatically chose his starter, the number of answered is: " + numberAnswered());
            if (numberAnswered() == players.size()){
                currentGamePhase.changePhase(this);
                resetAnswered();
            }
        });

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
        updateElements(player, playerStarterCard,side);

        synchronized (eventQueue) {
            eventQueue.add(new UpdateStarterCardEvent(this, allConnectedClients(), playerIndex, playerStarterCard.getId(), side));
            eventQueue.notifyAll();
        }


        this.answered.put(playerIndex, true);
        System.out.println("GAMESTATE: " + playerIndex + " chose his startercard, the number of answered is: " + numberAnswered());
        //notify him and all the others about the change
        if (numberAnswered() == players.size()) {
            System.out.println("GAMESTATE: everyone answered");
            this.currentGamePhase.changePhase(this);
            resetAnswered();
        }
    }

    /**
     * Set player choice for the color
     * @param playerIndex Player who wants to set the color
     * @param color Color chosen
     */
    public void setColor(int playerIndex, Color color) {//method called by view when the player chooses the side
        //this is runned when a player select the color, all the other player not connected select it automatically
        players.stream().filter(e -> !e.isConnected() && e.getColor() == null).forEach(e->{
            Optional<Color> colorOptional = Arrays.stream(Color.values()).filter(p-> !players.stream().map(Player::getColor).toList().contains(p)).findFirst();
            if (colorOptional.isPresent()){
                e.setColor(colorOptional.get());
                this.answered.put(getPlayerIndex(e), true);
                System.out.println(e.getNickname() + " automatically chose his color, the number of answered is: " + numberAnswered());
                if (numberAnswered() == players.size()){
                    currentGamePhase.changePhase(this);
                    resetAnswered();
                }

            }

        });


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
        System.out.println("GAMESTATE:" + getPlayer(playerIndex).getNickname() + " chose his color, the number of answered is: " + numberAnswered());


        if (numberAnswered() == players.size()) {
            System.out.println("GAMESTATE:" + "everyone answered");
            resetAnswered();
            try {
                //send also the cards to the view
                this.setHands();
            } catch (EmptyDeckException e) {
                e.printStackTrace();
                // if in this moment of the game the deck is empty it's a programming mistake
                throw new RuntimeException(e);
            }
            //send also the cards to the view
            this.setObjectives();
            this.currentGamePhase.changePhase(this);
        }
    }

    /**
     * Initialization of players hands
     * @throws EmptyDeckException Deck is empty
     */
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

    /**
     * Set both secret and shared objectives for each player
     */
    public void setObjectives() {
        Set<Integer> randomKeysSet = new HashSet<>(); // a set has no duplicates
        // Generate four different random numbers
        while (randomKeysSet.size() < 2 * players.size() + 2) { // 2 for each player and 2 shared
            int randomNumber = Global.firstObjectiveCardId + ThreadLocalRandom.current().nextInt(Config.OBJECTIVEQTY); // Adjust range as needed
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

    /**
     * Set player choice of the secret objective
     * @param playerIndex Player who wants to set the objective
     * @param cardIndex Index that the player choose (0-1)
     */
    //called from controller
    public void setSecretObjective(int playerIndex, int cardIndex) {
        //this is runned when a player select the objective, all the other player not connected select it automatically
        players.stream().filter(e -> !e.isConnected() && e.getObjectiveCard() == null).forEach(e->{
            e.setObjectiveCard(e.getObjectiveCardOption(0));
            this.answered.put(getPlayerIndex(e), true);
            System.out.println(e.getNickname() + " automatically chose his objective, the number of ansewred is: " + numberAnswered());
            if (numberAnswered() == players.size()){
                currentGamePhase.changePhase(this);
                resetAnswered();
            }
        });

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
        System.out.println("GAMESTATE:" + getPlayer(playerIndex).getNickname() + " chose his objective, the number of answered is: " + numberAnswered());


        if (numberAnswered() == players.size()) {
            System.out.println("GAMESTATE:" + "everyone answered");
            resetAnswered();
            this.currentGamePhase.changePhase(this);
            currentPlayerIndex = players.size() -1;
            currentGameTurn = TurnPhase.DRAWPHASE;
            changeCurrentPlayer();
        }
    }


    //GAMEPHASE


//----------------------------place Card--------------------------------

    /**
     * Funzion for plaicing a card on the player board
     * @param player Player who is playcing the card
     * @param placingCardId Card to place
     * @param tableCardId Card on the table to be connected to
     * @param tableCornerPos Corner position of the table card for the connection
     * @param placingCornerPos Corner posizion of the hand card for the connection
     * @param placingCardSide Side of the playing card
     * @throws PlacingOnHiddenCornerException The corner is hidden
     */
    public void placeCard(Player player, int placingCardId, int tableCardId, CornerPos tableCornerPos, CornerPos placingCornerPos, Side placingCardSide) throws PlacingOnHiddenCornerException, CardNotPlacedException, CardNotOnBoardException {
        // check if the indirect corners are hidden
        CornerCard placingCard = (CornerCard) cardsMap.get(placingCardId);
        ArrayList<ArrayList<Integer>> playerBoard = player.getPlayerBoard();
        boolean tableCardExists = false;
        for (Integer row = 0; row < playerBoard.size() && !tableCardExists; row++) {
            for (Integer col = 0; col < playerBoard.get(row).size() && !tableCardExists; col++) {
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
                    tableCardExists = true;
                }
            }
        }

        if (!tableCardExists) throw new CardNotOnBoardException("You are trying to place on a card that is not on table");

        updateElements(player, placingCard, placingCardSide);
        System.out.println("GAMESTATE:" + player.getNickname() + " placed card " + placingCardId);
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

    /**
     * Update the elements in the player attributes
     * @param player Player to be updated
     * @param placingCard Card placed
     * @param placingCardSide Side of the placed card
     */
    public void updateElements(Player player, CornerCard placingCard, Side placingCardSide) {
        for (Element ele : Element.values()) {
            if (player.getAllElements().get(ele) != null) {
                int currentOccurencies = player.getAllElements().get(ele);
                int newOccurencies = Collections.frequency(placingCard.getUncoveredElements(placingCardSide), ele);
                player.addToAllElements(ele, currentOccurencies + newOccurencies);
            }
            eventQueue.add(new UpdateElementsEvent(this, allClients(), getPlayerIndex(player), ele, player.getAllElements().get(ele)));
        }
    }

    //ENDGAME

    /**
     * Check if game is ended by one of the possible conditions
     */
    public void checkGameEnded() {
        // switch to end phase
        if (!currentGamePhase.equals(GamePhase.ENDPHASE)) {
            // first endphase entering: player reach 20 points
            for (Player player : this.players) {
                if (player.getPoints() >= Config.POINTSTOENDPHASE) {
                    currentGamePhase.changePhase(this);
                    break;
                }
            }
            // second endphase entering: all decks are empty
            if (this.getMainBoard().isResourceDeckEmpty() && this.getMainBoard().isGoldDeckEmpty() && !currentGamePhase.equals(GamePhase.ENDPHASE)){
                currentGamePhase.changePhase(this);
            }

            //inizialize turnToPlay
            if (currentGamePhase.equals(GamePhase.ENDPHASE)) {

                if (currentPlayerIndex == 0) {
                    this.turnToPlay = players.size();
                } else {
                    this.turnToPlay = 2 * players.size() - currentPlayerIndex;
                }
            }
        }

        // switch to final phase
        if (turnToPlay == 0) {
            currentGamePhase.changePhase(this);

            calculateFinalPoints();
            ArrayList<Integer> winners;
            winners = getWinnerPlayerIndex();
            gameEnded(winners);
        }
    }

    /**
     * Calculation of the points at the end of the game
     */
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

    /**
     * Get the winner player
     * @return List of indexes of winners
     */
    public ArrayList<Integer> getWinnerPlayerIndex() {
        int maxPoints = 0;
        ArrayList<Integer> winnerPlayerIndeces = new ArrayList<>();
        boolean playerReachedPoints = false;
        for (int i = 0; i < this.players.size(); i++) {
            if (this.players.get(i).getPoints() > maxPoints) {
                winnerPlayerIndeces = new ArrayList<>();
                winnerPlayerIndeces.add(i);
                maxPoints = this.players.get(i).getPoints();
            } else if (this.players.get(i).getPoints() == maxPoints) {
                winnerPlayerIndeces.add(i);
            }
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

    private void gameEnded(ArrayList<Integer> winners){
        playerThreads.forEach(Thread::interrupt);
        pingRunning = false;
        pingThread.interrupt();
        synchronized (eventQueue){
            eventQueue.clear();
            //eventQueue.add(new UpdateGamePhaseEvent(this, allConnectedClients(), GamePhase.FINALPHASE));
            eventQueue.add(new GameEndedEvent(this, allConnectedClients(), winners));
            eventQueue.notifyAll();
        }
        executeEvents.interrupt();

        System.out.println(executeEvents.getState());
        System.out.println(pingThread.getState());
        playerThreads.forEach(e ->{
            System.out.println(e.getState());
        });

        //erase state
        Gson gson = new Gson();
        String filePath = new File("").getAbsolutePath() + Config.GAME_STATE_PATH;
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(new HashMap<>(), writer);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void clientEnded(VirtualView client){
        System.out.println("client " + getPlayer(getPlayerIndex(client)).getNickname() + " ended");
        answered.put(getPlayerIndex(client), true);
        if (numberAnswered() >= allConnectedClients().size()){
            server.restart();
            Thread.currentThread().interrupt();
            System.out.println("---------GAME RESTARTED------");
        }
    }

//------------ chat ---------------

    public void addMessage(int playerIndex, String content){
        this.chat.addMessage(playerIndex, content);

        synchronized (eventQueue) {
            eventQueue.add(new UpdateChatEvent(this, allConnectedClients(), playerIndex, content));
            eventQueue.notifyAll();
        }
    }

}