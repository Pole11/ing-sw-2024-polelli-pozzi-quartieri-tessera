package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums;

public enum CornerPos {
    UPLEFT(0),
    UPRIGHT(1),
    DOWNRIGHT(2),
    DOWNLEFT(3);

    private int value;
    private CornerPos(int value){
        this.value = value;
    }

    public int getCornerPosValue()
    {
        return value;
    }
}
