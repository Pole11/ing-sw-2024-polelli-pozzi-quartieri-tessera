package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Color;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Command;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.DrawType;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Board;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.Client;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.VirtualServer;
import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.event.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.io.IOException;
import java.rmi.RemoteException;

public class GUIController {
    @FXML private VBox mainContainer;
    @FXML private VBox sharedGoldContainerGame;
    @FXML private VBox plateauContainerGame;
    @FXML private VBox sharedResourceContainerGame;
    @FXML private StackPane starterCardImageContainerFront;
    @FXML private StackPane starterCardImageContainerBack;
    @FXML private StackPane firstObjectiveImageContainer;
    @FXML private StackPane secondObjectiveImageContainer;
    @FXML private Label serverMessageLabel;
    @FXML private Label serverErrorLabel;
    @FXML private TextField nicknameTextField;

    private VirtualView client;
    private VirtualServer server;

    public GUIController(VirtualView client, VirtualServer server) {
        this.client = client;
        this.server = server;
    }

    @FXML
    public void handleAddUser(ActionEvent event) {
        String message = Command.ADDUSER + " " + nicknameTextField.getText();
        try {
            Client.manageInputCli(this.server, message.split(" "), this.client);
        } catch (RemoteException e) {
            e.printStackTrace(); // TODO: remove on prod
            serverMessageLabel.setText("There was an error while adding a user");
        }
    }

    @FXML
    public void handleStartGame(ActionEvent event) {
        String message = Command.START.toString();
        try {
            Client.manageInputCli(this.server, message.split(" "), this.client);
        } catch (RemoteException e) {
            e.printStackTrace(); // TODO: remove on prod
            serverMessageLabel.setText("There was an error while adding a user");
        }
        // check if the user has logged in
        // check if the game is startable
    }

    @FXML
    public void handleChooseFrontStarter(ActionEvent event) {
        handleChooseSideStarter(Side.FRONT);
    }

    @FXML
    public void handleChooseBackStarter(ActionEvent event) {
        handleChooseSideStarter(Side.BACK);
    }

    private void handleChooseSideStarter(Side side) {
        String message = Command.CHOOSESTARTER + " " + side;
        try {
            Client.manageInputCli(this.server, message.split(" "), this.client);
        } catch (RemoteException e) {
            e.printStackTrace(); // TODO: remove on prod
            serverMessageLabel.setText("There was an error while choosing the starter side");
        }
    }

    @FXML
    private void handleChooseBlueColor(ActionEvent event) {
        handleChooseColor(Color.BLUE);
    }

    @FXML
    private void handleChooseGreenColor(ActionEvent event) {
        handleChooseColor(Color.GREEN);
    }

    @FXML
    private void handleChooseYellowColor(ActionEvent event) {
        handleChooseColor(Color.YELLOW);
    }

    @FXML
    private void handleChooseRedColor(ActionEvent event) {
        handleChooseColor(Color.RED);
    }

    private void handleChooseColor(Color color) {
        changeMainContainerBorder(color); // TODO: move from here, put it in the client so that the color is changed only one single time

        String message = Command.CHOOSECOLOR + " " + color;
        try {
            Client.manageInputCli(this.server, message.split(" "), this.client);
        } catch (RemoteException e) {
            e.printStackTrace(); // TODO: remove on prod
            serverMessageLabel.setText("There was an error while selecting a color");
        }
    }

    private void changeMainContainerBorder(Color color) {
        for (Color colorIterator : Color.values()) { mainContainer.getStyleClass().remove(colorIterator.toString().toLowerCase() + "Border"); }
        mainContainer.getStyleClass().add(color.toString().toLowerCase() + "Border");
    }

    private void disableChooseColorBtns(Color color) { // TODO: call this method when the ack from the server that the color is correct, but it is not mandatory, !!! even without it works great !!!
        for (Color colorIterator : Color.values()) {
            Node button = mainContainer.lookup("#" + colorIterator.toString().toLowerCase() + "Button");
            if (button != null) ((Button) button).setOnAction(null);
        }
    }

    public void goToScene(String fxml) {
        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                try {
                    GUIApplication.changeScene(fxml);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @FXML
    public void handleChooseFirstObjective(ActionEvent event) {
        handleChooseObjective(0);
    }

    @FXML
    public void handleChooseSecondObjective(ActionEvent event) {
        handleChooseObjective(1);
    }

    public void handleChooseObjective(int index) {
        String message = Command.CHOOSEOBJECTIVE + " " + index;
        try {
            Client.manageInputCli(this.server, message.split(" "), this.client);
        } catch (RemoteException e) {
            e.printStackTrace(); // TODO: remove on prod
            serverMessageLabel.setText("There was an error selecting the secret objective card");
        }
    }

    private ImageView createCardImageView(String url, int width) {
        String imageUrl = getClass().getResource(url).toExternalForm();
        Image image = new Image(imageUrl);

        PixelReader reader = image.getPixelReader();
        WritableImage imageWritable = new WritableImage(reader, 103, 101, 823, 547);

        ImageView imageView = new ImageView(imageWritable);

        imageView.setFitHeight(width);
        imageView.setPreserveRatio(true);

        return imageView;
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

    public void setStarterCardImage(int id) {
        ImageView starterCardImageViewFront = createCardImageView("/img/carte_fronte/" + id + ".jpg", 180);
        ImageView starterCardImageViewBack = createCardImageView("/img/carte_retro/" + id + ".jpg", 180);

        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                starterCardImageViewFront.setOnMouseClicked(mouseEvent -> {
                    // OPZIONE 1
                    if (!starterCardImageViewBack.getParent().getStyleClass().contains("greenBackground"))
                        starterCardImageViewFront.getParent().getStyleClass().add("greenBackground");
                    // OPZIONE 2
                    //starterCardImageViewFront.setOnMouseClicked(null);
                    //starterCardImageViewBack.setOnMouseClicked(null);
                    handleChooseSideStarter(Side.FRONT);
                });
                starterCardImageViewBack.setOnMouseClicked(mouseEvent -> {
                    // OPZIONE 1
                    if (!starterCardImageViewFront.getParent().getStyleClass().contains("greenBackground"))
                        starterCardImageViewBack.getParent().getStyleClass().add("greenBackground");
                    // OPZIONE 2
                    //starterCardImageViewFront.setOnMouseClicked(null);
                    //starterCardImageViewBack.setOnMouseClicked(null);
                    handleChooseSideStarter(Side.BACK);

                });
                starterCardImageContainerFront.getChildren().add(starterCardImageViewFront);
                starterCardImageContainerBack.getChildren().add(starterCardImageViewBack);
            }
        });
    }

    public void setObjectiveCardImages(int id1, int id2) {
        ImageView firstObjectiveImageView = createCardImageView("/img/carte_fronte/" + id1 + ".jpg", 180);
        ImageView secondObjectiveImageView = createCardImageView("/img/carte_fronte/" + id2 + ".jpg", 180);

        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                firstObjectiveImageView.setOnMouseClicked(mouseEvent -> {
                    // OPZIONE 1
                    if (!secondObjectiveImageView.getParent().getStyleClass().contains("greenBackground"))
                        firstObjectiveImageView.getParent().getStyleClass().add("greenBackground");
                    // OPZIONE 2
                    //firstObjectiveImageView.setOnMouseClicked(null);
                    //secondObjectiveImageView.setOnMouseClicked(null);
                    handleChooseObjective(0);
                });
                secondObjectiveImageView.setOnMouseClicked(mouseEvent -> {
                    // OPZIONE 1
                    if (!firstObjectiveImageView.getParent().getStyleClass().contains("greenBackground"))
                        secondObjectiveImageView.getParent().getStyleClass().add("greenBackground");
                    // OPZIONE 2
                    //firstObjectiveImageView.setOnMouseClicked(null);
                    //secondObjectiveImageView.setOnMouseClicked(null);
                    handleChooseObjective(1);

                });
                firstObjectiveImageContainer.getChildren().add(firstObjectiveImageView);
                secondObjectiveImageContainer.getChildren().add(secondObjectiveImageView);
            }
        });
    }

    public void initTable(int numPlayers, int firstGoldDeckCardId, int firstResourceDeckCardId, int firstSharedGoldCardId, int firstSharedResourceCardId, int secondSharedGoldCardId, int secondSharedResourceCardId) {
        initDecks(firstGoldDeckCardId, firstResourceDeckCardId, firstSharedGoldCardId, firstSharedResourceCardId, secondSharedGoldCardId, secondSharedResourceCardId);
    }

    public void initDecks(int firstGoldDeckCardId, int firstResourceDeckCardId, int firstSharedGoldCardId, int firstSharedResourceCardId, int secondSharedGoldCardId, int secondSharedResourceCardId) {

        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                sharedGoldContainerGame.getChildren().add(new Text("Gold Deck"));
                ImageView goldDeckImageView = createCardImageView("/img/carte_retro/" + firstGoldDeckCardId + ".jpg", 75);
                BorderPane goldDeckPane = new BorderPane(goldDeckImageView);
                goldDeckPane.getStyleClass().add("cardDeck");
                // add event
                sharedGoldContainerGame.getChildren().add(goldDeckPane);
                sharedGoldContainerGame.getChildren().add(new Text("Shared Gold"));
                // add event
                ImageView firstSharedGoldCardImageView = createCardImageView("/img/carte_fronte/" + firstSharedGoldCardId + ".jpg", 75);
                sharedGoldContainerGame.getChildren().add(firstSharedGoldCardImageView);
                // add event
                ImageView secondSharedGoldCardImageView = createCardImageView("/img/carte_fronte/" + secondSharedGoldCardId + ".jpg", 75);
                sharedGoldContainerGame.getChildren().add(secondSharedGoldCardImageView);

                plateauContainerGame.getChildren().add(createPlateauImageView());

                sharedResourceContainerGame.getChildren().add(new Text("Resource Deck"));
                ImageView resourceDeckImageView = createCardImageView("/img/carte_retro/" + firstResourceDeckCardId + ".jpg", 75);
                BorderPane resourceDeckPane = new BorderPane(resourceDeckImageView);
                resourceDeckPane.getStyleClass().add("cardDeck");
                // add event
                sharedResourceContainerGame.getChildren().add(resourceDeckPane);

                sharedResourceContainerGame.getChildren().add(new Text("Shared Resource"));
                // add event
                ImageView firstSharedResourceCardImageView = createCardImageView("/img/carte_fronte/" + firstSharedResourceCardId + ".jpg", 75);
                sharedResourceContainerGame.getChildren().add(firstSharedResourceCardImageView);
                // add event
                ImageView secondSharedResourceCardImageView = createCardImageView("/img/carte_fronte/" + secondSharedResourceCardId + ".jpg", 75);
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

    public void addHover(Node node) {
        node.setOnMouseEntered(mouseEvent -> { node.getStyleClass().add("rotate10"); node.getStyleClass().remove("rotate0"); });
        node.setOnMouseExited(mouseEvent -> { node.getStyleClass().add("rotate0"); node.getStyleClass().remove("rotate10"); });
    }

    public void highlightCurrentPlayerTable(int idCurrentPlayer) {

    }

    public void printPlayerHand(int idCurrentPlayer, int idFirstCard, int idSecondCard, int idThirdCard) {

    }

    public void printGoldDeck(int topCard) {
        if (topCard <= 0) {

        }
    }

    public void printResourceDeck(int topCard) {
        if (topCard <= 0) {

        }
    }

    public void printBoard() {

    }

    @FXML
    public void handlePlaceCard(ActionEvent event) {
    }

    public void handleDrawCard(DrawType drawType) {
        try {
            server.drawCard(client, drawType);
        } catch (RemoteException e) {
            setServerError("There was an error while drawing the card, please try again");
        }
    }

    @FXML
    public void setServerMessage(String serverMessage) {
        clearServerError();
        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                System.out.println("[DEBUG] Rendering server message: " + serverMessage);
                serverMessageLabel.setText("INFO FROM SERVER: " + serverMessage);
            }
        });
    }
    @FXML
    public void setServerError(String serverMessage) {
        clearServerMessage();
        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                System.err.println("[DEBUG] Rendering server error: " + serverMessage);
                serverErrorLabel.setText("ERROR FROM SERVER: " + serverMessage);
            }
        });
    }

    public void clearServerError() {
                Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                serverErrorLabel.setText("");
            }
        });
    }
    public void clearServerMessage() {
                Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                serverMessageLabel.setText("");
            }
        });
    }
}
