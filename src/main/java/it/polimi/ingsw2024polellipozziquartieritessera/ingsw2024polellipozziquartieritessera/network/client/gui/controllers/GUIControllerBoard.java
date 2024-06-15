package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.controllers;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.CornerPos;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable.PlaceCardCommandRunnable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.controlsfx.control.spreadsheet.Grid;

import java.util.ArrayList;
import java.util.Objects;

public class GUIControllerBoard extends GUIController {
    @FXML
    private VBox mainContainerBoard;

    public GUIControllerBoard() {
        this.update();
        setFontSize(mainContainerBoard);
    }

    public void printBoard() {
        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                int boardImageHeight = 100;
                int boardImageWidth = boardImageHeight*3/2;
                int gridPaneVPadding = 50;
                int gridPaneHgap = 0;
                int gridPaneVgap = 0;
                int rowOffset = 1;
                int colOffset = 1;
                int cornerWidth = 28;
                int cornerHeight = 35;
                int playerId = getParamsMap().get("playerId");

                ArrayList<ArrayList<Integer>> playerBoard = getViewModel().getPlayerBoard(playerId); // the first arg is the index of the player to print the board of
                playerBoard = rotateBoard(playerBoard);
                resizeI(playerBoard);

                mainContainerBoard.getChildren().remove(mainContainerBoard.lookup("#boardGridPane"));

                GridPane gridPane = new GridPane();
                //gridPane.setGridLinesVisible(true);
                gridPane.setId("boardGridPane");
                gridPane.setHgap(gridPaneHgap); // Spacing orizzontale
                gridPane.setVgap(gridPaneVgap); // Spacing verticale
                gridPane.setPadding(new Insets(gridPaneVPadding)); // Margine di 20 pixel su tutti i lati
                mainContainerBoard.getChildren().add(gridPane);

                addPanning(gridPane);

                for (int i = 0; i < playerBoard.size(); i++) {
                    gridPane.getRowConstraints().add(new RowConstraints(boardImageHeight - cornerHeight));
                }
                for (int i = 0; i < playerBoard.getFirst().size(); i++) {
                    gridPane.getColumnConstraints().add(new ColumnConstraints(boardImageWidth - cornerWidth));
                }

                for (int k = 0; k < getViewModel().getPlacingCardOrderMap(playerId).size(); k++) {
                    for (int i = 0; i < playerBoard.size(); i++) {
                        for (int j = 0; j < playerBoard.getFirst().size(); j++) {
                            if (playerBoard.get(i).get(j) != -1 && getViewModel().getPlacingCardOrderMap(playerId).get(k).equals(playerBoard.get(i).get(j))) {
                                int ele = playerBoard.get(i).get(j);
                                ImageView tempImageView;
                                String imageUrl = null;
                                if (getViewModel().getPlacedCardSide(ele) == Side.FRONT) {
                                    imageUrl = "/img/carte_fronte/" + ele + ".jpg";
                                } else {
                                    imageUrl = "/img/carte_retro/" + ele + ".jpg";
                                }
                                tempImageView = createCardImageView(imageUrl, boardImageHeight);
                                gridPane.add(tempImageView, j, i);
                            }
                        }
                    }
                }

            }
        });
    }

    public void handleBackToGame(ActionEvent event) {
        goToScene("/fxml/game.fxml");
    }

    @FXML
    public void handleRestoreView(ActionEvent event) {
        Platform.runLater(() -> {
            GridPane boardGridPane = (GridPane) mainContainerBoard.lookup("#boardGridPane");
            boardGridPane.setTranslateX(0);
            boardGridPane.setTranslateY(0);
        });
    }

    @Override
    public void update() {
        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                if (getViewModel() != null) {
                    printBoard();
                }
            }
        });
    }
}

