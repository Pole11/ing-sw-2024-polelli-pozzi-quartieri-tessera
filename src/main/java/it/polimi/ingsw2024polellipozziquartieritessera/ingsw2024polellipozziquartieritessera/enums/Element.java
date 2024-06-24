package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums;

/**
 * Enumeration of all elements (resources and items) of the game
 */
public enum Element implements ElementTypeInterface {

    QUILL(ElementType.ITEM),
    MANUSCRIPT(ElementType.ITEM),
    INKWELL(ElementType.ITEM),
    FUNGI(ElementType.RESOURCE),
    ANIMAL(ElementType.RESOURCE),
    INSECT(ElementType.RESOURCE),
    PLANT(ElementType.RESOURCE),
    EMPTY(ElementType.EMPTY);

    private final ElementType type;

    /**
     * Constructs an element of the specified type.
     *
     * @param type The type of the element.
     */
    Element(ElementType type) {
        this.type = type;
    }

    /**
     * Returns the type of the element.
     *
     * @return The type of the element.
     */
    public ElementType getType() {
        return this.type;
    }

    /**
     * Returns a displayable string representing the type of the element.
     *
     * @return Displayable type string.
     */
    public String getDisplayableType() {
        return type.getDisplayableType();
    }
}

