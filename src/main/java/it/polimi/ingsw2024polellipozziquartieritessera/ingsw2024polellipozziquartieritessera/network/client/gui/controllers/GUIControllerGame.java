package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.controllers;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.GUIApplication;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.*;
import javafx.application.*;
import javafx.collections.ObservableList;
import javafx.fxml.*;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
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

import java.io.*;
import java.rmi.RemoteException;
import java.util.*;

public class GUIControllerGame extends GUIController {
    @FXML private BorderPane mainContainerGame;
    @FXML private VBox sharedGoldContainerGame;
    @FXML private VBox plateauContainerGame;
    @FXML private VBox sharedResourceContainerGame;

    public GUIControllerGame() {
        clearAllchilds();
    }

    private void clearAllchilds() {
        Platform.runLater(() -> {
            if (sharedResourceContainerGame != null) sharedResourceContainerGame.getChildren().clear();
            if (sharedGoldContainerGame != null) sharedGoldContainerGame.getChildren().clear();
            if (plateauContainerGame != null) {
                while (plateauContainerGame.getChildren().size() > 2) {
                    plateauContainerGame.getChildren().removeLast(); // the first two are the messages from server
                }
            }
            for (int i = 1; i <= 4; i++) {
                if (mainContainerGame.lookup("#player" + i + "ContainerGame") != null) {
                    while (((Pane) mainContainerGame.lookup("#player" + i + "ContainerGame")).getChildren().size() > 1) {
                        ((Pane) mainContainerGame.lookup("#player" + i + "ContainerGame")).getChildren().removeLast();
                    }
                }
                if (mainContainerGame.lookup("#infoContainerPlayer" + i) != null) ((Pane) mainContainerGame.lookup("#infoContainerPlayer" + i)).getChildren().clear();
            }
        });
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

    public void initTable(ArrayList<HashMap<Integer, Side>> playerHandCards, HashMap<Integer, String> nicknames, int meId, int firstGoldDeckCardId, int firstResourceDeckCardId, int firstSharedGoldCardId, int firstSharedResourceCardId, int secondSharedGoldCardId, int secondSharedResourceCardId, int firstCommonObjective, int secondCommonObjective, int secretObjectiveCardId) {
        printCommonObjective(firstCommonObjective, secondCommonObjective);
        printDecks(firstGoldDeckCardId, firstResourceDeckCardId, firstSharedGoldCardId, firstSharedResourceCardId, secondSharedGoldCardId, secondSharedResourceCardId);
        for (int i = 0; i < playerHandCards.size(); i++) initPlayerHand(i + 1, meId + 1, nicknames, playerHandCards.get(i), secretObjectiveCardId);
    }

    public void printCommonObjective(int firstCommonObjective, int secondCommonObjective) {
        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                ImageView firstCommonObjectiveImageView = createCardImageView("/img/carte_fronte/" + firstCommonObjective + ".jpg", 75);
                BorderPane firstCommonObjectivePane = new BorderPane(firstCommonObjectiveImageView);
                sharedGoldContainerGame.getChildren().add(firstCommonObjectivePane);

                ImageView secondCommonObjectiveImageView = createCardImageView("/img/carte_fronte/" + secondCommonObjective + ".jpg", 75);
                BorderPane secondCommonObjectivePane = new BorderPane(secondCommonObjectiveImageView);
                sharedResourceContainerGame.getChildren().add(secondCommonObjectivePane);
            }
        });
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

    public void highlightCurrentPlayerTable(int idCurrentPlayer) {
        Platform.runLater(() -> {
                // TODO: get current player id
                Node currentPlayerHBox = mainContainerGame.lookup("#player" + idCurrentPlayer + "ContainerGame");

                // TODO: get current player color
                Color currentPlayerColor = Color.BLUE;
                currentPlayerHBox.getStyleClass().add(currentPlayerColor.toString().toLowerCase() + "Background");
            }
        );
    }

    public void initPlayerHand(int playerId, int meId,  HashMap<Integer, String> nicknames, HashMap<Integer, Side> playerHandCards, int secreteObjCardId) {
        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                // TODO: get player nickname
                VBox infoContainerVBox = new VBox();
                infoContainerVBox.setId("infoContainerPlayer" + playerId);
                String currentPlayerNickname = nicknames.get(playerId - 1);
                Text nicknameText = new Text(currentPlayerNickname);
                Button expandButton = new Button("Expand Board");
                expandButton.setOnMousePressed(mouseEvent -> {
                    goToScene("/fxml/board.fxml", getTempViewModel());
                });
                infoContainerVBox.getChildren().addAll(nicknameText, expandButton);
                infoContainerVBox.setAlignment(Pos.CENTER);

                Pane playerContainer = (Pane) mainContainerGame.lookup("#player" + playerId + "ContainerGame");
                playerContainer.getChildren().add(infoContainerVBox);

                updatePlayerHand(playerId, meId, playerHandCards);
                if (meId == playerId) printPrivateObjective(meId, secreteObjCardId);
            }
        });
    }

    public void updatePlayerHand(int playerId, int meId, HashMap<Integer, Side> playerHandCards) {
        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                Pane handContainer = null;

                handContainer = (Pane) mainContainerGame.lookup("#player" + playerId + "HandContainerGame");
                if (handContainer != null) { handContainer.getChildren().clear(); }

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
                    if (handContainer != null) handContainer.getChildren().add(tempImageView);

                    if (playerId == meId) {
                        tempImageView.getStyleClass().add("clickable");
                        tempImageView.setOnMousePressed(mouseEvent -> {
                            if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                                flipCard(cardId);
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
                                goToScene("/fxml/board.fxml", getTempViewModel());
                            }
                        });
                    }
                }
                Separator verticalSeparator = new Separator();
                verticalSeparator.setOrientation(Orientation.VERTICAL);
                if (handContainer != null) handContainer.getChildren().add(verticalSeparator);
            }
        });
    }

    public void flipCard(int cardId) {
        try {
            getServer().flipCard(getClient(), cardId);
        } catch (RemoteException e) {
            Platform.runLater(() -> {
                setServerError("There was an error while flipping the card, please try again");
            });
        }
    }

    public void printPrivateObjective(int playerId, int cardId) {
        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                ImageView tempImageView = createCardImageView("/img/carte_fronte/" + cardId + ".jpg", 100);
                if (tempImageView == null) return;
                //tempImageView.getStyleClass().add("imageWithBorder");

                Pane playerContainer = (Pane) mainContainerGame.lookup("#player" + playerId + "HandContainerGame");
                playerContainer.getChildren().add(tempImageView);
                Separator verticalSeparator = new Separator();
                verticalSeparator.setOrientation(Orientation.VERTICAL);
                playerContainer.getChildren().add(verticalSeparator);
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
            Platform.runLater(() -> {
                setServerError("There was an error while drawing the card, please try again");
            });
        }
    }

    @Override
    public void update(ViewModel viewModel) {
        Platform.runLater(() -> {
            clearAllchilds();

            // delete everything  or find a way to differentially change the content of the view
            ArrayList<HashMap<Integer, Side>> playerHandCards = new ArrayList<>();
            HashMap<Integer, String> nicknames = new HashMap<>();
            for (Integer playerId = 0; playerId < viewModel.getPlayersSize(); playerId++) {
                HashMap<Integer, Side> playerHandCardsMap = new HashMap<>();
                for (Integer cardId : viewModel.getHand(playerId)) {
                    playerHandCardsMap.put(cardId, viewModel.getHandCardsSide(cardId));
                }
                playerHandCards.add(playerHandCardsMap);
                nicknames.put(playerId, viewModel.getNickname(playerId));
            }

            initTable(playerHandCards,
                    nicknames,
                    viewModel.getPlayerIndex(),
                    viewModel.getSharedCards()[0],
                    viewModel.getSharedCards()[1],
                    viewModel.getSharedGoldCards()[0],
                    viewModel.getSharedResourceCards()[0],
                    viewModel.getSharedGoldCards()[1],
                    viewModel.getSharedResourceCards()[1],
                    viewModel.getObjectives()[0],
                    viewModel.getObjectives()[1],
                    viewModel.getObjectives()[2]); // !!! get the right secret objective
            highlightCurrentPlayerTable(viewModel.getCurrentPlayer() + 1); // !!! make it work
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
            }
        });
    }
}
