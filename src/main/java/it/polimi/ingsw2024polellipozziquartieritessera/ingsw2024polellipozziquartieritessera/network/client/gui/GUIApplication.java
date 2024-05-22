package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.controllers.GUIController;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.controllers.GUIControllerGame;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.VirtualServer;
import javafx.application.*;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.*;

import java.io.*;

public class GUIApplication extends Application {
    private static GUIController guiController;
    private static Stage mainStage;
    private static VirtualView client;
    private static VirtualServer server;

    public static GUIController getGUIController() {
        return guiController;
    }

    public static Stage getMainStage() {
        return mainStage;
    }

    public static void changeScene(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GUIApplication.class.getResource(fxml));
        mainStage.getScene().setRoot(fxmlLoader.load());
        guiController = fxmlLoader.getController();
        guiController.setClient(client);
        guiController.setServer(server);
    }

    public static void changeScene(String fxml, String[] args) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GUIApplication.class.getResource(fxml));
        mainStage.getScene().setRoot(fxmlLoader.load());
        guiController = fxmlLoader.getController();
        guiController.setArgs(args);
        guiController.setClient(client);
        guiController.setServer(server);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/game.fxml"));
        Parent root = fxmlLoader.load();

        guiController = fxmlLoader.getController();
        guiController.setClient(client);
        guiController.setServer(server);
        mainStage = stage;

        Scene scene = new Scene(root, 920, 920);
        stage.setTitle("Codex Naturalis");
        stage.setScene(scene);

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });

        stage.show();
    }

    //public void runGui(GUIControllerOld guiControllerOld) {
    public void runGui(VirtualView client, VirtualServer server) {
        this.client = client;
        this.server = server;
        launch(); // create the UI thread
    }
}
