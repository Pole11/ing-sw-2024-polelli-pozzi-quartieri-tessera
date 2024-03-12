package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.card;

public abstract class CornerCard extends Card {
    private Corner frontCorners[] = new Corner[Config.N_CORNERS];
    private Corner backCorners[] = new Corner[Config.N_CORNERS];
    private Resource centerBackResource[] = new Corner[Config.MAX_BACK_RESOURCES];


    public CornerCard[] getLinkedCards() {
        return new CornerCard[0];
    }

    public CornerCard[] getUncoveredCorners() {
        return new CornerCard[0];
    }

    public Resource[] getUncoveredResources(int side) {
        return new Resource[0];
    }

    public Item getUncoveredItems(int side) {
        return Item;
    }

}