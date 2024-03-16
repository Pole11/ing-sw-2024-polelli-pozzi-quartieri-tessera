package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera;

public class Config {
    public static final int MAX_PLAYERS = 4;
    public static final int N_SHARED_GOLDS = 2;
    public static final int N_SHARED_RESOURCES = 2;
    public static final int N_SHARED_OBJECTIVES = 2;
    public static final int N_CORNERS = 4;
    public static final int MAX_CENTRAL_RESOURCES = 3;
    public static final int MAX_RES_NEEDED = 5;
    public static final int MAX_UNCOVERED_RESOURCES = 4;
    public static final int MAX_CHAL_RESOURCE = 3;
    public static final int MAX_CHAL_ITEM = 3;
    public static final int MAX_UNCOVERED_ITEMS = 1;
    public static final int MAX_TURN_TIME = 60;
    public static final String GAME_STATE_PATH = "rescue/gamestate/";

    public static final String CARD_JSON_PATH = "/src/main/java/it/polimi/ingsw2024polellipozziquartieritessera/ingsw2024polellipozziquartieritessera/cards.json";

    enum Color {
        BLACK,
        GREEN,
        YELLOW,
        RED
    }
}


