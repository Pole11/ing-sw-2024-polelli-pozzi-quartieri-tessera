package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;
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

    public static GUIController getGUIController() {
        return guiController;
    }

    public static Stage getMainStage() {
        return mainStage;
    }

    public static void changeScene(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GUIApplication.class.getResource(fxml));
        fxmlLoader.setController(guiController);
        mainStage.getScene().setRoot(fxmlLoader.load());
    }

    @Override
    public void start(Stage stage) throws IOException {
        //FXMLLoader fxmlLoader = new FXMLLoader(GUIApplication.class.getResource("/it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui/lobby.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(GUIApplication.class.getResource("/fxml/lobby.fxml"));

        fxmlLoader.setController(guiController);
        mainStage = stage;

        Scene scene = new Scene(fxmlLoader.load(), 920, 920);
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

    public void runGui(GUIController guiController) {
        //guiController = new GUIController(client, server);
        this.guiController = guiController;
        launch(); // create the UI thread
    }
}
