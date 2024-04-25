package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.*;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.*;
import org.junit.jupiter.api.Test;

public class GameStateTest {
    @Test
    void gameStateTestConstructorColor() throws WrongStructureConfigurationSizeException, IOException {
        // same colors
        Main.createCardsMap();
        Main.gameState.removeAllPlayers();

        assertDoesNotThrow(() -> Main.gameState.setPlayer(0, new Player("nick1", Color.RED)));
        assertThrowsExactly(NotUniquePlayerColorException.class, () -> Main.gameState.setPlayer(1, new Player("nick2", Color.RED)));
    }

    @Test
    void gameStateTestConstructorNickname() throws WrongStructureConfigurationSizeException, IOException {
        // same nickname
        Main.createCardsMap();
        Main.gameState.removeAllPlayers();

        assertDoesNotThrow(() -> Main.gameState.setPlayer(0, new Player("nick1", Color.RED)));
        assertThrowsExactly(NotUniquePlayerNicknameException.class, () -> Main.gameState.setPlayer(1, new Player("nick1", Color.BLUE)));
    }

    @Test
    void gameStateTestConstructorColorAndNickname() throws WrongStructureConfigurationSizeException, IOException {
        // same colors and nicknames
        Main.createCardsMap();
        Main.gameState.removeAllPlayers();

        assertDoesNotThrow(() -> Main.gameState.setPlayer(0, new Player("nick1", Color.RED)));
        assertThrowsExactly(NotUniquePlayerException.class, () -> Main.gameState.setPlayer(1, new Player("nick1", Color.RED)));
    }

    @Test
    void calculateFinalPoints(){

    }

    @Test
    void isGameEnded(){

    }

    @Test
    void getWinnerPlayerIndex(){

    }

}
