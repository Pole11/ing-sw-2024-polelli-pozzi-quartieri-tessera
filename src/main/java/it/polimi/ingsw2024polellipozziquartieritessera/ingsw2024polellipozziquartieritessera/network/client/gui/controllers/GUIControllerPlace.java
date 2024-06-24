package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.controllers;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.CornerPos;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.ViewModel;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable.FlipCardCommandRunnable;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable.PlaceCardCommandRunnable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.HashMap;

public class GUIControllerPlace extends GUIController {
    @FXML private VBox mainContainerBoard;
    @FXML private GridPane gridPaneContainerBoard;
    @FXML private ScrollPane scrollPaneContainerBoard;
    private int cardId;
    private static int previousHandCardAmount = 3;

    public GUIControllerPlace() {
        setFontSize(mainContainerBoard);
        initPreviousHandCardAmount();
        initGridPaneContainerBoard();
        this.update();
    }

    public void initPreviousHandCardAmount() {
        Platform.runLater(() -> {
            if (getViewModel() != null) {
                System.out.println("Init");
                previousHandCardAmount = getViewModel().getHand(getViewModel().getPlayerIndex()).size();
            }
        });
    }

    public void updatePlayerHand(int handCardId) {
        Platform.runLater(() -> {
            HashMap<Integer, Side> playerHandCards = new HashMap<>();
            getViewModel().getHand(getViewModel().getPlayerIndex()).forEach(e -> {
                playerHandCards.put(e, getViewModel().getHandCardsSide(e));
            });

            Pane handContainer = null;

            handContainer = (Pane) mainContainerBoard.lookup("#playerHandContainerBoard");
            if (handContainer != null) { handContainer.getChildren().clear(); }

            // TODO: get my player id
            for (Integer cardIdIterator : playerHandCards.keySet()) {
                int imageHeight = (int) (getWindowHeight() * 0.108);
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

                // hover
                GridPane imageGridPane = new GridPane();
                //imageGridPane.setGridLinesVisible(false);

                imageGridPane.add(tempImageView, 0, 0);
                imageGridPane.setHalignment(tempImageView, HPos.LEFT);
                imageGridPane.setValignment(tempImageView, VPos.TOP);
                handCardContainer.getChildren().add(imageGridPane);

                Pane dummyCell = new Pane();
                dummyCell.getStyleClass().add("clickable");
                imageGridPane.add(dummyCell, 0, 0);
                addHoverBgColor(dummyCell);

                if (cardIdIterator == handCardId) {
                    int rectX = 0, rectY = 0;

                    Rectangle clickedRectangle = new Rectangle(rectX, rectY,imageHeight*3/2, imageHeight);
                    clickedRectangle.setFill(new Color(0,0,0,0.4));

                    Pane rectanglePane = new Pane();
                    handCardContainer.getChildren().add(rectanglePane);
                    rectanglePane.getChildren().add(clickedRectangle);
                }

                // handle event
                tempImageView.getStyleClass().add("clickable");
                imageGridPane.setOnMousePressed(mouseEvent -> {
                    if (mouseEvent.isPrimaryButtonDown()) {
                        cardId = cardIdIterator;
                        // if phase is placing
                        Point2D tempImageViewPosition = tempImageView.localToScene(0,0);
                        updatePlayerHand(cardIdIterator);
                    } else if (mouseEvent.isSecondaryButtonDown()) {
                        flipCard(cardIdIterator);
                    }
                });
            }
        });
    }

    public void printBoard() {
        GUIController thisController = this;
        Platform.runLater(() -> {
            ViewModel vm = getViewModel();
            ArrayList<ArrayList<Integer>> playerBoard = vm.getPlayerBoard(getViewModel().getPlayerIndex()); // the first arg is the index of the player to print the board of
            playerBoard = rotateBoard(playerBoard);
            resizeI(playerBoard);

            if (gridPaneContainerBoard != null) { gridPaneContainerBoard.getChildren().clear(); }

            for (int k = 0; k < getViewModel().getPlacingCardOrderMap(getViewModel().getPlayerIndex()).size(); k++) {
                for (int i = 0; i < playerBoard.size(); i++) {
                    for (int j = 0; j < playerBoard.getFirst().size(); j++) {
                        if (playerBoard.get(i).get(j) != -1 && getViewModel().getPlacingCardOrderMap(getViewModel().getPlayerIndex()).get(k).equals(playerBoard.get(i).get(j))) {
                            int ele = playerBoard.get(i).get(j);
                            ImageView tempImageView;
                            String imageUrl = null;
                            if (getViewModel().getPlacedCardSide(ele) == Side.FRONT) {
                                imageUrl = "/img/carte_fronte/" + ele + ".jpg";
                            } else {
                                imageUrl = "/img/carte_retro/" + ele + ".jpg";
                            }
                            tempImageView = createCardImageView(imageUrl, (int) (getWindowHeight() * 0.108));
                            GridPane imageGridPane = new GridPane();

                            for (int w = 0; w < 2; w++) {
                                imageGridPane.getRowConstraints().add(new RowConstraints(getWindowHeight() * 0.108/2));
                            }
                            for (int w = 0; w < 2; w++) {
                                imageGridPane.getColumnConstraints().add(new ColumnConstraints(getWindowHeight() * 0.108*3/4));
                            }

                            imageGridPane.add(tempImageView, 0, 0);
                            gridPaneContainerBoard.setHalignment(imageGridPane, HPos.CENTER);
                            gridPaneContainerBoard.setValignment(imageGridPane, VPos.CENTER);
                            imageGridPane.setHalignment(tempImageView, HPos.LEFT);
                            imageGridPane.setValignment(tempImageView, VPos.TOP);
                            gridPaneContainerBoard.add(imageGridPane, j, i);

                            for (int u = 0; u < 2; u++) {
                                for (int w = 0; w < 2; w++) {
                                    Pane dummyCell = new Pane();
                                    dummyCell.setPrefWidth(getWindowHeight() * 0.108*3/4);
                                    dummyCell.setPrefHeight(getWindowHeight() * 0.108/2);
                                    dummyCell.getStyleClass().add("clickable");
                                    int cornerId = (u == 1 && w == 0) ? 3 : u+w;
                                    CornerPos tableCornerPos = null;
                                    switch(cornerId) {
                                        case(0) -> tableCornerPos = CornerPos.UPLEFT;
                                        case(1) -> tableCornerPos = CornerPos.UPRIGHT;
                                        case(2) -> tableCornerPos = CornerPos.DOWNRIGHT;
                                        case(3) -> tableCornerPos = CornerPos.DOWNLEFT;
                                    }
                                    dummyCell.setId(ele + "[" + cornerId + "]");
                                    imageGridPane.add(dummyCell, w, u);
                                    addHoverBgColor(dummyCell);
                                    CornerPos finalTableCornerPos = tableCornerPos;
                                    dummyCell.setOnMousePressed((mouseEvent) -> {
                                        if (!mouseEvent.isControlDown()) {
                                            if (cornerId >= 0 && cardId >= 0) {
                                                PlaceCardCommandRunnable command = new PlaceCardCommandRunnable();
                                                command.setParams(cardId, ele, finalTableCornerPos, getViewModel().getHandCardsSide(cardId));
                                                addCommand(command, thisController);
                                                //showAlert(Alert.AlertType.INFORMATION, "Placed card", "Thank you for placing the card");
                                                //goToScene("/fxml/place.fxml");
                                            } else {
                                                showAlert(Alert.AlertType.INFORMATION, "Placed card", "Incorrect card placing");
                                            }
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

    private void initGridPaneContainerBoard() {
        int gridPaneVPadding = 50;
        int gridPaneHgap = 0;
        int gridPaneVgap = 0;

        Platform.runLater(() -> {
            int cornerWidth = (int) (getWindowHeight() * 0.0304);
            int cornerHeight = (int) (getWindowHeight() * 0.0310);

            ViewModel vm = getViewModel();
            ArrayList<ArrayList<Integer>> playerBoard = vm.getPlayerBoard(getViewModel().getPlayerIndex()); // the first arg is the index of the player to print the board of
            playerBoard = rotateBoard(playerBoard);
            resizeI(playerBoard);

            scrollPaneContainerBoard.prefViewportHeightProperty().bind(gridPaneContainerBoard.heightProperty());
            gridPaneContainerBoard.setHgap(gridPaneHgap); // Spacing orizzontale
            gridPaneContainerBoard.setVgap(gridPaneVgap); // Spacing verticale
            gridPaneContainerBoard.setPadding(new Insets(gridPaneVPadding)); // Margine di 20 pixel su tutti i lati
            //gridPaneContainerBoard.setGridLinesVisible(true);

            addPanning(gridPaneContainerBoard);

            for (int i = 0; i < playerBoard.size(); i++) {
                gridPaneContainerBoard.getRowConstraints().add(new RowConstraints(getWindowHeight() * 0.108 - cornerHeight));
            }
            for (int i = 0; i < playerBoard.getFirst().size(); i++) {
                gridPaneContainerBoard.getColumnConstraints().add(new ColumnConstraints((getWindowHeight() * 0.108*3/2) - cornerWidth));
            }
        });
    }

    @Override
    public void setServerMessage(String serverMessage) {
        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                //System.out.println("[DEBUG] Rendering server message: " + serverMessage);
                showAlert(Alert.AlertType.INFORMATION, "Message from server", serverMessage);
            }
        });
    }

    @Override
    public void setServerError(String serverMessage) {
        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                //System.err.println("[DEBUG] Rendering server error: " + serverMessage);
                showAlert(Alert.AlertType.WARNING, "Error from server", serverMessage);
            }
        });
    }

    @FXML
    public void handleBackToGame(ActionEvent event) {
        goToScene("/fxml/game.fxml");
    }

    @FXML
    public void handleRestoreView(ActionEvent event) {
        Platform.runLater(() -> {
            gridPaneContainerBoard.setTranslateX(0);
            gridPaneContainerBoard.setTranslateY(0);
        });
    }

    public void flipCard(int cardId) {
        FlipCardCommandRunnable command = new FlipCardCommandRunnable();
        command.setCardId(cardId);
        addCommand(command,this);
    }

    @Override
    public void update() {
        System.gc();
        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                try {
                    if (previousHandCardAmount == getViewModel().getHand(getViewModel().getPlayerIndex()).size() + 1) {
                        previousHandCardAmount = getViewModel().getHand(getViewModel().getPlayerIndex()).size();
                        goToScene("/fxml/game.fxml");
                    }
                } catch (Exception e) {
                    //System.out.println("Proceeding to open place card scene");
                }

                if (getViewModel() != null) {
                    printBoard();
                    updatePlayerHand(-1);
                }
                cardId = -1;
            }
        });
    }
}
