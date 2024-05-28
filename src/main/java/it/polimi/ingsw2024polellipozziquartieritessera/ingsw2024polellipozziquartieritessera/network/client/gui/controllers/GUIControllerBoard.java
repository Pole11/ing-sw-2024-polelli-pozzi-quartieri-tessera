package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.controllers;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.HashMap;

public class GUIControllerBoard extends GUIController {
    @FXML private VBox mainContainerBoard;

    public GUIControllerBoard() {
        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                if (getViewModel() != null) {
                    if (getParamsMap().get("cardId") != null) printSelectedCorner();
                    printBoard();
                }
            }
        });
    }

    public void printSelectedCorner() {
        Platform.runLater(() -> {
            int imageHeight = 120;
            String url = "/img/carte_" +
                    (getViewModel().getHandCardsSide(getParamsMap().get("cardId")).equals(Side.FRONT) ? "fronte" : "retro") +
                    "/" +
                    getParamsMap().get("cardId") +
                    ".jpg";
            Pane handCardContainer = new Pane();
            ImageView handCardImageView = createCardImageView(url, imageHeight);

            int cornerId = getParamsMap().get("cornerId");
            int rectX = 0, rectY = 0;
            if (cornerId == 0 || cornerId == 3) rectX = 0; else rectX = imageHeight*3/4;
            if (cornerId == 0 || cornerId == 1) rectY = 0; else rectY = imageHeight/2;

            Rectangle clickedRectangle = new Rectangle(rectX, rectY,imageHeight*3/4 + 2, imageHeight/2);
            clickedRectangle.setFill(new Color(0,0,0,0.4));

            handCardContainer.getChildren().add(handCardImageView);
            handCardContainer.getChildren().add(clickedRectangle);
            mainContainerBoard.getChildren().add(handCardContainer);
        });
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
                int cornerWidth = 25;
                int cornerHeight = 30;

                int playerId = getParamsMap().get("playerId");
                ArrayList<ArrayList<Integer>> playerBoard = getViewModel().getPlayerBoard(playerId); // the first arg is the index of the player to print the board of

                GridPane gridPane = new GridPane();
                gridPane.setGridLinesVisible(true);
                gridPane.setHgap(gridPaneHgap); // Spacing orizzontale
                gridPane.setVgap(gridPaneVgap); // Spacing verticale
                gridPane.setPadding(new Insets(gridPaneVPadding)); // Margine di 20 pixel su tutti i lati
                mainContainerBoard.getChildren().add(gridPane);

                addPanning(gridPane);

                for (int i = 0; i < playerBoard.size() + 2; i++) {
                    gridPane.getRowConstraints().add(new RowConstraints(boardImageHeight - cornerHeight));
                }
                for (int i = 0; i < playerBoard.getFirst().size() + 2; i++) {
                    gridPane.getColumnConstraints().add(new ColumnConstraints(boardImageWidth - cornerWidth));
                }

                for (int i = 0; i < playerBoard.size() + 2; i++) {
                    for (int j = 0; j < playerBoard.getFirst().size() + 2; j++) {
                        if (i > 0 && j > 0 && i < playerBoard.size() && j < playerBoard.get(i - rowOffset).size() && playerBoard.get(i - rowOffset) != null && playerBoard.get(i - rowOffset).get(j - colOffset) != null) {
                            int ele = playerBoard.get(i - rowOffset).get(j - colOffset);
                            System.out.print(ele + " ");
                            ImageView tempImageView;
                            if (getViewModel().getPlacedCardSide(ele) == Side.FRONT) {
                                tempImageView = createCardImageView("/img/carte_fronte/" + ele + ".jpg", boardImageHeight);
                            } else {
                                tempImageView = createCardImageView("/img/carte_retro/" + ele + ".jpg", boardImageHeight);
                            }
                            tempImageView.setId("" + ele);
                            System.out.println(ele);
                            gridPane.add(tempImageView, rowOffset + i, colOffset + j);
                            gridPane.setHalignment(tempImageView, HPos.CENTER);
                            gridPane.setValignment(tempImageView, VPos.CENTER);
                        }
                    }
                    System.out.println("");
                }

                for (int i = 0; i < playerBoard.size() + 2; i++) {
                    for (int j = 0; j < playerBoard.getFirst().size() + 2; j++) {
                        Pane dummyCell = new Pane();
                        dummyCell.setPrefWidth(boardImageWidth);
                        dummyCell.setPrefHeight(boardImageHeight);
                        dummyCell.getStyleClass().add("clickable");
                        gridPane.add(dummyCell, j, i);
                        addHoverBg(dummyCell);
                        dummyCell.setOnMouseClicked((mouseEvent) -> { System.out.println("CIAO"); });
                    }
                }

                /*if (i > 0 && j > 0 && i < playerBoard.size() && j < playerBoard.get(i).size() && playerBoard.get(i - rowOffset) != null && playerBoard.get(i - rowOffset).get(j - colOffset) != null) {
                    int ele = playerBoard.get(i - rowOffset).get(j - colOffset);
                    ImageView tempImageView;
                    if (getViewModel().getPlacedCardSide(ele) == Side.FRONT) {
                        tempImageView = createCardImageView("/img/carte_fronte/" + ele + ".jpg", boardImageHeight);
                    } else {
                        tempImageView = createCardImageView("/img/carte_retro/" + ele + ".jpg", boardImageHeight);
                    }
                    tempImageView.setId("" + ele);
                    System.out.println(ele);
                    gridPane.add(tempImageView, rowOffset + i, colOffset + j);
                    gridPane.setHalignment(tempImageView, HPos.CENTER);
                    gridPane.setValignment(tempImageView, VPos.CENTER);
                }*/
            }
        });
    }

    @FXML
    public void handleBackToGame(ActionEvent event) {
        goToScene("/fxml/game.fxml");
    }

    @Override
    public void update() {
        printBoard();
    }
}
