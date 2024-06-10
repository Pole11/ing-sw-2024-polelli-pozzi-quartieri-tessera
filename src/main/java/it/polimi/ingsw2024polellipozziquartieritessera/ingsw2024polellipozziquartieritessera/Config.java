package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera;

public class Config {
    public static final int MAX_PLAYERS = 4;
    public static final int MAX_HAND_CARDS = 3;
    public static final int N_SHARED_GOLDS = 2;
    public static final int N_SHARED_RESOURCES = 2;
    public static final int N_SHARED_OBJECTIVES = 2;
    public static final int N_CORNERS = 4;
    public static final int N_STRUCTURE_CHALLENGE_CONFIGURATION = 3;
    public static final int N_OBJECTIVE_CARD_OPTIONS = 2;
    public static final int MAX_TURN_TIME = 60;
    public static final int MAX_CHAT_LENGHT = 255;
    public static final int POINTSTOENDPHASE = 20;
    public static final int OBJECTIVEQTY = 16;
    public static final int STARTERQTY = 6;

    public static final int TIMEOUT_TIME = 60; // 60
    public static final int NEXT_PING_TIME = 2; // 10
    public static final int WAIT_FOR_PING_TIME = 1; // 5
    public static final int WAIT_FOR_SAVE_TIME = 10; // 10

    public static final String GAME_STATE_PATH = "/src/main/java/it/polimi/ingsw2024polellipozziquartieritessera/ingsw2024polellipozziquartieritessera/rescue.json";
    public static final String CARD_JSON_PATH = "/src/main/java/it/polimi/ingsw2024polellipozziquartieritessera/ingsw2024polellipozziquartieritessera/cards.json";
    public static final String TITLE_CLI_PATH = "/src/main/resources/img/TitleCLI.txt";

    public static int firstResourceCardId = Integer.MAX_VALUE;
    public static int firstGoldCardId = Integer.MAX_VALUE;
    public static int firstStarterCardId = Integer.MAX_VALUE;
    public static int firstObjectiveCardId = Integer.MAX_VALUE;
}


