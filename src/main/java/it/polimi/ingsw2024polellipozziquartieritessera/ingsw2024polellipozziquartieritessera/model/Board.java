package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.*;
import java.util.ArrayList;

public class Board {
    private GoldCard[] sharedGoldCards; // 2 gold cards shared between players
    private ResourceCard[] sharedResourceCards; // 2 resource cards shared between players
    private ObjectiveCard[] sharedObjectiveCards; // 2 objective cards shared between players
    private ArrayList<GoldCard> goldDeck;
    private ArrayList<ResourceCard> resourceDeck;

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

    public void setSharedResourceCard(ResourceCard[] sharedResourceCard) {
        this.sharedResourceCards = sharedResourceCard;
    }

    public void setSharedObjectiveCards(ObjectiveCard[] sharedObjectiveCards) {
        this.sharedObjectiveCards = sharedObjectiveCards;
    }

    public void setGoldDeck(ArrayList<GoldCard> goldDeck) {
        this.goldDeck = goldDeck;
    }

    public void setResourceDeck(ArrayList<ResourceCard> resourceDeck) {
        this.resourceDeck = resourceDeck;
    }

    //GETTER
    public GoldCard[] getSharedGoldCards() {
        return sharedGoldCards;
    }

    public ResourceCard[] getSharedResourceCard() {
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


    //METHODS
    // returns the requested shared gold card
    public GoldCard getSharedGoldCard(int pos) {
        return getSharedGoldCards()[pos-1]; // remember that pos is either 1 or 2
    }

    // returns the requested shared resource card
    public ResourceCard getSharedResourceCard(int pos){
        return getSharedResourceCard()[pos-1]; // remember that pos is either 1 or 2
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
        }
        else{
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

    }
}
