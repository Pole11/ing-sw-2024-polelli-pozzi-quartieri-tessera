package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera;

public class GameState {
    static GameState uniqueInstace;
    private arrayList<Player> players; //2<players.size()<4
    private Player currentPlayer;
    private GamePhase currentGamePhase;
    private Board mainBoard;

    public Player getCurrentPlayer(){}
    public GamePhase getCurrentGamePhase(){}
    public Board getMainBoard(){}
    public static void instance() {}
    public void singletonOperation(){}
    public Player getBlackPlayer(){}
    public void flipCard(String type){}
}
