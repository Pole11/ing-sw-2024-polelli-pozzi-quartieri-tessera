package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.card.ObjectiveCard;

import java.util.ArrayList;

public class Board {
    private GoldCard[] sharedGoldCards;
    private ResourceCard[] sharedResourceCard;
    private ObjectiveCard[] sharedObjectiveCards;
    private ArrayList<GoldCard> goldDeck;
    private ArrayList<ResourceCard> resourceDeck;

    public Board(){
        sharedGoldCards = new GoldCard[Config.N_SHARED_GOLDS];
        sharedResourceCard = new ResourceCard[Config.N_SHARED_RESOURCES];
        sharedObjectiveCards = new ObjectiveCard[Config.N_SHARED_OBJECTIVES];
        goldDeck = new ArrayList<GoldCard>();
        resourceDeck  = new ArrayList<ResourceCard>();
    }
}


