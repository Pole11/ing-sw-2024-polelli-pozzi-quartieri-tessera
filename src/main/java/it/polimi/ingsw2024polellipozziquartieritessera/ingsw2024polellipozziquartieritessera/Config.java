package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera;

public class Config {
    public static final int MAX_PLAYERS = 4;
    public static final int MAX_HAND_CARDS = 3;
    public static final int N_SHARED_GOLDS = 2;
    public static final int N_SHARED_RESOURCES = 2;
    public static final int N_SHARED_OBJECTIVES = 2;
    public static final int N_CORNERS = 4;
    public static final int N_STRUCTURE_CHALLENGE_CONFIGURATION = 3;
    public static final int MAX_TURN_TIME = 60;
    public static final int MAX_CHAT_LENGHT = 255;
    public static final int POINTSTOENDPHASE = 20;
    public static final String GAME_STATE_PATH = "rescue/gamestate/";
    public static final String CARD_JSON_PATH = "/src/main/java/it/polimi/ingsw2024polellipozziquartieritessera/ingsw2024polellipozziquartieritessera/cards.json";

    public static final String HELP_STRING = "help";
    public static final String ADDUSER_STRING = "adduser";
    public static final String START_STRING = "start";
    public static final String CHOOSESTARTER_STRING = "choosestarterside";
    public static final String CHOOSECOLOR_STRING = "chooseinitialcolor";
    public static final String CHOOSEOBJECTIVE_STRING = "chooseinitialobjective";
    public static final String PLACECARD_STRING = "placecard";
    public static final String DRAWCARD_STRING = "drawcard";
    public static final String FLIPCARD_STRING = "flipcard";
    public static final String OPENCHAT_STRING = "openchat";
    public static final String ADDMESSAGE_STRING = "addmessage";

    public static int firstResourceCardId = Integer.MAX_VALUE;
    public static int firstGoldCardId = Integer.MAX_VALUE;
    public static int firstStarterCardId = Integer.MAX_VALUE;
    public static int firstObjectiveCardId = Integer.MAX_VALUE;

}


