package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.controllers;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.GUIApplication;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.*;
import javafx.application.*;
import javafx.collections.ObservableList;
import javafx.fxml.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.*;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;

public class GUIControllerGame extends GUIController {
    @FXML private BorderPane mainContainerGame;
    @FXML private VBox sharedGoldContainerGame;
    @FXML private VBox plateauContainerGame;
    @FXML private VBox sharedResourceContainerGame;
    @FXML private Stage dialog;
    @FXML private Point2D targetPoint;

    private HashMap<Integer, Side> playerHandCards;
    int meId;
    private ArrayList<ArrayList<Integer>> playerBoard;
    private HashMap<Integer, Side> placedSideMap;

    public GUIControllerGame() {
        playerHandCards = new HashMap<>();
        playerBoard = new ArrayList<>();
        placedSideMap = new HashMap<>();
        meId = 1;

        initTable(4, 61, 11, 42, 27, 63, 3);
        highlightCurrentPlayerTable();
    }

    private ImageView createPlateauImageView() {
        String imageUrl = getClass().getResource("/img/plateau_score.jpg").toExternalForm();
        Image image = new Image(imageUrl);

        PixelReader reader = image.getPixelReader();
        WritableImage imageWritable = new WritableImage(reader, 48, 49, 460, 926);

        ImageView imageView = new ImageView(imageWritable);

        imageView.setFitHeight(350);
        imageView.setPreserveRatio(true);

        return imageView;
    }

    public void initTable(int numPlayers, int firstGoldDeckCardId, int firstResourceDeckCardId, int firstSharedGoldCardId, int firstSharedResourceCardId, int secondSharedGoldCardId, int secondSharedResourceCardId) {
        printDecks(firstGoldDeckCardId, firstResourceDeckCardId, firstSharedGoldCardId, firstSharedResourceCardId, secondSharedGoldCardId, secondSharedResourceCardId);

        // TODO: get the player hand
        playerHandCards.put(30, Side.FRONT);
        playerHandCards.put(3, Side.BACK);
        playerHandCards.put(45, Side.FRONT);
        for (int i = 1; i <= numPlayers; i++) initPlayerHand(i, playerHandCards, 90);
    }

    public void printDecks(int firstGoldDeckCardId, int firstResourceDeckCardId, int firstSharedGoldCardId, int firstSharedResourceCardId, int secondSharedGoldCardId, int secondSharedResourceCardId) {
        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                sharedGoldContainerGame.getChildren().add(new Text("Gold Deck"));
                ImageView goldDeckImageView = createCardImageView("/img/carte_retro/" + firstGoldDeckCardId + ".jpg", 75);
                goldDeckImageView.getStyleClass().add("clickable");
                BorderPane goldDeckPane = new BorderPane(goldDeckImageView);
                goldDeckPane.getStyleClass().add("cardDeck");
                // add event
                sharedGoldContainerGame.getChildren().add(goldDeckPane);
                sharedGoldContainerGame.getChildren().add(new Text("Shared Gold"));
                // add event
                ImageView firstSharedGoldCardImageView = createCardImageView("/img/carte_fronte/" + firstSharedGoldCardId + ".jpg", 75);
                firstSharedGoldCardImageView.getStyleClass().add("clickable");
                sharedGoldContainerGame.getChildren().add(firstSharedGoldCardImageView);
                // add event
                ImageView secondSharedGoldCardImageView = createCardImageView("/img/carte_fronte/" + secondSharedGoldCardId + ".jpg", 75);
                secondSharedGoldCardImageView.getStyleClass().add("clickable");
                sharedGoldContainerGame.getChildren().add(secondSharedGoldCardImageView);

                plateauContainerGame.getChildren().add(createPlateauImageView());

                sharedResourceContainerGame.getChildren().add(new Text("Resource Deck"));
                ImageView resourceDeckImageView = createCardImageView("/img/carte_retro/" + firstResourceDeckCardId + ".jpg", 75);
                resourceDeckImageView.getStyleClass().add("clickable");
                BorderPane resourceDeckPane = new BorderPane(resourceDeckImageView);
                resourceDeckPane.getStyleClass().add("cardDeck");
                // add event
                sharedResourceContainerGame.getChildren().add(resourceDeckPane);

                sharedResourceContainerGame.getChildren().add(new Text("Shared Resource"));
                // add event
                ImageView firstSharedResourceCardImageView = createCardImageView("/img/carte_fronte/" + firstSharedResourceCardId + ".jpg", 75);
                firstSharedResourceCardImageView.getStyleClass().add("clickable");
                sharedResourceContainerGame.getChildren().add(firstSharedResourceCardImageView);
                // add event
                ImageView secondSharedResourceCardImageView = createCardImageView("/img/carte_fronte/" + secondSharedResourceCardId + ".jpg", 75);
                secondSharedResourceCardImageView.getStyleClass().add("clickable");
                sharedResourceContainerGame.getChildren().add(secondSharedResourceCardImageView);

                goldDeckPane.setOnMouseClicked(mouseEvent -> { handleDrawCard(DrawType.DECKGOLD); });
                firstSharedGoldCardImageView.setOnMouseClicked(mouseEvent -> { handleDrawCard(DrawType.SHAREDGOLD1); });
                secondSharedGoldCardImageView.setOnMouseClicked(mouseEvent -> { handleDrawCard(DrawType.SHAREDGOLD2); });
                resourceDeckPane.setOnMouseClicked(mouseEvent -> { handleDrawCard(DrawType.DECKRESOURCE); });
                firstSharedResourceCardImageView.setOnMouseClicked(mouseEvent -> { handleDrawCard(DrawType.SHAREDRESOURCE1); });
                secondSharedResourceCardImageView.setOnMouseClicked(mouseEvent -> { handleDrawCard(DrawType.SHAREDRESOURCE2); });

                addHover(goldDeckPane);
                addHover(firstSharedGoldCardImageView);
                addHover(secondSharedGoldCardImageView);
                addHover(resourceDeckPane);
                addHover(firstSharedResourceCardImageView);
                addHover(secondSharedResourceCardImageView);

            }
        });
    }

    public void highlightCurrentPlayerTable() {
        Platform.runLater(() -> {
                // TODO: get current player id
                int idCurrentPlayer = 3;
                Node currentPlayerHBox = mainContainerGame.lookup("#player" + idCurrentPlayer + "ContainerGame");

                // TODO: get current player color
                Color currentPlayerColor = Color.BLUE;
                currentPlayerHBox.getStyleClass().add(currentPlayerColor.toString().toLowerCase() + "Background");
            }
        );
    }

    public void initPlayerHand(int playerId, HashMap<Integer, Side> playerHandCards, int secreteObjCardId) {
        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                // TODO: get player nickname
                VBox infoContainerVBox = new VBox();
                String currentPlayerNickname = "Player nickname " + playerId;
                Text nicknameText = new Text(currentPlayerNickname);
                Button expandButton = new Button("Expand Board");
                expandButton.setOnMousePressed(mouseEvent -> {
                    printBoard(playerId);
                });
                infoContainerVBox.getChildren().addAll(nicknameText, expandButton);
                infoContainerVBox.setAlignment(Pos.CENTER);

                if (playerId == 1 || playerId == 2) {
                    HBox playerContainer = (HBox) mainContainerGame.lookup("#player" + playerId + "ContainerGame");
                    playerContainer.getChildren().add(infoContainerVBox);
                } else {
                    VBox playerContainer = (VBox) mainContainerGame.lookup("#player" + playerId + "ContainerGame");
                    playerContainer.getChildren().add(infoContainerVBox);
                }

                updatePlayerHand(playerId, playerHandCards);
                if (meId == playerId) printPrivateObjective(playerId, secreteObjCardId);
            }
        });
    }

    public void updatePlayerHand(int playerId, HashMap<Integer, Side> playerHandCards) {
        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                HBox handContainerHBox = null;
                VBox handContainerVBox = null;

                if (playerId == 1 || playerId == 2) {
                    handContainerHBox = (HBox) mainContainerGame.lookup("#player" + playerId + "HandContainerGame");
                    if (handContainerHBox != null) { handContainerHBox.getChildren().clear(); }
                } else {
                    handContainerVBox = (VBox) mainContainerGame.lookup("#player" + playerId + "HandContainerGame");
                    if (handContainerVBox != null) { handContainerVBox.getChildren().clear(); }
                }

                // TODO: get my player id
                for (Integer cardId : playerHandCards.keySet()) {
                    ImageView tempImageView;
                    if ((playerId == meId && playerHandCards.get(cardId) == Side.FRONT) || (playerId != meId && playerHandCards.get(cardId) == Side.BACK)) {
                        tempImageView = createCardImageView("/img/carte_fronte/" + cardId + ".jpg", 100);
                    } else {
                        tempImageView = createCardImageView("/img/carte_retro/" + cardId + ".jpg", 100);
                    }
                    tempImageView.setId("player" + playerId + "card" + cardId);
                    //tempImageView.getStyleClass().add("imageWithBorder");
                    if (playerId == 1 || playerId == 2) {
                        handContainerHBox.getChildren().add(tempImageView);
                    } else {
                        handContainerVBox.getChildren().add(tempImageView);
                    }
                    if (playerId == meId) {
                        tempImageView.getStyleClass().add("clickable");
                        tempImageView.setOnMousePressed(mouseEvent -> {
                            if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                                flipCard(playerId, cardId);
                            } else if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                                // if phase is placing
                                Point2D tempImageViewPosition = tempImageView.localToScene(0,0);
                                int cornerId;
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
                                //Line dragLine = new Line(mouseEvent.getSceneX(), mouseEvent.getSceneY(), 270, 40);
                                //mainContainerGame.getChildren().add(dragLine);
                                Circle clickedCircle = new Circle(mouseEvent.getSceneX(), mouseEvent.getSceneY(), 10);
                                mainContainerGame.getChildren().removeIf(n -> n instanceof Circle);
                                mainContainerGame.getChildren().add(clickedCircle);
                                printBoard(playerId);
                            }
                        });
                    }
                }
            }
        });
    }

    public void flipCard(int playerId, int cardId) {
        if (playerHandCards.get(cardId) == Side.FRONT) {
            playerHandCards.put(cardId, Side.BACK);
        } else {
            playerHandCards.put(cardId, Side.FRONT);
        }
        updatePlayerHand(playerId, playerHandCards);
    }

    public void printPrivateObjective(int playerId, int cardId) {
        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                ImageView tempImageView = createCardImageView("/img/carte_fronte/" + cardId + ".jpg", 100);
                //tempImageView.getStyleClass().add("imageWithBorder");
                if (playerId == 1 || playerId == 2) {
                    HBox playerContainer = (HBox) mainContainerGame.lookup("#player" + playerId + "ContainerGame");
                    playerContainer.getChildren().add(tempImageView);
                } else {
                    VBox playerContainer = (VBox) mainContainerGame.lookup("#player" + playerId + "ContainerGame");
                    playerContainer.getChildren().add(tempImageView);
                }
            }
        });
    }

    public void printBoard(int playerId) {
        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                int boardImageWidth = 100;
                int gridPaneVPadding = 50;
                int gridPaneHgap = -30;
                int gridPaneVgap = -35;

                dialog = new Stage();
                dialog.initModality(Modality.APPLICATION_MODAL);
                dialog.initOwner(GUIApplication.getMainStage());

                Pane dialogPane = new Pane();

                playerBoard = new ArrayList<>();
                playerBoard.add(new ArrayList<>(Arrays.asList(null, 2, 6, null)));
                playerBoard.add(new ArrayList<>(Arrays.asList(3, 1, 4, 5)));
                playerBoard.add(new ArrayList<>(Arrays.asList(null, 7, 76, null)));
                playerBoard.add(new ArrayList<>(Arrays.asList(null, 71, 73, null)));
                playerBoard.add(new ArrayList<>(Arrays.asList(null, 23, 56, null)));

                placedSideMap.put(2, Side.FRONT);
                placedSideMap.put(6, Side.BACK);
                placedSideMap.put(3, Side.FRONT);
                placedSideMap.put(1, Side.FRONT);
                placedSideMap.put(4, Side.BACK);
                placedSideMap.put(5, Side.FRONT);
                placedSideMap.put(7, Side.FRONT);
                placedSideMap.put(76, Side.FRONT);
                placedSideMap.put(71, Side.FRONT);
                placedSideMap.put(73, Side.FRONT);
                placedSideMap.put(23, Side.FRONT);
                placedSideMap.put(56, Side.BACK);

                GridPane gridPane = new GridPane();
                dialogPane.getChildren().add(gridPane);
                gridPane.setHgap(gridPaneHgap); // Spacing orizzontale
                gridPane.setVgap(gridPaneVgap); // Spacing verticale
                gridPane.setPadding(new Insets(gridPaneVPadding)); // Margine di 20 pixel su tutti i lati
                for(ArrayList<Integer> row : playerBoard) {
                    for (Integer ele : row) {
                        if (ele != null) {
                            ImageView tempImageView;
                            if (placedSideMap.get(ele) == Side.FRONT) {
                                tempImageView = createCardImageView("/img/carte_fronte/" + ele + ".jpg", boardImageWidth);
                            } else {
                                tempImageView = createCardImageView("/img/carte_retro/" + ele + ".jpg", boardImageWidth);
                            }
                            tempImageView.getStyleClass().add("clickable");
                            tempImageView.setOnMousePressed(mouseEvent -> {
                                targetPoint = new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY());
                                dialogPane.getChildren().removeIf(n -> n instanceof Circle);
                                Circle clickedCircle = new Circle(0, 0, 10);
                                clickedCircle.setLayoutX(mouseEvent.getSceneX());
                                clickedCircle.setLayoutY(mouseEvent.getSceneY());

                                disappearAfter(1, clickedCircle, dialogPane);
                                dialogPane.getChildren().add(clickedCircle);
                            });
                            gridPane.add(tempImageView, row.indexOf(ele), playerBoard.indexOf(row));
                        }
                    }
                }

                Scene dialogScene = new Scene(dialogPane, (boardImageWidth - gridPaneHgap) * playerBoard.getFirst().size() + 2 * gridPaneVPadding, (boardImageWidth*(2/3) - 2 * gridPaneVgap) * playerBoard.size() + 3 * gridPaneVPadding);
                dialogScene.getStylesheets().add(getClass().getResource("/style/game.css").toExternalForm());
                dialogScene.getStylesheets().add(getClass().getResource("/style/main.css").toExternalForm());
                dialog.setTitle("Board of " + playerId);
                dialog.setScene(dialogScene);
                dialog.show();
            }
        });
    }

    @FXML
    public void handlePlaceCard(ActionEvent event) {
    }

    public void handleDrawCard(DrawType drawType) {
        try {
            getServer().drawCard(getClient(), drawType);
        } catch (RemoteException e) {
            setServerError("There was an error while drawing the card, please try again");
        }
    }
}
