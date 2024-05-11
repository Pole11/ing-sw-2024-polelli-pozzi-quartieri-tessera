package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.VirtualServer;
import javafx.application.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.*;

import java.io.*;
import java.util.Objects;

public class GUIApplication extends Application {
    private static VirtualServer server;
    private static VirtualView client;
    private static GUIController guiController;
    private static Stage mainStage;

    public void setServer(VirtualServer server) {
        this.server = server;
    }

    public void setClient(VirtualView client) {
        this.client = client;
    }

    public GUIController getGUIController() {
        return this.guiController;
    }

    public static void changeScene(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GUIApplication.class.getResource(fxml));
        fxmlLoader.setController(guiController);
        mainStage.getScene().setRoot(fxmlLoader.load());
    }

    @Override
    public void start(Stage stage) throws IOException {
        //FXMLLoader fxmlLoader = new FXMLLoader(GUIApplication.class.getResource("/it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui/lobby.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(GUIApplication.class.getResource("/lobby.fxml"));

        fxmlLoader.setController(guiController);
        mainStage = stage;

        Scene scene = new Scene(fxmlLoader.load(), 720, 240);
        stage.setTitle("Codex Naturalis");
        stage.setScene(scene);
        stage.show();
    }

    public void runGui(GUIController guiController) {
        //guiController = new GUIController(client, server);
        this.guiController = guiController;
        launch(); // create the UI thread
    }
}
