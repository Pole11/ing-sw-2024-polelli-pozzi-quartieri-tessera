package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.controllers;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.Config;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.RingBuffer;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.Client;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.ViewModel;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable.CommandRunnable;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable.PingCommandRunnable;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.GUIApplication;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.VirtualServer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

abstract public class GUIController {
    @FXML private Label serverMessageLabel;
    @FXML private Label serverErrorLabel;
    @FXML private Button handleMuteButton;
    // add virtual model
    private final ArrayDeque<CommandRunnable> commandQueue;
    private Thread executeCommands;
    private VirtualView client; // temp
    private VirtualServer server; // temp
    private Client clientContainer;
    private ViewModel viewModel;
    private boolean isSceneLoaded = false;
    private HashMap<String, Integer> paramsMap;
    private double pressedX;
    private double pressedY;
    private MediaPlayer mediaPlayer;
    private int windowHeight = 920;
    RingBuffer<Image> imageRingBuffer;

    @FXML
    private void initialize() {
        isSceneLoaded = true;
        System.out.println("[DEBUG] Scene is loaded with " + this.getClass());
        Platform.runLater(() -> {
            handleMuteButton.setText(getMediaPlayer().isMute() ? "Unmute" : "Mute");

            handleMuteButton.getScene().setOnKeyPressed(event -> {
                if (event.getCode().equals(KeyCode.M)) muteMusic();
            });

            //windowHeight = GUIApplication.getSize();
            windowHeight = (int) GUIApplication.getMainStage().getHeight();
        });
    }

    public GUIController() {
        this.commandQueue = new ArrayDeque();
        this.executeCommands = new Thread(this::executeCommandsRunnable);
        this.executeCommands.start();
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public void setWindowHeight(int windowHeight) {
        this.windowHeight = windowHeight;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    @FXML
    public void handleMuteMusic(ActionEvent event) {
        Platform.runLater(() -> {
            muteMusic();
        });
    }

    public void muteMusic() {
        getMediaPlayer().setMute(!getMediaPlayer().isMute());
        handleMuteButton.setText(getMediaPlayer().isMute() ? "Unmute" : "Mute");
    }

    private void executeCommandsRunnable() {
        while (true) {
            CommandRunnable command;
            synchronized (commandQueue) {
                while (commandQueue.isEmpty()) {
                    try {
                        commandQueue.wait();
                    } catch (InterruptedException e) {
                    }
                }
                command = commandQueue.remove();
                commandQueue.notifyAll();
            }
            command.executeGUI();
        }
    }

    public void addCommand(CommandRunnable command, GUIController guiController) {
        command.setClient(getClient());
        command.setServer(getServer());
        command.setClientContainer(getClientContainer());
        command.setGuiController(guiController);
        ArrayDeque<CommandRunnable> commandQueue = getCommandQueue();
        synchronized (commandQueue) {
            commandQueue.addLast(command);
            commandQueue.notifyAll();
        }
    }

    public void setFontSize(Node root) {
        Platform.runLater(() -> {
            if (root != null) root.setStyle("-fx-font-size: " + getWindowHeight()*0.017 + "px");
        });
    }

    public ArrayDeque<CommandRunnable> getCommandQueue() {
        return commandQueue;
    }

    public boolean isSceneLoaded() {
        return isSceneLoaded;
    }

    abstract public void update();

    public ViewModel getViewModel() {
        return viewModel;
    }

    public void setViewModel(ViewModel newViewModel) {
        viewModel = newViewModel;
    }

    public VirtualView getClient() {
        return client;
    }

    public void setClient(VirtualView client) {
        this.client = client;
    }

    public VirtualServer getServer() {
        return server;
    }

    public void setServer(VirtualServer server) {
        this.server = server;
    }

    public Client getClientContainer() {
        return clientContainer;
    }

    public void ping(VirtualView client, VirtualServer server){
        synchronized (commandQueue) {
            PingCommandRunnable commandRunnable = new PingCommandRunnable();
            commandRunnable.setClient(client);
            commandRunnable.setServer(server);
            commandQueue.add(commandRunnable);
            commandQueue.notifyAll();
        }
    }

    public void setClientContainer(Client clientContainer) {
        this.clientContainer = clientContainer;
    }

    public Label getServerMessageLabel() {
        return serverMessageLabel;
    }

    public Label getServerErrorLabel() {
        return serverErrorLabel;
    }

    @FXML
    public void setServerMessage(String serverMessage) {
        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                clearServerError();
                System.out.println("[DEBUG] Rendering server message: " + serverMessage);
                serverMessageLabel.setText(serverMessage);
            }
        });
    }

    @FXML
    public void setServerError(String serverMessage) {
        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                clearServerMessage();
                System.err.println("[DEBUG] Rendering server error: " + serverMessage);
                serverErrorLabel.setText(serverMessage);
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

    public void goToScene(String fxml) {
        GUIApplication.changeScene(fxml);
    }

    public void goToScene(String fxml, HashMap<String, Integer> paramsMap) {
        GUIApplication.changeScene(fxml, paramsMap);
    }

    public HashMap<String, Integer> getParamsMap() {
        return paramsMap;
    }

    public void setParamsMap(HashMap<String, Integer> paramsMap) {
        this.paramsMap = paramsMap;
    }

    public ImageView createCardImageView(String url, int height) {
        URL resource = GUIController.class.getResource(url);
        if (resource == null) return null;
        String imageUrl = resource.toExternalForm();
        Image image = new Image(imageUrl);

        PixelReader reader = image.getPixelReader();
        //WritableImage imageWritable = new WritableImage(reader, 103, 101, 823, 547);
        WritableImage imageWritable = new WritableImage(reader, 52, 51, 411, 273);

        ImageView imageView = new ImageView(imageWritable);
        imageView.setFitHeight(height);
        imageView.getStyleClass().add("imageWithBorder");

        imageView.setPreserveRatio(true);

        return imageView;
    }

    public void addHoverRotate(Node node) {
        Platform.runLater(() -> {
            node.setOnMouseEntered(mouseEvent -> { node.getStyleClass().add("rotateYes"); node.getStyleClass().remove("rotateNo"); });
            node.setOnMouseExited(mouseEvent -> { node.getStyleClass().add("rotateNo"); node.getStyleClass().remove("rotateYes"); });
        });
    }

    public void addHoverBgColor(Node node) {
        Platform.runLater(() -> {
            node.setOnMouseEntered(mouseEvent -> { node.getStyleClass().add("backgoundHover"); node.getStyleClass().remove("backgoundNoHover"); });
            node.setOnMouseExited(mouseEvent -> { node.getStyleClass().add("backgoundNoHover"); node.getStyleClass().remove("backgoundHover"); });
        });
    }

    public void addHoverBgImage(Node node, String imageUrl) { // non funziona
        Platform.runLater(() -> {
            node.setOnMouseEntered(mouseEvent -> { node.setStyle("-fx-background-image: url('" + imageUrl + "')"); node.setStyle("-fx-background-image: none"); System.out.println("-fx-background-image: url('" + imageUrl + "')"); });
            node.setOnMouseExited(mouseEvent -> { node.setStyle("-fx-background-image: none"); node.setStyle("-fx-background-image: url('" + imageUrl + "')"); });
        });
    }

    public ArrayList<ArrayList<Integer>> rotateBoard(ArrayList<ArrayList<Integer>> board) {
        if (board == null) return null;
        ArrayList<ArrayList<Integer>> rotatedBoard = new ArrayList<>();
        ArrayList<String> cardList  = new ArrayList<>();

        for(int i = 0; i < 7; i++){
            cardList.add("");
        }
        //initialize rotatedBoard
        int a = board.size();
        int b = board.get(0).size();
        int c = a+b;

        for(int i = 0; i < c; i++){
            rotatedBoard.add(new ArrayList<>());
            for(int j  = 0; j < c; j++){
                rotatedBoard.get(i).add(0);
            }
        }
        //rotate board of 45°
        int centeri = (int)Math.floor((double) board.size()/2);
        int centerj = (int)Math.floor((double) board.get(0).size()/2);
        int rcenteri = (int)Math.floor((double) rotatedBoard.size()/2);
        int rcenterj = (int)Math.floor((double) rotatedBoard.get(0).size()/2);

        for(int i = 0; i < board.size(); i++){
            for(int j = 0; j < board.get(i).size(); j++){
                int irel = i - centeri;
                int jrel = j - centerj;
                rotatedBoard.get(rcenteri - centeri + i - jrel).set(rcenterj -centerj + j + irel, board.get(i).get(j));
            }
        }

        return new ArrayList<>(rotatedBoard);
    }

    public void resizeI(ArrayList<ArrayList<Integer>> matrix){
        if (matrix == null) return;
        //remove empty rows
        for(int j = 0; j < matrix.size(); j++){
            Boolean isEmpty = true;
            for(int i = 0; i < matrix.get(j).size() && isEmpty; i++){
                if(!matrix.get(j).get(i).equals(0)){
                    isEmpty = false;
                }
            }
            if(isEmpty){
                matrix.remove(j);
                j--;
            }
        }
        //remove empty columns
        ArrayList<Boolean> areEmpty = new ArrayList<>();
        for(int i = 0; i < matrix.get(0).size(); i++){
            areEmpty.add(true);
        }

        for(int i = 0; i < matrix.size(); i++){
            for (int j = 0; j < matrix.get(i).size(); j++){
                if(!matrix.get(i).get(j).equals(0)){
                    areEmpty.set(j, false);
                }
            }
        }
        for(int j = 0; j < areEmpty.size(); j++){
            if(areEmpty.get(j).equals(true)){
                for(int i = 0; i < matrix.size(); i++){
                    matrix.get(i).remove(j);
                }
                areEmpty.remove(j--);
            }
        }
    }

    public static WritableImage resizeWritableImage(WritableImage originalImage, double newWidth, double newHeight) {
        // Create a Canvas to draw the resized image
        Canvas canvas = new Canvas(newWidth, newHeight);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Draw the original image onto the canvas, scaling it to fit the new dimensions
        gc.drawImage(originalImage, 0, 0, newWidth, newHeight);

        // Create a new WritableImage and transfer the canvas content to it
        WritableImage resizedImage = new WritableImage((int) newWidth, (int) newHeight);
        PixelWriter pw = resizedImage.getPixelWriter();
        canvas.snapshot(null, resizedImage);

        return resizedImage;
    }

    public void addPanning(Node node) {
        node.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                if (event.isControlDown()) {
                    pressedX = event.getX();
                    pressedY = event.getY();
                }
            }
        });

        node.setOnMouseDragged(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                if (event.isControlDown()) {
                    node.setTranslateX(node.getTranslateX() + event.getX() - pressedX);
                    node.setTranslateY(node.getTranslateY() + event.getY() - pressedY);

                    event.consume();
                }
            }
        });
    }

    public Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }

    public void disappearAfter(int time, Node target, Pane father) {
        // Set up a Timeline to remove the Label after 10 seconds
        Platform.runLater(() -> {
            Timeline timeline = new Timeline(new KeyFrame(
                    Duration.seconds(10),
                    event -> father.getChildren().remove(target)
            ));
            timeline.setCycleCount(1); // Run only once
            timeline.play();
        });
    }

    public void deleteContainingNodes(Pane root) {
        Platform.runLater(() -> {
            List<Node> nodesToRemove = new ArrayList<>();

            // Iterate through children of the root pane
            for (javafx.scene.Node node : root.getChildren()) {
                nodesToRemove.add(node);
            }

            // Remove the collected nodes from the root pane
            root.getChildren().removeAll(nodesToRemove);
        });
    }

    // Method to show a simple alert
    public void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        // Load the custom CSS
        String css = getClass().getResource("/style/alert.css").toExternalForm();
        alert.getDialogPane().getStylesheets().add(css);

        // Apply a custom style class to the alert
        alert.getDialogPane().getStyleClass().add("myAlert");

        alert.showAndWait();
    }

}
