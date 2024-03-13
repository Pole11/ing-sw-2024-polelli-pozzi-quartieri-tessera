package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.card;

public abstract class CornerCard extends Card {
    private Corner frontCorners[] = new Corner[Config.N_CORNERS];
    private Corner backCorners[] = new Corner[Config.N_CORNERS];
    private Resource centerBackResource[] = new Corner[Config.MAX_BACK_RESOURCES];

    public CornerCard(int id) {
        this.id = id;
        for (int i = 0; i < Config.N_CORNERS; i++) {
            frontCorners[i] = new Corner(false, null, null, null); // al posto di questi false e null 
            backCorners[i] = new Corner(false, null, null, null);  // metteremo dei dati passati 
            centerBackResource[i] = new Corner(false, null, null, null); // da parametro, presi da file
        }
    }


    // l'interno del metodo è a caso, solo le signature sono giuste ora
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