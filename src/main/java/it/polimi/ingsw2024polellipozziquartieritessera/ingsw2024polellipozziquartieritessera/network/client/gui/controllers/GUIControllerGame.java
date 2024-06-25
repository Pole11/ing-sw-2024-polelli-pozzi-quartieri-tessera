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
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.*;

public class GUIControllerGame extends GUIController {
    @FXML private BorderPane mainContainerGame;
    @FXML private VBox sharedGoldContainerGame;
    @FXML private VBox plateauContainerGame;
    @FXML private VBox sharedResourceContainerGame;
    @FXML private Label currentPhase;
    @FXML private StackPane plateauImageViewPane;
    @FXML private Button handleOpenGameRulesButton;

    private HashMap<Integer, ArrayList<Integer>> plateauCoordinatedMap;
    private static ImageView plateauImageView;
    private static Image plateauImage;

    public GUIControllerGame() {

        rotatePlayerContainer();
        setFontSize(mainContainerGame);
        this.update();
    }

    private void populatePlateauCoordinateMap() {
        plateauCoordinatedMap = new HashMap<>();
        int plateauHeight = (int) (getWindowHeight() * 0.38);
        double[] xOffsetFactors = { -0.11, 0, 0.11, 0.17, 0.057, -0.057, -0.17, -0.17,
                -0.057, 0.057, 0.17, 0.17, 0.057, -0.057, -0.17, -0.17,
                -0.057, 0.057, 0.17, 0.17, 0, -0.17, -0.17, -0.17,
                -0.1, 0, 0.1, 0.17, 0.17, 0 };

        double[] yOffsetFactors = { +0.43, +0.43, +0.43, +0.33, +0.33, +0.33, +0.33, +0.22,
                +0.22, +0.22, +0.22, +0.12, +0.12, +0.12, +0.12, +0.02,
                +0.02, +0.02, +0.02, -0.09, -0.15, -0.11, -0.20, -0.31,
                -0.43, -0.45, -0.43, -0.31, -0.24, -0.33 };

        // Populate the map with coordinates
        for (int i = 0; i < xOffsetFactors.length; i++) {
            int offsetX = (int) (plateauHeight * xOffsetFactors[i]);
            int offsetY = (int) (plateauHeight * yOffsetFactors[i]);
            plateauCoordinatedMap.put(i, new ArrayList<>(List.of(offsetX, offsetY)));
        }
    }

    private void clearAllchilds() {
        Platform.runLater(() -> {
            if (sharedResourceContainerGame != null) sharedResourceContainerGame.getChildren().clear();
            if (sharedGoldContainerGame != null) sharedGoldContainerGame.getChildren().clear();
            /*if (plateauContainerGame != null) {
                for (int i = 0; i < plateauContainerGame.getChildren().size(); i++) {
                    // define the elements that must not be deleted
                    ArrayList<Node> doNotDeleteElements = new ArrayList<>(Arrays.asList(serverMessageLabel,
                            serverErrorLabel,
                            handleMuteButton,
                            handleOpenGameRulesButton,
                            handleOpenChatButton,
                            currentPhase,
                            plateauImageViewPane));
                    if (!doNotDeleteElements.contains(plateauContainerGame.getChildren().get(i))) plateauContainerGame.getChildren().remove(i); // the first two are the messages from server
                }
            }*/
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
        plateauImage = new Image(imageUrl);

        // crop the image
        PixelReader reader = plateauImage.getPixelReader();
        WritableImage imageWritable = new WritableImage(reader, 48, 49, 460, 926);

        plateauImageView = new ImageView(imageWritable);

        plateauImageView.setFitHeight(plateauHeight); // just for info,the width is half thee height
        plateauImageView.setPreserveRatio(true);

        return plateauImageView;
    }

    public void initTable() {
        // prepare the data structure for the hand
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

        // print shared objectives
        int firstCommonObjective = getViewModel().getObjectives()[0];
        int secondCommonObjective = getViewModel().getObjectives()[1];
        printCommonObjective(firstCommonObjective, secondCommonObjective);

        // print decks
        printDecks();
        int secretObjectiveCardId = getViewModel().getObjectives()[2];

        // print the hands
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
        Platform.runLater(() -> {
            addTextToContainer(sharedGoldContainerGame, "Gold Deck");
            try {
                addCardToContainer(
                        sharedGoldContainerGame,
                        "/img/carte_retro/" + getViewModel().getSharedCards()[5] + ".jpg",
                        DrawType.DECKGOLD,
                        true
                );
                addTextToContainer(sharedGoldContainerGame, "Shared Gold");
            } catch (Exception e) {
                System.out.println("The gold deck is empty");
            }

            try {
                addCardToContainer(
                        sharedGoldContainerGame,
                        "/img/carte_fronte/" + getViewModel().getSharedGoldCards()[0] + ".jpg",
                        DrawType.SHAREDGOLD1,
                        false
                );
            } catch (Exception e) {
                System.out.println("The first gold shared is empty");
            }

            try {
                addCardToContainer(
                        sharedGoldContainerGame,
                        "/img/carte_fronte/" + getViewModel().getSharedGoldCards()[1] + ".jpg",
                        DrawType.SHAREDGOLD2,
                        false
                );
            } catch (Exception e) {
                System.out.println("The second gold shared is empty");
            }

            configurePlateau();

            addTextToContainer(sharedResourceContainerGame, "Resource Deck");
            try {
                addCardToContainer(
                        sharedResourceContainerGame,
                        "/img/carte_retro/" + getViewModel().getSharedCards()[4] + ".jpg",
                        DrawType.DECKRESOURCE,
                        true
                );
                addTextToContainer(sharedResourceContainerGame, "Shared Resource");
            } catch (Exception e) {
                System.out.println("The resource deck is empty");
            }

            try {
                addCardToContainer(
                        sharedResourceContainerGame,
                        "/img/carte_fronte/" + getViewModel().getSharedResourceCards()[0] + ".jpg",
                        DrawType.SHAREDRESOURCE1,
                        false
                );
            } catch (Exception e) {
                System.out.println("The first resource shared is empty");
            }

            try {
                addCardToContainer(
                        sharedResourceContainerGame,
                        "/img/carte_fronte/" + getViewModel().getSharedResourceCards()[1] + ".jpg",
                        DrawType.SHAREDRESOURCE2,
                        false
                );
            } catch (Exception e) {
                System.out.println("The second resource shared is empty");
            }
        });
    }

    private void addTextToContainer(Pane container, String text) {
        container.getChildren().add(new Text(text));
    }

    private void addCardToContainer(Pane container, String imagePath, DrawType drawType, boolean addBorder) throws Exception {
        ImageView imageView = createCardImageView(imagePath, (int) (getWindowHeight() * 0.090));
        imageView.getStyleClass().add("clickable");
        BorderPane cardPane = new BorderPane(imageView);
        if (addBorder) cardPane.getStyleClass().add("cardDeck");
        cardPane.setOnMouseClicked(mouseEvent -> handleDrawCard(drawType));
        addHoverRotate(cardPane);
        container.getChildren().add(cardPane);
    }

    private void configurePlateau() {
        int plateauHeight = (int) (getWindowHeight() * 0.38);
        plateauImageViewPane.setAlignment(Pos.CENTER);
        plateauImageViewPane.setPrefHeight(plateauHeight);
        plateauImageViewPane.setPrefWidth(plateauHeight / 2);
        ImageView plateauImageView = createPlateauImageView(plateauHeight);
        plateauImageViewPane.getChildren().add(plateauImageView);
        //plateauContainerGame.getChildren().add(plateauImageViewPane);
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
                Circle circle = new Circle(0, 0, getWindowHeight()*0.01);
                circle.setId("circlePoints" + i);
                plateauImageViewPane.getChildren().add(circle);

                int offset = 5;
                if (getViewModel().getColorsMap(i).equals(Color.BLUE)) { circle.setFill(javafx.scene.paint.Color.BLUE); circle.setTranslateX(x - offset); circle.setTranslateY(y - offset);}
                else if (getViewModel().getColorsMap(i).equals(Color.GREEN)) { circle.setFill(javafx.scene.paint.Color.GREEN); circle.setTranslateX(x + offset); circle.setTranslateY(y - offset); }
                else if (getViewModel().getColorsMap(i).equals(Color.RED)) { circle.setFill(javafx.scene.paint.Color.RED); circle.setTranslateX(x + offset); circle.setTranslateY(y + offset); }
                else if (getViewModel().getColorsMap(i).equals(Color.YELLOW)) { circle.setFill(javafx.scene.paint.Color.YELLOW); circle.setTranslateX(x -offset); circle.setTranslateY(y + offset);}
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
                String currentPlayerDisplayName = (playerId == 0 ? "⚫ " : "") + currentPlayerNickname + " (" + getViewModel().getColorsMap(playerId) + " " + getViewModel().getPointsMap(playerId) + ")";
                Text nicknameText = new Text(currentPlayerDisplayName);
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
                    HashMap<String, Object> paramsMap = new HashMap<>();
                    paramsMap.put("playerId", playerId);
                    System.out.println(playerId);
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
                    leftContainer.setId("player1ContainerGame"); leftContainerHand.setId("player1HandContainerGame");
                    topContainer.setId("player2ContainerGame"); topContainerHand.setId("player2HandContainerGame");
                    rightContainer.setId("player3ContainerGame"); rightContainerHand.setId("player3HandContainerGame");
                }
                case(1) -> { bottomContainer.setId("player1ContainerGame"); bottomContainerHand.setId("player1HandContainerGame");
                    leftContainer.setId("player2ContainerGame"); leftContainerHand.setId("player2HandContainerGame");
                    topContainer.setId("player3ContainerGame"); topContainerHand.setId("player3HandContainerGame");
                    rightContainer.setId("player0ContainerGame"); rightContainerHand.setId("player0HandContainerGame");
                }
                case(2) -> { bottomContainer.setId("player2ContainerGame"); bottomContainerHand.setId("player2HandContainerGame");
                    leftContainer.setId("player3ContainerGame"); leftContainerHand.setId("player3HandContainerGame");
                    topContainer.setId("player0ContainerGame"); topContainerHand.setId("player0HandContainerGame");
                    rightContainer.setId("player1ContainerGame"); rightContainerHand.setId("player1HandContainerGame");
                }
                case(3) -> { bottomContainer.setId("player3ContainerGame"); bottomContainerHand.setId("player3HandContainerGame");
                    leftContainer.setId("player0ContainerGame"); leftContainerHand.setId("player0HandContainerGame");
                    topContainer.setId("player1ContainerGame"); topContainerHand.setId("player1HandContainerGame");
                    rightContainer.setId("player2ContainerGame"); rightContainerHand.setId("player2HandContainerGame");
                }
            }
        });
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
        System.gc();
        Platform.runLater(() -> {
            populatePlateauCoordinateMap();
            clearAllchilds();
            initTable();
            highlightCurrentPlayerTable();
            //setCurrentPhase();
            setNewMessageChat();
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
