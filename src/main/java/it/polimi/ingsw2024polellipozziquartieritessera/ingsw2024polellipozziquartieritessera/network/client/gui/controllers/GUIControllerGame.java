package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.controllers;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable.DrawCardCommandRunnable;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable.FlipCardCommandRunnable;
import javafx.application.*;
import javafx.fxml.*;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;

import java.lang.reflect.Array;
import java.util.*;

import static javafx.scene.layout.TilePane.setAlignment;

public class GUIControllerGame extends GUIController {
    @FXML private BorderPane mainContainerGame;
    @FXML private VBox sharedGoldContainerGame;
    @FXML private VBox plateauContainerGame;
    @FXML private VBox sharedResourceContainerGame;
    @FXML private Label currentPhase;
    private HashMap<Integer, ArrayList<Integer>> plateauCoordinatedMap;
    private final int plateauHeight = 350;

    public GUIControllerGame() {
        clearAllchilds();
        update();
        populatePlateauCoordinateMap();
    }

    private void populatePlateauCoordinateMap() {
        plateauCoordinatedMap = new HashMap<>();
        plateauCoordinatedMap.put(0, new ArrayList<>(Arrays.asList(plateauHeight/4,plateauHeight - 20)));
        plateauCoordinatedMap.put(1, new ArrayList<>(Arrays.asList(0,0)));
        plateauCoordinatedMap.put(2, new ArrayList<>(Arrays.asList(0,0)));
        plateauCoordinatedMap.put(3, new ArrayList<>(Arrays.asList(0,0)));
        plateauCoordinatedMap.put(4, new ArrayList<>(Arrays.asList(0,0)));
        plateauCoordinatedMap.put(5, new ArrayList<>(Arrays.asList(0,0)));
        plateauCoordinatedMap.put(6, new ArrayList<>(Arrays.asList(0,0)));
        plateauCoordinatedMap.put(7, new ArrayList<>(Arrays.asList(0,0)));
        plateauCoordinatedMap.put(8, new ArrayList<>(Arrays.asList(0,0)));
        plateauCoordinatedMap.put(9, new ArrayList<>(Arrays.asList(0,0)));
        plateauCoordinatedMap.put(10, new ArrayList<>(Arrays.asList(0,0)));
        plateauCoordinatedMap.put(11, new ArrayList<>(Arrays.asList(0,0)));
        plateauCoordinatedMap.put(12, new ArrayList<>(Arrays.asList(0,0)));
        plateauCoordinatedMap.put(13, new ArrayList<>(Arrays.asList(0,0)));
        plateauCoordinatedMap.put(14, new ArrayList<>(Arrays.asList(0,0)));
        plateauCoordinatedMap.put(15, new ArrayList<>(Arrays.asList(0,0)));
        plateauCoordinatedMap.put(16, new ArrayList<>(Arrays.asList(0,0)));
        plateauCoordinatedMap.put(17, new ArrayList<>(Arrays.asList(0,0)));
        plateauCoordinatedMap.put(18, new ArrayList<>(Arrays.asList(0,0)));
        plateauCoordinatedMap.put(19, new ArrayList<>(Arrays.asList(0,0)));
        plateauCoordinatedMap.put(20, new ArrayList<>(Arrays.asList(0,0)));
        plateauCoordinatedMap.put(21, new ArrayList<>(Arrays.asList(0,0)));
        plateauCoordinatedMap.put(22, new ArrayList<>(Arrays.asList(0,0)));
        plateauCoordinatedMap.put(23, new ArrayList<>(Arrays.asList(0,0)));
        plateauCoordinatedMap.put(24, new ArrayList<>(Arrays.asList(0,0)));
        plateauCoordinatedMap.put(25, new ArrayList<>(Arrays.asList(0,0)));
        plateauCoordinatedMap.put(26, new ArrayList<>(Arrays.asList(0,0)));
        plateauCoordinatedMap.put(27, new ArrayList<>(Arrays.asList(0,0)));
        plateauCoordinatedMap.put(28, new ArrayList<>(Arrays.asList(0,0)));
        plateauCoordinatedMap.put(29, new ArrayList<>(Arrays.asList(0,0)));
    }

    private void clearAllchilds() {
        Platform.runLater(() -> {
            if (sharedResourceContainerGame != null) sharedResourceContainerGame.getChildren().clear();
            if (sharedGoldContainerGame != null) sharedGoldContainerGame.getChildren().clear();
            if (plateauContainerGame != null) {
                while (plateauContainerGame.getChildren().size() > 3) {
                    plateauContainerGame.getChildren().removeLast(); // the first two are the messages from server
                }
            }
            for (int i = 0; i < 4; i++) {
                if (mainContainerGame.lookup("#player" + i + "ContainerGame") != null) {
                    while (((Pane) mainContainerGame.lookup("#player" + i + "ContainerGame")).getChildren().size() > 1) {
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

        ImageView imageView = new ImageView(imageWritable);

        imageView.setFitHeight(plateauHeight);
        imageView.setPreserveRatio(true);

        return imageView;
    }

    public void initTable(ArrayList<HashMap<Integer, Side>> playerHandCards, HashMap<Integer, String> nicknames, int meId, int firstGoldDeckCardId, int firstResourceDeckCardId, int firstSharedGoldCardId, int firstSharedResourceCardId, int secondSharedGoldCardId, int secondSharedResourceCardId, int firstCommonObjective, int secondCommonObjective, int secretObjectiveCardId) {
        printCommonObjective(firstCommonObjective, secondCommonObjective);
        printDecks(firstGoldDeckCardId, firstResourceDeckCardId, firstSharedGoldCardId, firstSharedResourceCardId, secondSharedGoldCardId, secondSharedResourceCardId);
        for (int i = 0; i < playerHandCards.size(); i++) initPlayerHand(i, meId, nicknames, playerHandCards.get(i), secretObjectiveCardId);
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

                Pane plateauImageViewPane = new Pane();
                plateauImageViewPane.setPrefHeight(plateauHeight);
                plateauImageViewPane.setId("plateauImageViewPane");
                ImageView plateauImageView = createPlateauImageView(plateauHeight);
                plateauImageViewPane.getChildren().add(plateauImageView);
                plateauContainerGame.getChildren().add(plateauImageViewPane);

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

                addHoverRotate(goldDeckPane);
                addHoverRotate(firstSharedGoldCardImageView);
                addHoverRotate(secondSharedGoldCardImageView);
                addHoverRotate(resourceDeckPane);
                addHoverRotate(firstSharedResourceCardImageView);
                addHoverRotate(secondSharedResourceCardImageView);
            }
        });
    }

    public void highlightCurrentPlayerTable(int idCurrentPlayer, Color color) {
        Platform.runLater(() -> {
                // TODO: get current player id
                Node currentPlayerHBox = mainContainerGame.lookup("#player" + idCurrentPlayer + "ContainerGame");

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
                Circle oldCircle = (Circle) plateauImageViewPane.lookup("circlePoints" + i);
                plateauImageViewPane.getChildren().remove(oldCircle);
                //int x = plateauCoordinatedMap.get(getViewModel().getPointsMap(i)).get(0);
                //int y = plateauCoordinatedMap.get(getViewModel().getPointsMap(i)).get(1);
                int x = plateauCoordinatedMap.get(0).get(0);
                int y = plateauCoordinatedMap.get(0).get(1);
                System.out.println(x);
                System.out.println(y);
                Circle circle = new Circle(x, y, 15);
                circle.setId("circlePoints" + i);
                plateauImageViewPane.getChildren().add(circle);
                if (getViewModel().getColorsMap(i) == Color.BLUE) { circle.setFill(javafx.scene.paint.Color.BLUE); }
                else if (getViewModel().getColorsMap(i) == Color.GREEN) { circle.setFill(javafx.scene.paint.Color.GREEN); }
                else if (getViewModel().getColorsMap(i) == Color.RED) { circle.setFill(javafx.scene.paint.Color.RED); }
                else if (getViewModel().getColorsMap(i) == Color.YELLOW) { circle.setFill(javafx.scene.paint.Color.YELLOW); }
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
                Text nicknameText = new Text(currentPlayerNickname);
                Button expandButton = new Button("Expand Board");
                expandButton.setOnMousePressed(mouseEvent -> {
                    HashMap<String, Integer> paramsMap = new HashMap<>();
                    paramsMap.put("playerId", playerId);
                    goToScene("/fxml/board.fxml", paramsMap);
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

                    tempImageView.getStyleClass().add("clickable");
                    if(playerId == meId) tempImageView.setOnMousePressed(mouseEvent -> { flipCard(cardId); });
                }


                if (playerId == meId) {
                    Separator verticalSeparator1 = new Separator();
                    verticalSeparator1.setOrientation(Orientation.VERTICAL);
                    if (handContainer != null) handContainer.getChildren().add(verticalSeparator1);

                    Button btnPlaceCard = new Button("Place Card");
                    btnPlaceCard.setOnAction((mouseEvent) -> {
                        goToScene("/fxml/place.fxml");
                    });
                    if (handContainer != null) handContainer.getChildren().add(btnPlaceCard);
                }

                Separator verticalSeparator2 = new Separator();
                verticalSeparator2.setOrientation(Orientation.VERTICAL);
                if (handContainer != null) handContainer.getChildren().add(verticalSeparator2);
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

    public void handleDrawCard(DrawType drawType) {
        DrawCardCommandRunnable command = new DrawCardCommandRunnable();
        command.setDrawType(drawType);
        addCommand(command,this);
    }

    @Override
    public void update() {
        populatePlateauCoordinateMap(); // only for TEST
        Platform.runLater(() -> {
            clearAllchilds();

            // delete everything  or find a way to differentially change the content of the view
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

            initTable(playerHandCards,
                    nicknames,
                    getViewModel().getPlayerIndex(),
                    getViewModel().getSharedCards()[0],
                    getViewModel().getSharedCards()[1],
                    getViewModel().getSharedGoldCards()[0],
                    getViewModel().getSharedResourceCards()[0],
                    getViewModel().getSharedGoldCards()[1],
                    getViewModel().getSharedResourceCards()[1],
                    getViewModel().getObjectives()[0],
                    getViewModel().getObjectives()[1],
                    getViewModel().getObjectives()[2]);

            highlightCurrentPlayerTable(getViewModel().getCurrentPlayer(), getViewModel().getColorsMap(getViewModel().getCurrentPlayer()));
            updatePoints();
            setCurrentPhase();
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
