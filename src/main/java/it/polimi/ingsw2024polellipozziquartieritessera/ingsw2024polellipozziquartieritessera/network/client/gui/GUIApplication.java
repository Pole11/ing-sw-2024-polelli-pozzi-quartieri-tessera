package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.Client;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.ViewModel;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.controllers.GUIController;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.VirtualServer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class GUIApplication extends Application {
    private static GUIController guiController;
    private static ViewModel viewModel;
    private static Stage mainStage;
    private static VirtualView client;
    private static VirtualServer server;
    private static Client clientContainer;
    private static MediaPlayer mediaPlayer;

    public static MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public GUIController getGUIController() {
        return guiController;
    }

    public  Stage getMainStage() {
        return mainStage;
    }

    /*public boolean sceneLoaded() {
        return mainStage.getScene() != null;
    }*/

    public void updateController() {
        Platform.runLater(() -> { getGUIController().update(); });
    }

     public static void changeScene(String fxml) {
         Platform.runLater(() -> {
            FXMLLoader fxmlLoader = new FXMLLoader(GUIApplication.class.getResource(fxml));
             try {
                 mainStage.getScene().setRoot(fxmlLoader.load());
             } catch (IOException e) {
                 throw new RuntimeException(e);
             }
             guiController = fxmlLoader.getController();
             guiController.setClient(client);
             guiController.setServer(server);
             guiController.setViewModel(viewModel);
             guiController.setMediaPlayer(mediaPlayer);
         });
    }

    public static void changeScene(String fxml, HashMap<String, Integer> paramsMap) { // this was used when viewModel was not static
        Platform.runLater(() -> {
            FXMLLoader fxmlLoader = new FXMLLoader(GUIApplication.class.getResource(fxml));
            try {
                mainStage.getScene().setRoot(fxmlLoader.load());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            guiController = fxmlLoader.getController();
            guiController.setParamsMap(paramsMap);
            guiController.setClient(client);
            guiController.setServer(server);
            guiController.setClientContainer(clientContainer);
            guiController.setViewModel(viewModel);
            guiController.setMediaPlayer(mediaPlayer);
        });
    }

    @Override
    public void start(Stage stage) throws IOException {
        //FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/lobby.fxml")); // uncomment for real use
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/game.fxml")); // uncomment for testing
        Parent root = fxmlLoader.load();

        String audioFilePath = "/sounds/soundtrack.mp3";
        Media sound = new Media(Objects.requireNonNull(getClass().getResource(audioFilePath)).toExternalForm());
        mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();

        guiController = fxmlLoader.getController();
        guiController.setClient(client);
        guiController.setServer(server);
        guiController.setClientContainer(clientContainer);
        guiController.setViewModel(viewModel);
        guiController.setMediaPlayer(mediaPlayer);

        mainStage = stage;

        Scene scene = new Scene(root, 920, 920);
        stage.setTitle("Codex Naturalis");
        stage.setScene(scene);
        stage.setResizable(true); // Ensure the stage is resizable

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        // Calculate the desired window size based on screen resolution
        double width = screenBounds.getWidth() * 0.89;  // 80% of screen width
        double height = screenBounds.getHeight() * 0.89;  // 80% of screen height
        // Apply scaling to the root layout
        double scaleFactor = Math.min(width / 920, height / 920); // assuming 800x600 is the original size
        root.setScaleX(scaleFactor);
        root.setScaleY(scaleFactor);

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });

        stage.show();
    }

    public void runGui(VirtualView client, VirtualServer server, Client clientContainer, ViewModel viewModel) {
        GUIApplication.client = client;
        GUIApplication.server = server;
        GUIApplication.clientContainer = clientContainer;
        GUIApplication.viewModel = viewModel;
        launch(); // create the UI thread
    }
}
