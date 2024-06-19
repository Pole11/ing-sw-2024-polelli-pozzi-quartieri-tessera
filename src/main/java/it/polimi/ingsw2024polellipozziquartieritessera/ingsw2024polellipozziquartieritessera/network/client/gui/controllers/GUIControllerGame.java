package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.controllers;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Color;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.DrawType;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Message;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable.AddMessageCommandRunnable;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable.DrawCardCommandRunnable;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable.FlipCardCommandRunnable;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class GUIControllerGame extends GUIController {
    @FXML private BorderPane mainContainerGame;
    @FXML private VBox sharedGoldContainerGame;
    @FXML private VBox plateauContainerGame;
    @FXML private VBox sharedResourceContainerGame;
    @FXML private Label currentPhase;
    private HashMap<Integer, ArrayList<Integer>> plateauCoordinatedMap;
    private ListView<Text> chatListView;
    private boolean chatOpen;

    public GUIControllerGame() {
        chatListView = new ListView<>();

        rotatePlayerContainer();
        populatePlateauCoordinateMap();
        setFontSize(mainContainerGame);
        update();
    }

    private void populatePlateauCoordinateMap() {
        plateauCoordinatedMap = new HashMap<>();
        int plateauHeight = (int) (getWindowHeight() * 0.38);
        plateauCoordinatedMap.put(0,  new ArrayList<>(Arrays.asList(plateauHeight/4 - (int) (plateauHeight * 0.11), plateauHeight - (int) (plateauHeight * 0.068))));
        plateauCoordinatedMap.put(1,  new ArrayList<>(Arrays.asList(plateauHeight/4                               , plateauHeight - (int) (plateauHeight * 0.068))));
        plateauCoordinatedMap.put(2,  new ArrayList<>(Arrays.asList(plateauHeight/4 + (int) (plateauHeight * 0.11), plateauHeight - (int) (plateauHeight * 0.068))));
        plateauCoordinatedMap.put(3,  new ArrayList<>(Arrays.asList(plateauHeight/4 + (int) (plateauHeight * 0.17), plateauHeight - (int) (plateauHeight * 0.17))));
        plateauCoordinatedMap.put(4,  new ArrayList<>(Arrays.asList(plateauHeight/4 + (int) (plateauHeight * 0.057), plateauHeight - (int) (plateauHeight * 0.17))));
        plateauCoordinatedMap.put(5,  new ArrayList<>(Arrays.asList(plateauHeight/4 - (int) (plateauHeight * 0.057), plateauHeight - (int) (plateauHeight * 0.17))));
        plateauCoordinatedMap.put(6,  new ArrayList<>(Arrays.asList(plateauHeight/4 - (int) (plateauHeight * 0.17), plateauHeight - (int) (plateauHeight * 0.17))));
        plateauCoordinatedMap.put(7,  new ArrayList<>(Arrays.asList(plateauHeight/4 - (int) (plateauHeight * 0.17), plateauHeight - (int) (plateauHeight * 0.28))));
        plateauCoordinatedMap.put(8,  new ArrayList<>(Arrays.asList(plateauHeight/4 - (int) (plateauHeight * 0.057), plateauHeight - (int) (plateauHeight * 0.28))));
        plateauCoordinatedMap.put(9,  new ArrayList<>(Arrays.asList(plateauHeight/4 + (int) (plateauHeight * 0.057), plateauHeight - (int) (plateauHeight * 0.28))));
        plateauCoordinatedMap.put(10, new ArrayList<>(Arrays.asList(plateauHeight/4 + (int) (plateauHeight * 0.17), plateauHeight - (int) (plateauHeight * 0.28))));
        plateauCoordinatedMap.put(11, new ArrayList<>(Arrays.asList(plateauHeight/4 + (int) (plateauHeight * 0.17), plateauHeight - (int) (plateauHeight * 0.39))));
        plateauCoordinatedMap.put(12, new ArrayList<>(Arrays.asList(plateauHeight/4 + (int) (plateauHeight * 0.057), plateauHeight - (int) (plateauHeight * 0.39))));
        plateauCoordinatedMap.put(13, new ArrayList<>(Arrays.asList(plateauHeight/4 - (int) (plateauHeight * 0.057), plateauHeight - (int) (plateauHeight * 0.39))));
        plateauCoordinatedMap.put(14, new ArrayList<>(Arrays.asList(plateauHeight/4 - (int) (plateauHeight * 0.17), plateauHeight - (int) (plateauHeight * 0.39))));
        plateauCoordinatedMap.put(15, new ArrayList<>(Arrays.asList(plateauHeight/4 - (int) (plateauHeight * 0.17), plateauHeight - (int) (plateauHeight * 0.49))));
        plateauCoordinatedMap.put(16, new ArrayList<>(Arrays.asList(plateauHeight/4 - (int) (plateauHeight * 0.057), plateauHeight - (int) (plateauHeight * 0.49))));
        plateauCoordinatedMap.put(17, new ArrayList<>(Arrays.asList(plateauHeight/4 + (int) (plateauHeight * 0.057), plateauHeight - (int) (plateauHeight * 0.49))));
        plateauCoordinatedMap.put(18, new ArrayList<>(Arrays.asList(plateauHeight/4 + (int) (plateauHeight * 0.17), plateauHeight - (int) (plateauHeight * 0.49))));
        plateauCoordinatedMap.put(19, new ArrayList<>(Arrays.asList(plateauHeight/4 + (int) (plateauHeight * 0.17), plateauHeight - (int) (plateauHeight * 0.60))));
        plateauCoordinatedMap.put(20, new ArrayList<>(Arrays.asList(plateauHeight/4                               , plateauHeight - (int) (plateauHeight * 0.65))));
        plateauCoordinatedMap.put(21, new ArrayList<>(Arrays.asList(plateauHeight/4 - (int) (plateauHeight * 0.17), plateauHeight - (int) (plateauHeight * 0.60))));
        plateauCoordinatedMap.put(22, new ArrayList<>(Arrays.asList(plateauHeight/4 - (int) (plateauHeight * 0.17), plateauHeight - (int) (plateauHeight * 0.71))));
        plateauCoordinatedMap.put(23, new ArrayList<>(Arrays.asList(plateauHeight/4 - (int) (plateauHeight * 0.17), plateauHeight - (int) (plateauHeight * 0.82))));
        plateauCoordinatedMap.put(24, new ArrayList<>(Arrays.asList(plateauHeight/4 - (int) (plateauHeight * 0.10), plateauHeight - (int) (plateauHeight * 0.91))));
        plateauCoordinatedMap.put(25, new ArrayList<>(Arrays.asList(plateauHeight/4                               , plateauHeight - (int) (plateauHeight * 0.93))));
        plateauCoordinatedMap.put(26, new ArrayList<>(Arrays.asList(plateauHeight/4 + (int) (plateauHeight * 0.10), plateauHeight - (int) (plateauHeight * 0.91))));
        plateauCoordinatedMap.put(27, new ArrayList<>(Arrays.asList(plateauHeight/4 + (int) (plateauHeight * 0.17), plateauHeight - (int) (plateauHeight * 0.82))));
        plateauCoordinatedMap.put(28, new ArrayList<>(Arrays.asList(plateauHeight/4 + (int) (plateauHeight * 0.17), plateauHeight - (int) (plateauHeight * 0.71))));
        plateauCoordinatedMap.put(29, new ArrayList<>(Arrays.asList(plateauHeight/4                               , plateauHeight - (int) (plateauHeight * 0.79))));
    }

    private void clearAllchilds() {
        Platform.runLater(() -> {
            if (sharedResourceContainerGame != null) sharedResourceContainerGame.getChildren().clear();
            if (sharedGoldContainerGame != null) sharedGoldContainerGame.getChildren().clear();
            if (plateauContainerGame != null) {
                while (plateauContainerGame.getChildren().size() > 5) {
                    plateauContainerGame.getChildren().removeLast(); // the first two are the messages from server
                }
            }
            for (int i = 0; i < 4; i++) {
                if (mainContainerGame.lookup("#player" + i + "ContainerGame") != null) {
                    while (mainContainerGame.lookup("#player" + i + "ContainerGame") != null && ((Pane) mainContainerGame.lookup("#player" + i + "ContainerGame")).getChildren().size() > 1) {
                        ((Pane) mainContainerGame.lookup("#player" + i + "ContainerGame")).getChildren().removeLast();
                    }
                }
                if (mainContainerGame.lookup("#infoContainerPlayer" + i) != null) ((Pane) mainContainerGame.lookup("#infoContainerPlayer" + i)).getChildren().clear();
            }
        });
    }

    private ImageView createPlateauImageView(int plateauHeight) {
        String imageUrl = getClass().getResource("/img/plateau_score.jpg").toExternalForm();
        Image image = new Image(imageUrl);

        PixelReader reader = image.getPixelReader();
        WritableImage imageWritable = new WritableImage(reader, 48, 49, 460, 926);
        //imageWritable = resizeWritableImage(imageWritable, (int) (getWindowHeight()*0.51), getWindowHeight());

        ImageView imageView = new ImageView(imageWritable);

        imageView.setFitHeight(plateauHeight);
        imageView.setPreserveRatio(true);

        return imageView;
    }

    public void initTable() {
        ArrayList<HashMap<Integer, Side>> playerHandCards = new ArrayList<>();
        HashMap<Integer, String> nicknames = new HashMap<>();
        for (Integer playerId = 0; playerId < getViewModel().getPlayersSize(); playerId++) {
            HashMap<Integer, Side> playerHandCardsMap = new HashMap<>();
            for (Integer cardId : getViewModel().getHand(playerId)) {
                playerHandCardsMap.put(cardId, getViewModel().getHandCardsSide(cardId));
            }
            playerHandCards.add(playerHandCardsMap);
            nicknames.put(playerId, getViewModel().getNickname(playerId));
        }

        int firstCommonObjective = getViewModel().getObjectives()[0];
        int secondCommonObjective = getViewModel().getObjectives()[1];
        int secretObjectiveCardId = getViewModel().getObjectives()[2];
        printCommonObjective(firstCommonObjective, secondCommonObjective);
        printDecks();
        int meId = getViewModel().getPlayerIndex();
        for (int i = 0; i < playerHandCards.size(); i++) initPlayerHand(i, meId, nicknames, playerHandCards.get(i), secretObjectiveCardId);
    }

    public void printCommonObjective(int firstCommonObjective, int secondCommonObjective) {
        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                ImageView firstCommonObjectiveImageView = createCardImageView("/img/carte_fronte/" + firstCommonObjective + ".jpg", (int) (getWindowHeight()*0.081));
                BorderPane firstCommonObjectivePane = new BorderPane(firstCommonObjectiveImageView);
                sharedGoldContainerGame.getChildren().add(firstCommonObjectivePane);

                ImageView secondCommonObjectiveImageView = createCardImageView("/img/carte_fronte/" + secondCommonObjective + ".jpg", (int) (getWindowHeight()*0.081));
                BorderPane secondCommonObjectivePane = new BorderPane(secondCommonObjectiveImageView);
                sharedResourceContainerGame.getChildren().add(secondCommonObjectivePane);
            }
        });
    }

    public void printDecks() {
        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                sharedGoldContainerGame.getChildren().add(new Text("Gold Deck"));
                ImageView goldDeckImageView = createCardImageView("/img/carte_retro/" + getViewModel().getSharedCards()[5] + ".jpg", (int) (getWindowHeight()*0.090));
                goldDeckImageView.getStyleClass().add("clickable");
                BorderPane goldDeckPane = new BorderPane(goldDeckImageView);
                goldDeckPane.getStyleClass().add("cardDeck");
                // add event
                sharedGoldContainerGame.getChildren().add(goldDeckPane);
                sharedGoldContainerGame.getChildren().add(new Text("Shared Gold"));
                // add event
                ImageView firstSharedGoldCardImageView = createCardImageView("/img/carte_fronte/" + getViewModel().getSharedGoldCards()[0] + ".jpg", (int) (getWindowHeight()*0.090));
                firstSharedGoldCardImageView.getStyleClass().add("clickable");
                sharedGoldContainerGame.getChildren().add(firstSharedGoldCardImageView);
                // add event
                ImageView secondSharedGoldCardImageView = createCardImageView("/img/carte_fronte/" + getViewModel().getSharedGoldCards()[1] + ".jpg", (int) (getWindowHeight()*0.090));
                secondSharedGoldCardImageView.getStyleClass().add("clickable");
                sharedGoldContainerGame.getChildren().add(secondSharedGoldCardImageView);

                int plateauHeight = (int) (getWindowHeight() * 0.38);
                Pane plateauImageViewPane = new Pane();
                plateauImageViewPane.setPrefHeight(plateauHeight);
                plateauImageViewPane.setId("plateauImageViewPane");
                ImageView plateauImageView = createPlateauImageView(plateauHeight);
                plateauImageViewPane.getChildren().add(plateauImageView);
                plateauContainerGame.getChildren().add(plateauImageViewPane);

                sharedResourceContainerGame.getChildren().add(new Text("Resource Deck"));
                ImageView resourceDeckImageView = createCardImageView("/img/carte_retro/" + getViewModel().getSharedCards()[4] + ".jpg", (int) (getWindowHeight()*0.090));
                resourceDeckImageView.getStyleClass().add("clickable");
                BorderPane resourceDeckPane = new BorderPane(resourceDeckImageView);
                resourceDeckPane.prefWidthProperty().bind(resourceDeckImageView.fitWidthProperty());
                resourceDeckPane.getStyleClass().add("cardDeck");
                // add event
                sharedResourceContainerGame.getChildren().add(resourceDeckPane);

                sharedResourceContainerGame.getChildren().add(new Text("Shared Resource"));
                // add event
                ImageView firstSharedResourceCardImageView = createCardImageView("/img/carte_fronte/" + getViewModel().getSharedResourceCards()[0] + ".jpg", (int) (getWindowHeight()*0.090));
                firstSharedResourceCardImageView.getStyleClass().add("clickable");
                sharedResourceContainerGame.getChildren().add(firstSharedResourceCardImageView);
                // add event
                ImageView secondSharedResourceCardImageView = createCardImageView("/img/carte_fronte/" + getViewModel().getSharedResourceCards()[1] + ".jpg", (int) (getWindowHeight()*0.090));
                secondSharedResourceCardImageView.getStyleClass().add("clickable");
                sharedResourceContainerGame.getChildren().add(secondSharedResourceCardImageView);

                goldDeckPane.setOnMouseClicked(mouseEvent -> { handleDrawCard(DrawType.DECKGOLD); });
                firstSharedGoldCardImageView.setOnMouseClicked(mouseEvent -> { handleDrawCard(DrawType.SHAREDGOLD1); });
                secondSharedGoldCardImageView.setOnMouseClicked(mouseEvent -> { handleDrawCard(DrawType.SHAREDGOLD2); });
                resourceDeckPane.setOnMouseClicked(mouseEvent -> { handleDrawCard(DrawType.DECKRESOURCE); });
                firstSharedResourceCardImageView.setOnMouseClicked(mouseEvent -> { handleDrawCard(DrawType.SHAREDRESOURCE1); });
                secondSharedResourceCardImageView.setOnMouseClicked(mouseEvent -> { handleDrawCard(DrawType.SHAREDRESOURCE2); });

                addHoverRotate(goldDeckPane);
                addHoverRotate(firstSharedGoldCardImageView);
                addHoverRotate(secondSharedGoldCardImageView);
                addHoverRotate(resourceDeckPane);
                addHoverRotate(firstSharedResourceCardImageView);
                addHoverRotate(secondSharedResourceCardImageView);
            }
        });
    }

    public void highlightCurrentPlayerTable() {
        int idCurrentPlayer = getViewModel().getCurrentPlayer();
        Color color = getViewModel().getColorsMap(getViewModel().getCurrentPlayer());

        Platform.runLater(() -> {
                // TODO: un-highlight
                for (int i = 0; i < getViewModel().getPlayersSize(); i++) {
                    Node tempNode = mainContainerGame.lookup("#player" + i + "ContainerGame");
                    if (tempNode == null) continue;
                    Color currentColor = getViewModel().getColorsMap(i);
                    if (currentColor == null) continue;
                    tempNode.getStyleClass().remove(currentColor.toString().toLowerCase() + "Background");
                }

                // TODO: get current player id
                Node currentPlayerHBox = mainContainerGame.lookup("#player" + idCurrentPlayer + "ContainerGame"); // ROTATE HERE
                if (currentPlayerHBox == null) return;

                // TODO: get current player color
                Color currentPlayerColor = color;
                currentPlayerHBox.getStyleClass().add(currentPlayerColor.toString().toLowerCase() + "Background");
            }
        );
    }

    public void updatePoints() {
        Platform.runLater(() -> {
            for (int i = 0; i < getViewModel().getPlayersSize(); i++) {
                Pane plateauImageViewPane = (Pane) mainContainerGame.lookup("#plateauImageViewPane");
                if (plateauImageViewPane == null) return;
                Circle oldCircle = (Circle) plateauImageViewPane.lookup("#circlePoints" + i);
                //if (oldCircle == null) return;
                plateauImageViewPane.getChildren().remove(oldCircle);
                int x, y;
                try {
                    x = plateauCoordinatedMap.get(getViewModel().getPointsMap(i)).get(0);
                    y = plateauCoordinatedMap.get(getViewModel().getPointsMap(i)).get(1);
                } catch(NullPointerException e) {
                    x = plateauCoordinatedMap.get(0).get(0); // comment for real use
                    y = plateauCoordinatedMap.get(0).get(1); // comment for real use
                }
                Circle circle = new Circle(x, y, getWindowHeight()*0.01);
                circle.setId("circlePoints" + i);
                plateauImageViewPane.getChildren().add(circle);

                int offset = 5;
                if (getViewModel().getColorsMap(i) == Color.BLUE) { circle.setFill(javafx.scene.paint.Color.BLUE); circle.setTranslateX(-offset); circle.setTranslateY(-offset);}
                else if (getViewModel().getColorsMap(i) == Color.GREEN) { circle.setFill(javafx.scene.paint.Color.GREEN); circle.setTranslateX(offset); circle.setTranslateY(-offset); }
                else if (getViewModel().getColorsMap(i) == Color.RED) { circle.setFill(javafx.scene.paint.Color.RED); circle.setTranslateX(offset); circle.setTranslateY(offset); }
                else if (getViewModel().getColorsMap(i) == Color.YELLOW) { circle.setFill(javafx.scene.paint.Color.YELLOW); circle.setTranslateX(-offset); circle.setTranslateY(offset);}
            }
        });
    }

    public void initPlayerHand(int playerId, int meId,  HashMap<Integer, String> nicknames, HashMap<Integer, Side> playerHandCards, int secreteObjCardId) {
        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                // TODO: get player nickname
                VBox infoContainerVBox = new VBox();
                infoContainerVBox.setId("infoContainerPlayer" + playerId);
                String currentPlayerNickname = nicknames.get(playerId);
                Text nicknameText = new Text(currentPlayerNickname + " (" + getViewModel().getColorsMap(playerId) + " " + getViewModel().getPointsMap(playerId) + ")");
                Text connectionText = new Text();
                if (getViewModel().getConnession(playerId)) {
                    connectionText.setText("Connected");
                    connectionText.getStyleClass().remove("redText");
                    connectionText.getStyleClass().add("greenText");
                } else {
                    connectionText.setText("Not Connected");
                    connectionText.getStyleClass().remove("greenText");
                    connectionText.getStyleClass().add("redText");
                }

                //nicknameText.getStyleClass().add("nicknameText");
                Button expandButton = new Button("Expand Board");
                expandButton.setOnMousePressed(mouseEvent -> {
                    HashMap<String, Integer> paramsMap = new HashMap<>();
                    paramsMap.put("playerId", playerId);
                    goToScene("/fxml/board.fxml", paramsMap);
                });
                infoContainerVBox.getChildren().addAll(nicknameText, expandButton, connectionText);
                infoContainerVBox.setAlignment(Pos.CENTER);

                Pane playerContainer = (Pane) mainContainerGame.lookup("#player" + playerId + "ContainerGame"); // ROTATE HERE
                if (playerContainer == null) return;
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

                handContainer = (Pane) mainContainerGame.lookup("#player" + playerId + "HandContainerGame"); // ROTATE HERE
                if (handContainer != null) { handContainer.getChildren().clear(); }

                // TODO: get my player id
                for (Integer cardId : playerHandCards.keySet()) {
                    ImageView tempImageView;
                    if ((playerId == meId && playerHandCards.get(cardId) == Side.FRONT) || (playerId != meId && playerHandCards.get(cardId) == Side.BACK)) {
                        tempImageView = createCardImageView("/img/carte_fronte/" + cardId + ".jpg", (int) (getWindowHeight()*0.1));
                    } else {
                        tempImageView = createCardImageView("/img/carte_retro/" + cardId + ".jpg", (int) (getWindowHeight()*0.1));
                    }
                    if (tempImageView == null) continue;
                    tempImageView.setId("player" + playerId + "card" + cardId);
                    //tempImageView.getStyleClass().add("imageWithBorder");
                    if (handContainer != null) handContainer.getChildren().add(tempImageView);

                    tempImageView.getStyleClass().add("clickable");
                    if(playerId == meId) tempImageView.setOnMousePressed(mouseEvent -> { flipCard(cardId); });
                }

                if (playerId == meId) {
                    Separator sep1 = new Separator();
                    sep1.setOrientation(Orientation.VERTICAL);
                    if (handContainer != null) handContainer.getChildren().add(sep1);

                    Button btnPlaceCard = new Button("Place Card");
                    btnPlaceCard.setOnAction((mouseEvent) -> {
                        goToScene("/fxml/place.fxml");
                    });
                    if (handContainer != null) handContainer.getChildren().add(btnPlaceCard);
                }

                Separator sep2 = new Separator();
                sep2.setOrientation(Orientation.VERTICAL);
                if (handContainer != null) handContainer.getChildren().add(sep2);
            }
        });
    }

    public void flipCard(int cardId) {
        FlipCardCommandRunnable command = new FlipCardCommandRunnable();
        command.setCardId(cardId);
        addCommand(command,this);
    }

    public void printPrivateObjective(int playerId, int cardId) {
        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                ImageView tempImageView = createCardImageView("/img/carte_fronte/" + cardId + ".jpg", (int) (getWindowHeight()*0.09));
                if (tempImageView == null) return;
                //tempImageView.getStyleClass().add("imageWithBorder");

                Pane playerContainer = (Pane) mainContainerGame.lookup("#player" + playerId + "HandContainerGame"); // ROTATE HERE
                if (playerContainer == null) return;
                playerContainer.getChildren().add(tempImageView);
                Separator verticalSeparator = new Separator();
                verticalSeparator.setOrientation(Orientation.VERTICAL);
                playerContainer.getChildren().add(verticalSeparator);
            }
        });
    }

    public void handleDrawCard(DrawType drawType) {
        DrawCardCommandRunnable command = new DrawCardCommandRunnable();
        command.setDrawType(drawType);
        addCommand(command,this);
    }

    public void rotatePlayerContainer() {
        Platform.runLater(() -> {
            HBox topContainer = new HBox();
            topContainer.setAlignment(Pos.TOP_CENTER);
            topContainer.getStyleClass().add("horizontalPlayerContainer");
            HBox topContainerHand = new HBox();
            topContainerHand.setAlignment(Pos.CENTER);
            topContainer.getChildren().add(topContainerHand);

            HBox bottomContainer = new HBox();
            bottomContainer.setAlignment(Pos.TOP_CENTER);
            bottomContainer.getStyleClass().add("horizontalPlayerContainer");
            HBox bottomContainerHand = new HBox();
            bottomContainerHand.setAlignment(Pos.CENTER);
            bottomContainer.getChildren().add(bottomContainerHand);

            VBox leftContainer = new VBox();
            leftContainer.setAlignment(Pos.CENTER_LEFT);
            leftContainer.getStyleClass().add("verticalPlayerContainer");
            VBox leftContainerHand = new VBox();
            leftContainerHand.setAlignment(Pos.CENTER);
            leftContainer.getChildren().add(leftContainerHand);

            VBox rightContainer = new VBox();
            rightContainer.setAlignment(Pos.CENTER_RIGHT);
            rightContainer.getStyleClass().add("verticalPlayerContainer");
            VBox rightContainerHand = new VBox();
            rightContainerHand.setAlignment(Pos.CENTER);
            rightContainer.getChildren().add(rightContainerHand);

            mainContainerGame.setTop(topContainer);
            mainContainerGame.setBottom(bottomContainer);
            mainContainerGame.setLeft(leftContainer);
            mainContainerGame.setRight(rightContainer);

            int meId = getViewModel().getPlayerIndex();
            switch(meId) {
                case(0) -> { bottomContainer.setId("player0ContainerGame"); bottomContainerHand.setId("player0HandContainerGame");
                    topContainer.setId("player1ContainerGame"); topContainerHand.setId("player1HandContainerGame");
                    leftContainer.setId("player3ContainerGame"); leftContainerHand.setId("player3HandContainerGame");
                    rightContainer.setId("player2ContainerGame"); rightContainerHand.setId("player2HandContainerGame");
                }
                case(1) -> { bottomContainer.setId("player1ContainerGame"); bottomContainerHand.setId("player1HandContainerGame");
                    topContainer.setId("player0ContainerGame"); topContainerHand.setId("player0HandContainerGame");
                    leftContainer.setId("player2ContainerGame"); leftContainerHand.setId("player2HandContainerGame");
                    rightContainer.setId("player3ContainerGame"); rightContainerHand.setId("player3HandContainerGame");
                }
                case(2) -> { bottomContainer.setId("player2ContainerGame"); bottomContainerHand.setId("player2HandContainerGame");
                    topContainer.setId("player3ContainerGame"); topContainerHand.setId("player3HandContainerGame");
                    leftContainer.setId("player0ContainerGame"); leftContainerHand.setId("player0HandContainerGame");
                    rightContainer.setId("player1ContainerGame"); rightContainerHand.setId("player1HandContainerGame");
                }
                case(3) -> { bottomContainer.setId("player3ContainerGame"); bottomContainerHand.setId("player3HandContainerGame");
                    topContainer.setId("player2ContainerGame"); topContainerHand.setId("player2HandContainerGame");
                    leftContainer.setId("player1ContainerGame"); leftContainerHand.setId("player1HandContainerGame");
                    rightContainer.setId("player0ContainerGame"); rightContainerHand.setId("player0HandContainerGame");
                }
            }
        });
    }

    @FXML
    private void handleOpenChat(ActionEvent event) {
        Platform.runLater(() -> {
            if (chatOpen) return;
            chatOpen = true;

            Stage chatStage = new Stage();
            chatStage.setOnCloseRequest(closeEvent -> { chatOpen = false; });
            //chatStage.initModality(Modality.APPLICATION_MODAL);
            chatStage.initStyle(StageStyle.DECORATED);
            chatStage.setTitle("Chat");

            VBox containerVBox = new VBox();

            //ListView<Text> chatListView = new ListView<>();
            chatListView.setId("chatListView");
            VBox.setVgrow(chatListView, Priority.ALWAYS);
            containerVBox.getChildren().add(chatListView);

            populateChatListview();

            HBox newMessageContainer = new HBox();
            newMessageContainer.setMaxHeight(100);
            VBox.setVgrow(newMessageContainer, Priority.ALWAYS);
            newMessageContainer.setAlignment(Pos.CENTER);
            containerVBox.getChildren().add(newMessageContainer);
            TextArea textArea = new TextArea();
            textArea.getStyleClass().add("chatTextArea");
            newMessageContainer.getChildren().add(textArea);
            textArea.prefHeightProperty().bind(newMessageContainer.heightProperty());
            Button btnSend = new Button("Send");
            newMessageContainer.getChildren().add(btnSend);
            HBox.setHgrow(textArea, Priority.ALWAYS);
            btnSend.prefHeightProperty().bind(newMessageContainer.heightProperty());
            setFontSize(btnSend);
            btnSend.setOnAction(mouseEvent -> {
                AddMessageCommandRunnable command = new AddMessageCommandRunnable();
                command.setContent(textArea.getText());
                addCommand(command,this);
                textArea.setText("");
                populateChatListview();
            });

            // Create a Scene for the new Stage
            Scene scene = new Scene(containerVBox, (int) (getWindowHeight()*0.65), (int) (getWindowHeight()*0.43));
            String mainCss = getClass().getResource("/style/main.css").toExternalForm();
            String chatCss = getClass().getResource("/style/chat.css").toExternalForm();
            scene.getStylesheets().addAll(mainCss, chatCss);
            chatStage.setScene(scene);
            chatStage.show();
        });
    }

    private void populateChatListview() {
        chatListView.getItems().clear();

        ArrayList<Message> messages = getViewModel().getChat().getMessages(); // implement with view model

        if (messages != null) {
            for (int i = messages.size() - 1; i >= 0; i--) {
                Message m = messages.get(i);
                if (m == null) continue;
                Text tempText = new Text(getViewModel().getNickname(m.getAuthor()) + ": " + m.getContent());
                chatListView.getItems().add(tempText);
            }
        }
    }

    @FXML
    private void handleOpenGameRules(ActionEvent event) {
        Platform.runLater(() -> {
            // Create a new stage for the image viewer
            Stage imageStage = new Stage();
            //imageStage.initModality(Modality.APPLICATION_MODAL);
            imageStage.initStyle(StageStyle.DECORATED);
            imageStage.setTitle("Game Rules");

            // Create an ImageView to display the image
            javafx.scene.image.Image image = new javafx.scene.image.Image(Objects.requireNonNull(getClass().getResource("/img/rulebook.png")).toExternalForm());
            javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView(image);
            imageView.setPreserveRatio(true);
            //imageView.setFitWidth((int) (getWindowHeight()*0.63));  // Adjust the width as needed
            DoubleBinding adjustedWidth = Bindings.createDoubleBinding(
                    () -> imageStage.getWidth() - 20,
                    imageStage.widthProperty()
            );
            imageView.fitWidthProperty().bind(adjustedWidth);

            // Create a ScrollPane to make the ImageView scrollable
            javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane(imageView);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);

            // Create a Scene for the new Stage
            Scene scene = new Scene(scrollPane, (int) (getWindowHeight()*0.65), (int) (getWindowHeight()*0.43));
            imageStage.setScene(scene);
            imageStage.show();
        });
    }

    @Override
    public void update() {
        //populatePlateauCoordinateMap(); // only for TEST
        Platform.runLater(() -> {
            clearAllchilds();
            initTable();
            highlightCurrentPlayerTable();
            setCurrentPhase();
            populateChatListview();
            updatePoints();
        });
    }

    @FXML
    public void setCurrentPhase() {
        Platform.runLater(() -> {
            currentPhase.setText("The current phase is " + (getViewModel().getTurnPhase() != null ? getViewModel().getTurnPhase() : "BOH") + " and is the turn of " + getViewModel().getNickname(getViewModel().getCurrentPlayer()));
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
