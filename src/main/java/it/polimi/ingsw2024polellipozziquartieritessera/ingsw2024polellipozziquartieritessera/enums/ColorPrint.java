package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums;

public enum ColorPrint {
    //Color end string, color reset
    RESET("\033[0m", 0),

    // Regular Colors. Normal color, no bold, background color etc.
    BLACK("\033[0;30m", 1),    // BLACK
    RED("\033[0;31m",2),      // RED
    GREEN("\033[0;32m",3),    // GREEN
    YELLOW("\033[0;33m",4),   // YELLOW
    BLUE("\033[0;34m",5),     // BLUE
    MAGENTA("\033[0;35m",6),  // MAGENTA
    CYAN("\033[0;36m",7),     // CYAN
    WHITE("\033[0;37m",8),    // WHITE

    // Background
    BLACK_BACKGROUND("\033[40m",9),   // BLACK
    RED_BACKGROUND("\033[41m",10),     // RED
    GREEN_BACKGROUND("\033[42m",11),   // GREEN
    YELLOW_BACKGROUND("\033[43m",12),  // YELLOW
    BLUE_BACKGROUND("\033[44m",13),    // BLUE
    MAGENTA_BACKGROUND("\033[45m",14), // MAGENTA
    CYAN_BACKGROUND("\033[46m",15),    // CYAN
    WHITE_BACKGROUND("\033[47m",16);   // WHITE


    private final String code;
    private final int index;
    ColorPrint(String code, int index) {
        this.code = code;
        this.index = index;
    }
    public static ColorPrint byIndex(int i){
        for(ColorPrint p : ColorPrint.values()){
            if (p.index == i)
                return  p;
        }
        return ColorPrint.RESET;
    }

    @Override
    public String toString() {
        return code;
    }
}
