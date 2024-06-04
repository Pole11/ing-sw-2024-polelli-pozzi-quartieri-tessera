package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums;

import java.util.ArrayList;

public enum PrintableCardParts {


    UPLEFT ("+--|  ", 0),
    UP("---   ", 1),
    UPRIGHT("--+  |",2),
    LEFT("|  |  ", 3),
    CENTER("ID:%3d",4),
    RIGHT("  |  |",5),
    DOWNLEFT("|  +--", 6),
    DOWN("   ---",7),
    DOWNRIGHT("  |--+",8),
    EMPTY("      ",9);


    private final String disposition;
    private final int index;

    private PrintableCardParts(final String disposition, final int index) {

        this.disposition = disposition;
        this.index = index;
    }

    public String getDisposition(){return this.disposition;}
    public static PrintableCardParts byIndex(int i){
        for(PrintableCardParts p : PrintableCardParts.values()){
            if (p.index == i)
                return  p;
        }
        return PrintableCardParts.EMPTY;
    }
    public String firstRow(){return this.getDisposition().substring(0,3);}

    public String secondRow(){return this.getDisposition().substring(3,6);}
}

