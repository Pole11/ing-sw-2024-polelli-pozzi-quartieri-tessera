package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.card;

public class ResourceCard extends CornerCard {
    private Resource resourceType; //color of the card, determined by the major resourceType
    private int points;

    public ResourceCard(int id, Resource resourceType, int points) {
        super(id); //bisognerà aggiungere altro
        this.resourceType = resourceType;
        this.points = points;
    }
}