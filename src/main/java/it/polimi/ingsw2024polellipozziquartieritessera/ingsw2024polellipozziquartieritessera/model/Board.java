package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.card.*;

public class Board {
    private GoldCard sharedGoldCards[Config.N_SHARED_GOLDS];
    private GoldCard sharedGoldCads[Config.N_SHARED_GOLDS];
    private ArrayList<ResourceCard> sharedResourceCards;
    private ArrayList<ObjectiveCard> sharedObjectiveCards;
    private ArrayList<GoldCard> goldDeck;
    private ArrayList<ResourceCard> resourceDeck;
}