package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums;

public enum ColorPrint {
    //Color end string, color reset
    RESET("\033[0m", 0),
    RED_BACKGROUND("\033[41m",1),     // RED
    GREEN_BACKGROUND("\033[42m",2),   // GREEN
    BLUE_BACKGROUND("\033[44m",3),    // BLUE
    MAGENTA_BACKGROUND("\033[45m",4), // MAGENTA
    LIGHT_YELLOW_BACKGROUND("\033[48;5;229m", 5),
    BLACK("\033[0;30m", 10),    // BLACK
    WHITE_BACKGROUND("\033[47m",16);   // WHITE


    /*
    // Regular Colors. Normal color, no bold, background color etc.
    RED("\033[0;31m",10),      // RED
    GREEN("\033[0;32m",11),    // GREEN
    YELLOW("\033[0;33m",4),   // YELLOW
    BLUE("\033[0;34m",13),     // BLUE
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
