package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.card;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;

public abstract class CornerCard extends Card {
    private final Corner[] frontCorners;
    private final Corner[] backCorners;
    public CornerCard(int id, Corner[] frontCorners, Corner[] backCorners) {
        super(id);
        this.frontCorners = frontCorners;
        this.backCorners =  backCorners;
    }

    public Corner[] getCorners() {
        // corner array initialization
        Corner[] corners = new Corner[8];
        // counter initialization
        int i = 0;

        // adding front and back corners
        for (Corner corner : frontCorners){
            corners[i] = corner;
            i++;
        }
        for (Corner corner : backCorners){
            corners[i] = corner;
            i++;
        }

        return corners;
    }

    // return the linked cards to this one
    public CornerCard[] getLinkedCards() {
        // card array initialization
        CornerCard[] linked_cards = new CornerCard[4];
        // counter initialization for the array index
        int i = 0;

        // front corners verification
        for (Corner corner : this.getCorners()){
            if (corner.getLinkedCorner() != null){
                linked_cards[i] = corner.getCard();
                i++;
            }
        }

        return linked_cards;
    }

    public Corner[] getUncoveredCorners() {
        // corner array initialization
        Corner[] uncovered_corners = new Corner[4];

        // counter initialization for the array index
        int i = 0;

        // front corners verification
        for (Corner corner : this.getCorners()){
            if (corner.getLinkedCorner() != null && !corner.getCovered()){
                uncovered_corners[i] = corner;
                i++;
            }
        }
        return uncovered_corners;
    }

    public Resource[] getUncoveredResources(int side) {
        return new Resource[0];
    }

    public Item getUncoveredItem(int side) {
        return Item.INKWELL;
    }
}