package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Board {
    private GoldCard[] sharedGoldCards; // 2 gold cards shared between players
    private ResourceCard[] sharedResourceCards; // 2 resource cards shared between players
    private ObjectiveCard[] sharedObjectiveCards; // 2 objective cards shared between players
    private final ArrayList<GoldCard> goldDeck; // may be final (the reference only)
    private final ArrayList<ResourceCard> resourceDeck; // may be final (the reference only)

    public Board(){
        sharedGoldCards = new GoldCard[Config.N_SHARED_GOLDS];
        sharedResourceCards = new ResourceCard[Config.N_SHARED_RESOURCES];
        sharedObjectiveCards = new ObjectiveCard[Config.N_SHARED_OBJECTIVES];
        goldDeck = new ArrayList<>();
        resourceDeck  = new ArrayList<>();
    }

    //GETTER

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

    public GoldCard getFirstGoldDeckCard(){
        return goldDeck.getFirst();
    }

    public ResourceCard getFirstResourceDeckCard(){
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

    //EMPTINESS

    public boolean isResourceDeckEmpty(){
        return resourceDeck.isEmpty();
    }

    public boolean isGoldDeckEmpty(){
        return goldDeck.isEmpty();
    }


    //SETTER
    /*
    public void setSharedGoldCards(GoldCard[] sharedGoldCards) {
        this.sharedGoldCards = sharedGoldCards;
    }

    public void setSharedResourceCards(ResourceCard[] sharedResourceCard) {
        this.sharedResourceCards = sharedResourceCard;
    }

    public void setSharedObjectiveCards(ObjectiveCard[] sharedObjectiveCards) {
        this.sharedObjectiveCards = sharedObjectiveCards;
    }

    //GETTER
    public GoldCard[] getSharedGoldCards() {
        return sharedGoldCards;
    }

    public ResourceCard[] getSharedResourceCards() {
        return sharedResourceCards;
    }

    public ObjectiveCard[] getSharedObjectiveCards() {
        return sharedObjectiveCards;
    }


    public ArrayList<GoldCard> getGoldDeck() {
        return new ArrayList<>(goldDeck);
    }

    public ArrayList<ResourceCard> getResourceDeck() {
        return new ArrayList<>(resourceDeck);
    }
    */
    //ADDER

    public void addToGoldDeck(GoldCard goldCard) {
        goldDeck.add(goldCard);
    }

    public void addToResourceDeck(ResourceCard resourceCard) {
        resourceDeck.add(resourceCard);
    }

    //deck remover are not needed, because the card are remove in the drawcard in this class


    //METHODS

    //------------inizitization-----------------

    public void initSharedGoldCards() throws EmptyDeckException {
        // it's like fillSharedGaps, look at the draw from shared methods to take inspiration
        this.sharedGoldCards[0] = drawFromGoldDeck();
        this.sharedGoldCards[1] = drawFromGoldDeck();
    }

    public void initSharedResourceCards() throws EmptyDeckException {
        this.sharedResourceCards[0] = drawFromResourceDeck();
        this.sharedResourceCards[1] = drawFromResourceDeck();
    }


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

    // returns the card and replace shared
    public GoldCard drawSharedGoldCard(int pos) throws EmptyDeckException {
        // verify position is valid
        if (pos < 1 || pos > this.sharedGoldCards.length) {
            throw new IllegalArgumentException("invalid position: " + pos);
        }
        // get the card
        GoldCard drawnCard = this.sharedGoldCards[pos - 1];
        // replace card in shared
        this.sharedGoldCards[pos - 1] = drawFromGoldDeck();
        // return the specified card
        return drawnCard;
    }

    // returns the card and replace shared
    public ResourceCard drawSharedResourceCard(int pos) throws EmptyDeckException {
        // verify position is valid
        if (pos < 1 || pos > this.sharedResourceCards.length) {
            throw new IllegalArgumentException("Invalid position: " + pos);
        }
        // get the specified card
        ResourceCard drawnCard = this.sharedResourceCards[pos - 1];
        // replace card in shared
        this.sharedResourceCards[pos - 1] = drawFromResourceDeck();
        // return the card
        return drawnCard;
    }

    //return and remove
    public ResourceCard drawFromResourceDeck() throws EmptyDeckException {
        if (resourceDeck.isEmpty()) throw new EmptyDeckException("The resource deck is empty");
        ResourceCard drawnCard = this.resourceDeck.getLast();
        resourceDeck.removeLast();
        return drawnCard;
    }

    //return and remove
    public GoldCard drawFromGoldDeck() throws EmptyDeckException {
        if (goldDeck.isEmpty()) throw new EmptyDeckException("The gold deck is empty");
        GoldCard drawnCard = this.goldDeck.getLast();
        goldDeck.removeLast();
        return drawnCard;
    }

    /*
    public void fillSharedCardsGap(){
        // verify if the shared cards have some gaps
        for (int index = 0; index < Config.N_SHARED_RESOURCES; index++){
            if (sharedResourceCards[index] == null){
                ResourceCard newCard = getFromResourceDeck();
                // verify deck is not empty
                if (newCard != null){
                    sharedResourceCards[index] = newCard;
                }
            }
        }
        for (int index = 0; index < Config.N_SHARED_GOLDS; index++){
            if (sharedGoldCards[index] == null){
                GoldCard newCard = getFromGoldDeck();
                // verify deck is not empty
                if (newCard != null){
                    sharedGoldCards[index] = newCard;
                }
            }
        }
    }

     */
}