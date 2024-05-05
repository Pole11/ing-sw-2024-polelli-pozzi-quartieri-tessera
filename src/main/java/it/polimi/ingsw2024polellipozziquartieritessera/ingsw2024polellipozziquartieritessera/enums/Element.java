package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums;

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
    private Element(ElementType type) {
        this.type = type;
    }

    public ElementType getType() { return this.type; }

    public String getDisplayableType() {
        return type.getDisplayableType();
    }
}

