package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.controllers;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.CornerPos;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable.PlaceCardCommandRunnable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.controlsfx.control.spreadsheet.Grid;

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
        GUIController thisController = this;
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

                GridPane gridPane = new GridPane();
                //gridPane.setGridLinesVisible(true);
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

                for (int i = 0; i < playerBoard.size(); i++) {
                    for (int j = 0; j < playerBoard.getFirst().size(); j++) {
                        if (playerBoard.get(i).get(j) != -1) {
                            int ele = playerBoard.get(i).get(j);
                            ImageView tempImageView;
                            String imageUrl = null;
                            if (getViewModel().getPlacedCardSide(ele) == Side.FRONT) {
                                imageUrl = "/img/carte_fronte/" + ele + ".jpg";
                            } else {
                                imageUrl = "/img/carte_retro/" + ele + ".jpg";
                            }
                            tempImageView = createCardImageView(imageUrl, boardImageHeight);
                            GridPane imageGridPane = new GridPane();
                            imageGridPane.setGridLinesVisible(true);

                            for (int k = 0; k < 2; k++) {
                                imageGridPane.getRowConstraints().add(new RowConstraints(boardImageHeight/2));
                            }
                            for (int k = 0; k < 2; k++) {
                                imageGridPane.getColumnConstraints().add(new ColumnConstraints(boardImageWidth/2));
                            }

                            imageGridPane.add(tempImageView, 0, 0);
                            gridPane.add(imageGridPane, j, i);
                            gridPane.setHalignment(imageGridPane, HPos.CENTER);
                            gridPane.setValignment(imageGridPane, VPos.CENTER);
                            imageGridPane.setHalignment(tempImageView, HPos.LEFT);
                            imageGridPane.setValignment(tempImageView, VPos.TOP);

                            for (int k = 0; k < 2; k++) {
                                for (int w = 0; w < 2; w++) {
                                    Pane dummyCell = new Pane();
                                    dummyCell.setPrefWidth(boardImageWidth/2);
                                    dummyCell.setPrefHeight(boardImageHeight/2);
                                    dummyCell.getStyleClass().add("clickable");
                                    int cornerId = (k == 1 && w == 0) ? 3 : k+w;
                                    CornerPos tableCornerPos = null;
                                    switch(cornerId) {
                                        case(0) -> tableCornerPos = CornerPos.UPLEFT;
                                        case(1) -> tableCornerPos = CornerPos.UPRIGHT;
                                        case(2) -> tableCornerPos = CornerPos.DOWNRIGHT;
                                        case(3) -> tableCornerPos = CornerPos.DOWNLEFT;
                                    }
                                    dummyCell.setId(ele + "[" + cornerId + "]");
                                    imageGridPane.add(dummyCell, w, k);
                                    addHoverBgColor(dummyCell);
                                    CornerPos finalTableCornerPos = tableCornerPos;
                                    dummyCell.setOnMouseClicked((mouseEvent) -> {
                                        PlaceCardCommandRunnable command = new PlaceCardCommandRunnable();
                                        command.setParams(getParamsMap().get("cardId"), ele, finalTableCornerPos, getViewModel().getHandCardsSide(getParamsMap().get("cardId")));
                                        addCommand(command, thisController);
                                        showAlert(Alert.AlertType.INFORMATION, "Placed card", "Thank you for placing the card");
                                        goToScene("/fxml/game.fxml");
                                    });
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    public void printBoard2() {
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
                        if (i >= rowOffset && j >= colOffset && i <= playerBoard.size() && j <= playerBoard.get(i - rowOffset).size() && playerBoard.get(i - rowOffset) != null && playerBoard.get(i - rowOffset).get(j - colOffset) != null && playerBoard.get(i - rowOffset).get(j - colOffset) != -1) {
                            int ele = playerBoard.get(i - rowOffset).get(j - colOffset);
                            ImageView tempImageView;
                            String imageUrl = null;
                            if (getViewModel().getPlacedCardSide(ele) == Side.FRONT) {
                                imageUrl = "/img/carte_fronte/" + ele + ".jpg";
                            } else {
                                imageUrl = "/img/carte_retro/" + ele + ".jpg";
                            }
                            tempImageView = createCardImageView(imageUrl, boardImageHeight);
                            tempImageView.setId("" + ele);
                            gridPane.add(tempImageView, j, i);
                            gridPane.setHalignment(tempImageView, HPos.CENTER);
                            gridPane.setValignment(tempImageView, VPos.CENTER);
                        }
                    }
                }

                for (int i = 0; i < playerBoard.size() + 2; i++) {
                    for (int j = 0; j < playerBoard.getFirst().size() + 2; j++) {
                        Pane dummyCell = new Pane();
                        dummyCell.setPrefWidth(boardImageWidth);
                        dummyCell.setPrefHeight(boardImageHeight);
                        dummyCell.getStyleClass().add("clickable");
                        // setta l'id per capire che angolo viene coperto
                        // come id metti quello di una carta adiacente e che ha un angolo disponibile (se c'è)

                        // initialize the id to -1
                        // cycle all the 8 positions around the card
                            // check if there is a card
                                // if there is card
                                    // cycle all the touching angles
                                        // if the touching angles is not hidden
                                            // set the id (or a property) with the cardId and the cornerId

                        gridPane.add(dummyCell, j, i);
                        addHoverBgColor(dummyCell);
                        dummyCell.setOnMouseClicked((mouseEvent) -> {
                            // chiama il place card

                            // if the id (or a property) of the card > 0
                                // placecard with the given values
                        });

                        /*String imageUrl = "../img/carte_" +
                                (getViewModel().getHandCardsSide(getParamsMap().get("cardId")).equals(Side.FRONT) ? "fronte" : "retro") +
                                "/" +
                                getParamsMap().get("cardId") +
                                ".jpg";
                        addHoverBgImage(dummyCell, imageUrl);*/
                    }
                }
            }
        });
    }

    @Override
    public void setServerMessage(String serverMessage) {
        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                System.out.println("[DEBUG] Rendering server message: " + serverMessage);
                showAlert(Alert.AlertType.INFORMATION, "Message from server", serverMessage);
            }
        });
    }

    @Override
    public void setServerError(String serverMessage) {
        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                System.err.println("[DEBUG] Rendering server error: " + serverMessage);
                showAlert(Alert.AlertType.WARNING, "Error from server", serverMessage);
                goToScene("/fxml/game.fxml");
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
