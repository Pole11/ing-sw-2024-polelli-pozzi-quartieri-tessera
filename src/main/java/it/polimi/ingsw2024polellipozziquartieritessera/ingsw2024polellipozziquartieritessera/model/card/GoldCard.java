package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.card;

public class GoldCard extends CornerCard {
    private Resource resourceType; //color of the card, determined by the major resourceType
    private Challenge challenge;
    private Resource[] resourceNeeded;
    private int points;

    public GoldCard(int id, Resource resourceType, Challenge challenge, Resource[] resourceNeeded, int points) {
        super(id); //bisognerà aggiungere altro
        this.resourceType = resourceType;
        this.challenge = challenge;
        this.resourceNeeded = resourceNeeded;
        this.points = points;
    
}