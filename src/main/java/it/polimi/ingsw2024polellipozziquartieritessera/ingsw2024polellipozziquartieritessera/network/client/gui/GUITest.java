package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Color;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.CornerPos;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.GamePhase;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.Client;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.ViewModel;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.controllers.GUIController;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.VirtualServer;
import javafx.application.*;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.*;

import java.io.*;
import java.util.HashMap;

public class GUITest {
    public static void main(String[] args) {
        ViewModel viewModel = new ViewModel();
        viewModel.setNickname(0, "Gino");
        viewModel.setNickname(1, "Pino");
        viewModel.setNickname(2, "Tino");
        viewModel.setColor(0, Color.BLUE);
        viewModel.setColor(1, Color.GREEN);
        viewModel.setColor(2, Color.YELLOW);
        viewModel.setGamePhase(GamePhase.MAINPHASE);
        viewModel.setSharedObjectives(96,97);
        viewModel.setCurrentPlayer(0);
        viewModel.setSecretObjective(90, 91);
        viewModel.addedCardToHand(0, 1);
        viewModel.setHandSide(1, Side.FRONT);
        viewModel.addedCardToHand(0, 11);
        viewModel.setHandSide(11, Side.BACK);
        viewModel.addedCardToHand(0, 21);
        viewModel.setHandSide(21, Side.FRONT);
        viewModel.addedCardToHand(1, 31);
        viewModel.addedCardToHand(1, 41);
        viewModel.addedCardToHand(1, 51);
        viewModel.addedCardToHand(2, 32);
        viewModel.addedCardToHand(2, 42);
        viewModel.addedCardToHand(2, 52);
        viewModel.setMainBoard(42, 52, 2,12, 43, 3);
        viewModel.setStarterCard(98);
        viewModel.initializeBoard(0, 98);
        viewModel.updatePlayerBoard(0, 5, 98, CornerPos.UPLEFT, Side.FRONT);
        viewModel.updatePlayerBoard(0, 15, 5, CornerPos.DOWNLEFT, Side.FRONT);
        viewModel.updatePlayerBoard(0, 25, 5, CornerPos.UPRIGHT, Side.FRONT);
        viewModel.updatePlayerBoard(0, 17, 5, CornerPos.UPLEFT, Side.FRONT);

        viewModel.initializeBoard(1, 34);
        viewModel.updatePlayerBoard(1, 35, 34, CornerPos.UPLEFT, Side.FRONT);
        viewModel.updatePlayerBoard(1, 65, 34, CornerPos.UPLEFT, Side.FRONT);
        viewModel.updatePlayerBoard(1, 75, 65, CornerPos.UPLEFT, Side.FRONT);
        viewModel.updatePlayerBoard(1, 37, 75, CornerPos.UPLEFT, Side.FRONT);

        GUIApplication guiApplication = new GUIApplication();
        guiApplication.runGui(null, null, null, viewModel);
    }
}
