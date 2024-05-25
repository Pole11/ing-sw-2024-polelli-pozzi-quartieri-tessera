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
                    goToScene("/fxml/board.fxml", new String[]{playerId + ""});
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
                                //Circle clickedCircle = new Circle(mouseEvent.getSceneX(), mouseEvent.getSceneY(), 10);
                                //mainContainerGame.getChildren().removeIf(n -> n instanceof Circle);
                                //mainContainerGame.getChildren().add(clickedCircle);
                                goToScene("/fxml/board.fxml", new String[]{playerId + ""});
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

    @Override
    public void update(ViewModel viewModel) {

    }
}
