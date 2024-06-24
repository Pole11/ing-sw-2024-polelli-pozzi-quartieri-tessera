package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * Board class
 */
public class Board {
    /**
     * Gold cards shared between players
     */
    private GoldCard[] sharedGoldCards;
    /**
     * Resource cards shared between players
     */
    private ResourceCard[] sharedResourceCards;
    /**
     * Objective cards shared between players
     */
    private ObjectiveCard[] sharedObjectiveCards;
    /**
     * List of gold card in deck
     */
    private ArrayList<GoldCard> goldDeck;
    /**
     * List of resource card in deck
     */
    private ArrayList<ResourceCard> resourceDeck;
    /**
     * Board Constructor
     */
    public Board(){
        sharedGoldCards = new GoldCard[Config.N_SHARED_GOLDS];
        sharedResourceCards = new ResourceCard[Config.N_SHARED_RESOURCES];
        sharedObjectiveCards = new ObjectiveCard[Config.N_SHARED_OBJECTIVES];
        goldDeck = new ArrayList<>();
        resourceDeck  = new ArrayList<>();
    }

    //GETTER

    //----plain----

    public GoldCard[] getSharedGoldCards() {
        GoldCard[] copy = new GoldCard[sharedGoldCards.length];
        System.arraycopy(sharedGoldCards, 0, copy, 0, sharedGoldCards.length);
        return copy;
    }

    public ResourceCard[] getSharedResourceCards() {
        ResourceCard[] copy = new ResourceCard[sharedResourceCards.length];
        System.arraycopy(sharedResourceCards, 0, copy, 0, sharedResourceCards.length);
        return copy;
    }

    public ObjectiveCard[] getSharedObjectiveCards() {
        ObjectiveCard[] copy = new ObjectiveCard[sharedObjectiveCards.length];
        System.arraycopy(sharedObjectiveCards, 0, copy, 0, sharedObjectiveCards.length);
        return copy;
    }

    public ArrayList<GoldCard> getGoldDeck() {
        return new ArrayList<>(goldDeck);
    }

    public ArrayList<ResourceCard> getResourceDeck() {
        return new ArrayList<>(resourceDeck);
    }


    //-----card------

    public GoldCard getSharedGoldCard(int index) {
        return sharedGoldCards[index];
    }

    public ResourceCard getSharedResourceCard(int index) {
        return sharedResourceCards[index];
    }

    public ObjectiveCard getSharedObjectiveCard(int index) {
        return sharedObjectiveCards[index];
    }

    public GoldCard getFirstGoldDeckCard() throws NoSuchElementException {
        return goldDeck.getFirst();
    }

    public ResourceCard getFirstResourceDeckCard() throws NoSuchElementException {
        return resourceDeck.getFirst();
    }

    //----size-----

    public int getGoldDeckSize(){
        return goldDeck.size();
    }

    public int getResourceDeckSize(){
        return resourceDeck.size();
    }

    //SETTER

    public void setSharedGoldCard(int index, GoldCard sharedGoldCard) {
        this.sharedGoldCards[index] = sharedGoldCard;
    }

    public void setSharedResourceCard(int index, ResourceCard sharedResourceCard) {
        this.sharedResourceCards[index] = sharedResourceCard;
    }

    public void setSharedObjectiveCard(int index, ObjectiveCard sharedObjectiveCard) {
        this.sharedObjectiveCards[index] = sharedObjectiveCard;
    }

    public void setGoldDeck(ArrayList<GoldCard> goldDeck) {
        this.goldDeck = goldDeck;
    }

    public void setResourceDeck(ArrayList<ResourceCard> resourceDeck) {
        this.resourceDeck = resourceDeck;
    }


    //EMPTINESS

    /**
     * Check if the resource deck is empty
     * @return True if empty resource deck
     */
    public boolean isResourceDeckEmpty(){
        return resourceDeck.isEmpty();
    }

    /**
     * Check if the gold deck is empty
     * @return True if empty gold deck
     */
    public boolean isGoldDeckEmpty(){
        return goldDeck.isEmpty();
    }

    /**
     * Add a card to the gold deck
     * @param goldCard Gold card to add
     */
    public void addToGoldDeck(GoldCard goldCard) {
        goldDeck.add(goldCard);
    }

    /**
     * Add a card to the resource deck
     * @param resourceCard Resource card to add
     */
    public void addToResourceDeck(ResourceCard resourceCard) {
        resourceDeck.add(resourceCard);
    }

    //deck remover are not needed, because the card are removed when drawn

    //METHODS
    //------------inizitization-----------------

    /**
     * Initialize the shared gold cards on the board
     * @throws EmptyDeckException Deck is empty
     */
    public void initSharedGoldCards() throws EmptyDeckException {
        // it's like fillSharedGaps, look at the draw from shared methods to take inspiration
        this.sharedGoldCards[0] = drawFromGoldDeck();
        this.sharedGoldCards[1] = drawFromGoldDeck();
    }

    /**
     * Initialize the shared resource cards on the board
     * @throws EmptyDeckException Deck is empty
     */
    public void initSharedResourceCards() throws EmptyDeckException {
        this.sharedResourceCards[0] = drawFromResourceDeck();
        this.sharedResourceCards[1] = drawFromResourceDeck();
    }

    /**
     * Shuffle decks
     */
    public void shuffleCards() {
        Random randStream = new Random();
        for (int i = 0; i < this.goldDeck.size(); i++) {
            int j = i + randStream.nextInt(this.goldDeck.size()-i);
            GoldCard temp = this.goldDeck.get(i);
            this.goldDeck.set(i, this.goldDeck.get(j));
            this.goldDeck.set(j, temp);
        }

        for (int i = 0; i < this.resourceDeck.size(); i++) {
            int j = i + randStream.nextInt(this.resourceDeck.size()-i);
            ResourceCard temp2 = this.resourceDeck.get(i);
            this.resourceDeck.set(i, this.resourceDeck.get(j));
            this.resourceDeck.set(j, temp2);
        }
    }

    //-------------drawCard Methods-----------------------

    /**
     * Get a card from the shared golds, removing it from the shared golds
     * @param pos Position of the shared gold card (1-2)
     * @return Gold card drawn
     * @throws EmptyMainBoardException A Card in the main board is not present
     */
    public GoldCard drawSharedGoldCard(int pos) throws EmptyMainBoardException {
        // verify position is valid
        if (pos < 1 || pos > this.sharedGoldCards.length) {
            throw new IllegalArgumentException("invalid position: " + pos);
        }
        if (this.sharedGoldCards[pos-1] == null){
            throw new EmptyMainBoardException("This shared gold card is not present");
        }
        // get the card
        GoldCard drawnCard = this.sharedGoldCards[pos - 1];
        // replace card in shared
        try {
            this.sharedGoldCards[pos - 1] = drawFromGoldDeck();
        } catch (EmptyDeckException e) {
            this.sharedGoldCards[pos - 1] = null;
        }
        // return the specified card
        return drawnCard;
    }

    /**
     * Get a card from the shared resources, removing it from the shared resources
     * @param pos Position of the shared resource card (1-2)
     * @return Resource card drawn
     * @throws EmptyMainBoardException A Card in the main board is not present
     */
    public ResourceCard drawSharedResourceCard(int pos) throws EmptyMainBoardException {
        // verify position is valid
        if (pos < 1 || pos > this.sharedResourceCards.length) {
            throw new IllegalArgumentException("Invalid position: " + pos);
        }
        if (this.sharedResourceCards[pos-1] == null){
            throw new EmptyMainBoardException("This shared gold card is not present");
        }
        // get the specified card
        ResourceCard drawnCard = this.sharedResourceCards[pos - 1];
        // replace card in shared
        try {
            this.sharedResourceCards[pos - 1] = drawFromResourceDeck();
        } catch (EmptyDeckException e) {
            this.sharedGoldCards[pos - 1] = null;
        }
        // return the card
        return drawnCard;
    }

    /**
     * Get a card from the resource deck, removing it from the deck
     * @return Resource card drawn
     * @throws EmptyDeckException Deck is empty
     */
    //return and remove
    public ResourceCard drawFromResourceDeck() throws EmptyDeckException {
        if (resourceDeck.isEmpty()) throw new EmptyDeckException("The resource deck is empty");
        ResourceCard drawnCard = this.resourceDeck.getFirst();
        resourceDeck.removeFirst();
        return drawnCard;
    }

    /**
     * Get a card from the gold deck, removing it from the deck
     * @return Gold card drawn
     * @throws EmptyDeckException Deck is empty
     */
    public GoldCard drawFromGoldDeck() throws EmptyDeckException {
        if (goldDeck.isEmpty()) throw new EmptyDeckException("The gold deck is empty");
        GoldCard drawnCard = this.goldDeck.getFirst();
        goldDeck.removeFirst();
        return drawnCard;
    }
}