package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.controller;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.Main;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.controller.Controller;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.CornerPos;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ControllerTest {
    @Test
    void placeCard() throws NotUniquePlayerNicknameException, NotUniquePlayerColorException, WrongStructureConfigurationSizeException, NotUniquePlayerException, IOException, WrongPlacingPositionException, WrongInstanceTypeException {
        GameState game = Main.populate();
        Controller controller = new Controller(game);
        controller.startGame();
        controller.chooseInitialStarterSide(0, Side.BACK);
        int goldCoverageCardId = 55;
        controller.placeCard(0, goldCoverageCardId, game.getPlayers().get(0).getStarterCard().getId(), CornerPos.UPLEFT, Side.FRONT);
        controller.placeCard(0, goldCoverageCardId, game.getPlayers().get(0).getStarterCard().getId(), CornerPos.UPRIGHT, Side.FRONT);
        controller.placeCard(0, goldCoverageCardId, game.getPlayers().get(0).getStarterCard().getId(), CornerPos.DOWNLEFT, Side.FRONT);
        controller.placeCard(0, goldCoverageCardId, game.getPlayers().get(0).getStarterCard().getId(), CornerPos.DOWNRIGHT, Side.FRONT);

    }
}
