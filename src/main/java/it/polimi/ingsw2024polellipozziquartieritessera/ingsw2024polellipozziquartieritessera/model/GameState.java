package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.card.*;

public class GameState {
    // uniqueInstance is used by GameState singleton
    static GameState uniqueInstance;
    private arrayList<Player> players; //2<players.size()<4
    private Player currentPlayer;
    private GamePhase currentGamePhase;
    private Board mainBoard;

    public Player getCurrentPlayer(){}
    public GamePhase getCurrentGamePhase(){}
    public Board getMainBoard(){}
    // instance is used by GameState singleton
    public static void instance() {}
    // singletonOperation is used by GameState singleton
    public void singletonOperation(){}
    public Player getBlackPlayer(){}
    public void flipCard(String type){}
}
