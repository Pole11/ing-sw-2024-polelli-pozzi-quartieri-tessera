package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.bots;

public class BotConfig {
    public static final float PASSIVITY = 1f; // probability to place on the back - suggested [<0.1]
    public static final int GOLDWEIGHT = 20; // suggested [10-50]
    public static final int RESOURCEWEIGHT = 1; // suggested [1]
    public static final int SPEED = 20; // speed for command insertion (millisecond) - suggested [10-1000]
    public static final boolean CHAT_ENABLED = true;
    public static final float CHAT_FLUENCY = 0.0001f; // probability to write in chat
    public static final boolean CAN_START_IF_FULL = true;
    public static final int PLAYER_NUM = 4;
}
