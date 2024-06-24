package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums;

/**
 * Enumeration of the possible elements categories
 */
public enum ElementType implements ElementTypeInterface {

    RESOURCE("Resource"), ITEM("Item"), EMPTY("Empty");
    private final String type;

    /**
     * Constructs an element type with the specified type string.
     *
     * @param type The type string of the element.
     */
    ElementType(final String type) {
        this.type = type;
    }

    /**
     * Returns the displayable type string of the element.
     *
     * @return The displayable type string.
     */
    public String getDisplayableType() {
        return type;
    }
}