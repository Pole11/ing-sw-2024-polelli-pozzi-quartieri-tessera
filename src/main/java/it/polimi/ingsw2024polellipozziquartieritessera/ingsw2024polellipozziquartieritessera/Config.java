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

    //al timeout in seconds
    public static final int TIMEOUT_TIME = 60;
    public static final int WAIT_FOR_PONG_TIME = 2;
    public static final int NEXT_PONG = 5;
    public static final int NEXT_PING_TIME = 4; // 10
    public static final int WAIT_FOR_PING_TIME = 4; // 5
    public static final int WAIT_DISCONNECTED_SERVER = 3;
    //this timout is in millisecond
    public static final String RMI_RESPONSE_TIMEOUT = "2000";

    public static final int BUFFER_IMAGE_SIZE = 100;

    public static final String GAME_STATE_PATH = "/rescue.json";
    public static final String CARD_JSON_PATH = "/cards.json";
    public static final String TITLE_CLI_PATH = "/img/TitleCLI.txt";

}


