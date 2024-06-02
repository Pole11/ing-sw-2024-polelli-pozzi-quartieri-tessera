package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.controllers;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.CornerPos;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable.PlaceCardCommandRunnable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.*;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.controlsfx.control.spreadsheet.Grid;

import java.util.ArrayList;
import java.util.HashMap;

public class GUIControllerBoard extends GUIController {
    @FXML private VBox mainContainerBoard;
    int cornerId, cardId;

    public GUIControllerBoard() {
        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                if (getViewModel() != null) {
                    printBoard();
                    updatePlayerHand(-1, -1);
                }
                cornerId = -1;
                cardId = -1;
            }
        });
    }

    public void printSelectedCorner(int cardId, int cornerId) {
        Platform.runLater(() -> {
            int imageHeight = 120;
            String url = "/img/carte_" +
                    (getViewModel().getHandCardsSide(cardId).equals(Side.FRONT) ? "fronte" : "retro") +
                    "/" +
                    cardId +
                    ".jpg";
            Pane handCardContainer = new Pane();
            ImageView handCardImageView = createCardImageView(url, imageHeight);

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

    public void updatePlayerHand(int handCardId, int handCornerId) {
        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                HashMap<Integer, Side> playerHandCards = new HashMap<>();
                getViewModel().getHand(getViewModel().getPlayerIndex()).forEach(e -> {
                    playerHandCards.put(e, getViewModel().getHandCardsSide(e));
                });

                Pane handContainer = null;

                handContainer = (Pane) mainContainerBoard.lookup("#playerHandContainerGame");
                if (handContainer != null) { handContainer.getChildren().clear(); }

                // TODO: get my player id
                for (Integer cardIdIterator : playerHandCards.keySet()) {
                    int imageHeight = 100;
                    Pane handCardContainer = new Pane();
                    ImageView tempImageView;
                    if (playerHandCards.get(cardIdIterator) == Side.FRONT) {
                        tempImageView = createCardImageView("/img/carte_fronte/" + cardIdIterator + ".jpg", imageHeight);
                    } else {
                        tempImageView = createCardImageView("/img/carte_retro/" + cardIdIterator + ".jpg", imageHeight);
                    }
                    tempImageView.setId("player" + getViewModel().getPlayerIndex() + "card" + cardIdIterator);
                    //tempImageView.getStyleClass().add("imageWithBorder");
                    handCardContainer.getChildren().add(tempImageView);
                    if (handContainer != null) handContainer.getChildren().add(handCardContainer);

                    if (cardIdIterator == handCardId) {
                        int rectX = 0, rectY = 0;
                        if (cornerId == 0 || cornerId == 3) rectX = 0; else rectX = imageHeight*3/4;
                        if (cornerId == 0 || cornerId == 1) rectY = 0; else rectY = imageHeight/2;

                        Rectangle clickedRectangle = new Rectangle(rectX, rectY,imageHeight*3/4, imageHeight/2);
                        clickedRectangle.setFill(new Color(0,0,0,0.4));

                        handCardContainer.getChildren().add(clickedRectangle);
                    }

                    tempImageView.getStyleClass().add("clickable");
                    tempImageView.setOnMousePressed(mouseEvent -> {
                        cardId = cardIdIterator;
                        // if phase is placing
                        Point2D tempImageViewPosition = tempImageView.localToScene(0,0);
                        if (mouseEvent.getSceneX() < tempImageViewPosition.getX() + tempImageView.getBoundsInLocal().getWidth()/2) { // left
                            if (mouseEvent.getSceneY() < tempImageViewPosition.getY() + tempImageView.getBoundsInLocal().getHeight()/2) { // top left
                                cornerId = 0;
                            } else { // down left
                                cornerId = 3;
                            }
                        } else { // right
                            if (mouseEvent.getSceneY() < tempImageViewPosition.getY() + tempImageView.getBoundsInLocal().getHeight()/2) { // top right
                                cornerId = 1;
                            } else { // down right
                                cornerId = 2;
                            }
                        }
                        updatePlayerHand(cardIdIterator, cornerId);
                    });
                }
            }
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

                ArrayList<ArrayList<Integer>> playerBoard = getViewModel().getPlayerBoard(getViewModel().getPlayerIndex()); // the first arg is the index of the player to print the board of

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
                                        if (cornerId >= 0 && cardId >= 0) {
                                            PlaceCardCommandRunnable command = new PlaceCardCommandRunnable();
                                            command.setParams(cardId, ele, finalTableCornerPos, getViewModel().getHandCardsSide(cardId));
                                            addCommand(command, thisController);
                                            //showAlert(Alert.AlertType.INFORMATION, "Placed card", "Thank you for placing the card");
                                            goToScene("/fxml/board.fxml");
                                        } else {
                                            showAlert(Alert.AlertType.INFORMATION, "Placed card", "Incorrect card placing");
                                        }
                                    });
                                }
                            }
                        }
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
