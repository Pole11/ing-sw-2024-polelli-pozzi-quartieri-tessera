package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.*;
import java.util.ArrayList;
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

    //SETTER
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
        return goldDeck;
    }

    public ArrayList<ResourceCard> getResourceDeck() {
        return resourceDeck;
    }

    //ADDER

    public void addToGoldDeck(GoldCard goldCard) {
        goldDeck.add(goldCard);
    }

    public void addToResourceDeck(ResourceCard resourceCard) {
        resourceDeck.add(resourceCard);
    }


    //METHODS


    public void initSharedGoldCards() {
        // it's like fillSharedGaps, look at the draw from shared methods to take inspiration
        GoldCard card1 = getFromGoldDeck();
        GoldCard card2 = getFromGoldDeck();

        GoldCard[] cards = {card1, card2};

        setSharedGoldCards(cards);
    }

    public void initSharedResourceCards() {
        ResourceCard card1 = getFromResourceDeck();
        ResourceCard card2 = getFromResourceDeck();

        ResourceCard[] cards = {card1, card2};

        setSharedResourceCards(cards);
    }

    // returns the requested shared gold card
    public GoldCard drawSharedGoldCard(int pos) {
        GoldCard[] sharedGolds = getSharedGoldCards();

        // verify position is valid
        if (pos < 1 || pos > sharedGolds.length) {
            throw new IllegalArgumentException("invalid position: " + pos);
        }

        // get the card
        GoldCard drawnCard = sharedGolds[pos - 1];

        // remove card from shared
        sharedGolds[pos - 1] = null;
        setSharedGoldCards(sharedGolds);

        // return the specified card
        return drawnCard;
    }

    // returns the requested shared resource card
    public ResourceCard drawSharedResourceCard(int pos) {
        ResourceCard[] sharedResources = getSharedResourceCards();

        // verify position is valid
        if (pos < 1 || pos > sharedResources.length) {
            throw new IllegalArgumentException("Invalid position: " + pos);
        }

        // get the specified card
        ResourceCard drawnCard = sharedResources[pos - 1];

        // remove card from shared
        sharedResources[pos - 1] = null;
        setSharedResourceCards(sharedResources);

        // return the card
        return drawnCard;
    }

    public ResourceCard getFromResourceDeck(){
        if (!resourceDeck.isEmpty()){
            ResourceCard drawnCard = getResourceDeck().getLast();
            resourceDeck.removeLast();
            return drawnCard;
        }
        else{
            return null;
        }
    }

    public GoldCard getFromGoldDeck(){
        if (!goldDeck.isEmpty()){
            GoldCard drawnCard = getGoldDeck().getLast();
            goldDeck.removeLast();
            return drawnCard;
        } else {
            return null;
        }
    }

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

    public void shuffleCards() {
        Random randStream = new Random();
        for (int i = 0; i < goldDeck.size(); i++) {
            int j = i + randStream.nextInt(goldDeck.size()-i);
            GoldCard temp = goldDeck.get(i);
            goldDeck.set(i, goldDeck.get(j));
            goldDeck.set(j, temp);
        }

        for (int i = 0; i < resourceDeck.size(); i++) {
            int j = i + randStream.nextInt(resourceDeck.size()-i);
            ResourceCard temp2 = resourceDeck.get(i);
            resourceDeck.set(i, resourceDeck.get(j));
            resourceDeck.set(j, temp2);
        }
    }
}
