package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.controllers;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.Config;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.PacmanBuffer;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Message;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.Client;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.ViewModel;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.GUIApplication;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.VirtualServer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;

import static java.lang.Thread.sleep;

abstract public class GUIController {
    @FXML public Label serverMessageLabel;
    @FXML public Label serverErrorLabel;
    @FXML public Button handleMuteButton;
    @FXML private Button handleOpenChatButton;
    private final ArrayDeque<CommandRunnable> commandQueue;
    private Thread executeCommands;
    private Thread pongThread;
    private VirtualView client; // temp
    private VirtualServer server; // temp
    private Client clientContainer;
    private ViewModel viewModel;
    private boolean isSceneLoaded = false;
    private HashMap<String, Object> paramsMap;
    private double pressedX;
    private double pressedY;
    private MediaPlayer mediaPlayer;
    private int windowHeight = 920;
    private ListView<Text> chatListView;
    private boolean chatOpen;
    private static boolean alertIsOpen = false;
    private Alert alert;
    //private static PacmanBuffer<ImageView> imageViewRingBuffer;

    private boolean executeCommandRunning;
    private boolean pongRunning;

    @FXML
    private void initialize() {
        isSceneLoaded = true;
        chatListView = new ListView<>();

        //System.out.println("[DEBUG] Scene is loaded with " + this.getClass());
        Platform.runLater(() -> {
            handleMuteButton.setText(getMediaPlayer().isMute() ? "Unmute" : "Mute");

            handleMuteButton.getScene().setOnKeyPressed(event -> {
                if (event.getCode().equals(KeyCode.M)) muteMusic();
            });

            //windowHeight = GUIApplication.getSize();
            windowHeight = (int) GUIApplication.getMainStage().getHeight();

            /*if (imageViewRingBuffer == null) {
                imageViewRingBuffer = new PacmanBuffer<>(Config.BUFFER_IMAGE_SIZE);
                for (int i = 0; i < Config.BUFFER_IMAGE_SIZE; i++) {
                    imageViewRingBuffer.add(new ImageView());
                }
            }*/

            //imageViewRingBuffer.printStatus();
        });
    }

    public GUIController() {
        this.commandQueue = new ArrayDeque();
        executeCommandRunning = true;
        pongRunning = true;
        restartExecuteCommand();

        restartPong();
    }
    
    public void restartPong(){
        pongThread = new Thread(()->{
            while (pongRunning) {
                if (server == null) continue;
                PongCommandRunnable command = new PongCommandRunnable();
                addCommand(command, this);
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    System.out.println("pong thread interrupted");
                }
            }
        });
        pongThread.start();
    }


    public void restartExecuteCommand(){
        System.out.println("restartExecuteCommand in guii");
        if (executeCommands != null && executeCommands.isAlive()){
            //executeCommandRunning = false;
            executeCommands.interrupt();

            /*if (commandQueue.isEmpty()){
                executeCommands.interrupt();
            } else {
                synchronized (commandQueue) {
                    executeCommandRunning = false;
                    commandQueue.notifyAll();
                }
            }

             */

            try {
                executeCommands.join();
            } catch (InterruptedException ignored) {}



        }
        commandQueue.clear();
        executeCommandRunning = true;
        this.executeCommands = new Thread(this::executeCommandsRunnable);
        this.executeCommands.start();
    }

    public void restart(VirtualView client, VirtualServer server){
        System.out.println("restarting gui");
        synchronized (commandQueue){
            commandQueue.clear();
            GameEndedCommandRunnable commandRunnable = new GameEndedCommandRunnable();
            commandRunnable.setClient(client);
            commandRunnable.setServer(server);
            commandQueue.add(commandRunnable);
            commandQueue.notifyAll();
        }
        restartExecuteCommand();
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
        while (executeCommandRunning) {
            CommandRunnable command = null;
            synchronized (commandQueue) {
                while (commandQueue.isEmpty()) {
                    try {
                        commandQueue.wait();
                    } catch (InterruptedException e) {
                        System.out.println("interrupted");
                        return;
                    }
                }

                command = commandQueue.remove();
                //commandQueue.notifyAll();
            }
            System.out.println(command);
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
                //System.out.println("[DEBUG] Rendering server message: " + serverMessage);
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

    public void goToScene(String fxml, HashMap<String, Object> paramsMap) {
        GUIApplication.changeScene(fxml, paramsMap);
    }

    public HashMap<String, Object> getParamsMap() {
        return paramsMap;
    }

    public void setParamsMap(HashMap<String, Object> paramsMap) {
        this.paramsMap = paramsMap;
    }

    public ImageView createCardImageView(String url, int height) {
        URL resource = GUIController.class.getResource(url);
        if (resource == null) return null;
        String imageUrl = resource.toExternalForm();
        Image image = new Image(imageUrl);

        /*ImageView imageView = imageViewRingBuffer.getNext();
        imageView.imageProperty().set(null);

        try {
            // TODO: togli tutti gli event handler
            imageView.setId("");
            imageView.setImage(image);
        } catch(Exception e) {
            System.out.println("Naggia");
            e.printStackTrace();
        }*/
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(height);
        imageView.getStyleClass().add("imageWithBorder");

        imageView.setPreserveRatio(true);

        return imageView;
    }

    private void removeAllEventHandlers(javafx.scene.Node node) throws NoSuchFieldException, IllegalAccessException {
        // Access the private field 'eventHandlerManager' using reflection
        Field field = null;
        try {
            field = Node.class.getDeclaredField("eventHandlerManager");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        field.setAccessible(true);

        Object eventHandlerManager = field.get(node);

        if (eventHandlerManager != null) {
            // Access the private field 'eventHandlers' inside 'EventHandlerManager'
            Field mapField = eventHandlerManager.getClass().getDeclaredField("eventHandlers");
            mapField.setAccessible(true);

            Map eventHandlers = null;
            try {
                eventHandlers = (Map) mapField.get(eventHandlerManager);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            // Clear the event handlers map
            if (eventHandlers != null) {
                eventHandlers.clear();
            }
        }
    }

    @FXML
    private void handleOpenChat(ActionEvent event) {
        Platform.runLater(() -> {
            if (chatOpen) return;
            chatOpen = true;

            Stage chatStage = new Stage();
            chatStage.setOnCloseRequest(closeEvent -> {
                chatOpen = false;
                handleOpenChatButton.getStyleClass().remove("pendingChatButton");
                handleOpenChatButton.setText("Open Chat");
                getViewModel().resetNewMessages();
            });
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

    public void populateChatListview() {
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

    public void setNewMessageChat() {
        if (getViewModel().getNewMessages() > 0) {
            handleOpenChatButton.getStyleClass().add("pendingChatButton");
            handleOpenChatButton.setText("Open Chat (" + getViewModel().getNewMessages() + ")");
        }
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
            node.setOnMouseEntered(mouseEvent -> { node.setStyle("-fx-background-image: url('" + imageUrl + "')"); node.setStyle("-fx-background-image: none"); });
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
        Platform.runLater(() -> {
            Alert alert = showAlertHelper(alertType, title, content);
            alert.showAndWait();
        });
    }

    public void showSingleAlert(Alert.AlertType alertType, String title, String content) {
        Platform.runLater(() -> {
            if (alert == null || !alert.isShowing()) {
                alert = showAlertHelper(alertType, title, content);
                alert.showAndWait();
            }
        });
    }

    private Alert showAlertHelper(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        // Load the custom CSS
        String css = getClass().getResource("/style/alert.css").toExternalForm();
        alert.getDialogPane().getStylesheets().add(css);

        // Apply a custom style class to the alert
        alert.getDialogPane().getStyleClass().add("myAlert");
        return alert;
    }

}
