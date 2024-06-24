package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums;

/**
 * Enumeration of all cli colors
 */
public enum ColorPrint {
    //Color end string, color reset
    RESET("\033[0m", 0),
    RED_BACKGROUND("\033[41m", 1),     // RED
    GREEN_BACKGROUND("\033[42m", 2),   // GREEN
    BLUE_BACKGROUND("\033[44m", 3),    // BLUE
    MAGENTA_BACKGROUND("\033[45m", 4), // MAGENTA
    LIGHT_YELLOW_BACKGROUND("\033[48;5;229m", 5),
    BLACK("\033[0;30m", 10),    // BLACK
    GOLD("\033[38;5;179m", 11),
    WHITE_BACKGROUND("\033[47m",12),   // WHITE
    RED("\033[0;31m",13),      // RED
    GREEN("\033[0;32m",14),    // GREEN
    YELLOW("\033[0;33m",15),   // YELLOW
    BLUE("\033[0;34m",16);     // BLUE

    /*
    // Regular Colors. Normal color, no bold, background color etc.
    MAGENTA("\033[0;35m",14),  // MAGENTA
    CYAN("\033[0;36m",7),     // CYAN
    WHITE("\033[0;37m",8),    // WHITE

    // Background

    RED_BACKGROUND("\033[41m",1),     // RED

    YELLOW_BACKGROUND("\033[43m",12),  // YELLOW
    CYAN_BACKGROUND("\033[46m",15),    // CYAN
    WHITE_BACKGROUND("\033[47m",16);   // WHITE

*/
    private final String code;
    private final int index;

    ColorPrint(String code, int index) {
        this.code = code;
        this.index = index;
    }

    public static ColorPrint byIndex(int i) {
        for (ColorPrint p : ColorPrint.values()) {
            if (p.index == i)
                return p;
        }
        return ColorPrint.RESET;
    }

    @Override
    public String toString() {
        return code;
    }

    public static int getColorIndex(int cardIndex) {
        int i = (int)Math.floor((double)(cardIndex-1) / 10 + 1);
        if (i > 4)
            i -= 4;
        return Math.min(i,5);
    }
    public static ColorPrint getColorPrint(Color c){
        switch (c){
            case RED -> {
                return RED;
            }
            case BLUE -> {
                return BLUE;
            }
            case YELLOW -> {
                return YELLOW;
            }
            case GREEN -> {
                return GREEN;
            }
            default -> {
                return BLACK;
            }
        }
    }
}
