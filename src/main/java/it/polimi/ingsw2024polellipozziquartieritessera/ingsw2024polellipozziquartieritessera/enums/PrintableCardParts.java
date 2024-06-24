package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums;

/**
 * Enumeration of strings used in cli card printings
 */
public enum PrintableCardParts {

    UPLEFT("+--|  ", 0),
    UP("---   ", 1),
    UPRIGHT("--+  |", 2),
    LEFT("|  |  ", 3),
    CENTER("ID:%3d", 4),
    RIGHT("  |  |", 5),
    DOWNLEFT("|  +--", 6),
    DOWN("   ---", 7),
    DOWNRIGHT("  |--+", 8),
    EMPTY("      ", 9);


    private final String disposition;
    private final int index;

    /**
     * Constructs a PrintableCardParts enum constant with the given disposition and index.
     *
     * @param disposition The string disposition of the card part.
     * @param index       The index of the card part.
     */
    PrintableCardParts(final String disposition, final int index) {
        this.disposition = disposition;
        this.index = index;
    }

    /**
     * Returns a PrintableCardParts constant by its index.
     *
     * @param i The index to search for.
     * @return The corresponding PrintableCardParts constant, or EMPTY if not found.
     */
    public static PrintableCardParts byIndex(int i) {
        for (PrintableCardParts p : PrintableCardParts.values()) {
            if (p.index == i)
                return p;
        }
        return PrintableCardParts.EMPTY;
    }

    /**
     * Returns the disposition string of the card part.
     *
     * @return The disposition string.
     */
    public String getDisposition() {
        return this.disposition;
    }

    /**
     * Returns the first row of the disposition string.
     *
     * @return The first row substring.
     */
    public String firstRow() {
        return this.getDisposition().substring(0, 3);
    }

    /**
     * Returns the second row of the disposition string.
     *
     * @return The second row substring.
     */
    public String secondRow() {
        return this.getDisposition().substring(3, 6);
    }
}

