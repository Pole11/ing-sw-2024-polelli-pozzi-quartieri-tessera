package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.ElementTypeInterface;

public enum ElementType implements ElementTypeInterface {

    RESOURCE("Resource"), ITEM("Item");
    private final String type;

    private ElementType(final String type) {
        this.type = type;
    }

    public String getDisplayableType() {
        return type;
    }
}