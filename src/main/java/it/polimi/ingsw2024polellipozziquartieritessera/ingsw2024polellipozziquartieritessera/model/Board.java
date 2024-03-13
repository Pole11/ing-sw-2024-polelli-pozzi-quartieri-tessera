package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.card.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.Config;
import java.util.ArrayList;

public class Board {
    private GoldCard[] sharedGoldCards; //2 gold cards shared between players
    private ResourceCard[] sharedResourceCard; //2 resource cards shared between players
    private ObjectiveCard[] sharedObjectiveCards; //2 objective cards shared between players
    private ArrayList<GoldCard> goldDeck;
    private ArrayList<ResourceCard> resourceDeck;

    public Board(){
        sharedGoldCards = new GoldCard[Config.N_SHARED_GOLDS];
        sharedResourceCard = new ResourceCard[Config.N_SHARED_RESOURCES];
        sharedObjectiveCards = new ObjectiveCard[Config.N_SHARED_OBJECTIVES];
        goldDeck = new ArrayList<GoldCard>();
        resourceDeck  = new ArrayList<ResourceCard>();
    }

    //non ho messo i metodi ancora
}
