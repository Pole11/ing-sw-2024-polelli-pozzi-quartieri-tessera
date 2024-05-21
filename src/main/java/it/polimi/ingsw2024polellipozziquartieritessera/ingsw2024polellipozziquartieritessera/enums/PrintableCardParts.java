package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums;

import java.util.ArrayList;

public enum PrintableCardParts {

    UPLEFTCOVERED ("  |--+"),
    UPLEFTCOVER ("+--|  "),
    UP("---   "),
    UPRIGHTCOVERED("|  +--"),
    UPRIGHTCOVER("--+  |"),
    RIGHT("  |  |"),
    DOWNRIGHTCOVERED("+--|  "),
    DOWNRIGHTCOVER("+--|  "),
    DOWN("   ---"),
    DOWNLEFTCOVERED("--+  |"),
    DOWNLEFTCOVER("|  +--"),
    LEFT("|  |  ");

    private final String disposition;

    private PrintableCardParts(final String disposition) {
        this.disposition = disposition;
    }

    public String getDisposition(){return this.disposition;}

    public String firstRow(){return this.getDisposition().substring(0,3);}

    public String secondRow(){return this.getDisposition().substring(4,6);}
}

