package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.Config;
import java.util.ArrayList;

public class Board {
    private GoldCard[] sharedGoldCards; // 2 gold cards shared between players
    private ResourceCard[] sharedResourceCard; // 2 resource cards shared between players
    private ObjectiveCard[] sharedObjectiveCards; // 2 objective cards shared between players
    private ArrayList<GoldCard> goldDeck;
    private ArrayList<ResourceCard> resourceDeck;

    public Board(){
        sharedGoldCards = new GoldCard[Config.N_SHARED_GOLDS];
        sharedResourceCard = new ResourceCard[Config.N_SHARED_RESOURCES];
        sharedObjectiveCards = new ObjectiveCard[Config.N_SHARED_OBJECTIVES];
        goldDeck = new ArrayList<GoldCard>();
        resourceDeck  = new ArrayList<ResourceCard>();
    }

    //SETTER
    public void setSharedGoldCards(GoldCard[] sharedGoldCards) {
        this.sharedGoldCards = sharedGoldCards;
    }

    public void setSharedResourceCard(ResourceCard[] sharedResourceCard) {
        this.sharedResourceCard = sharedResourceCard;
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
        return sharedResourceCard;
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
    /*
    public GoldCard getSharedGoldCard(int pos) {

    }
    public ResourceCard getSharedResourceCard(int pos){

    }
    ResourceCard getFromResourceDeck(){

    }
    GoldCard getFromGoldDeck(){

    }*/
    void fillSharedCardsGap(){

    }



}
