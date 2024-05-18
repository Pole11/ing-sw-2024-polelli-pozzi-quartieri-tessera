package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.Client;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.VirtualServer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.io.IOException;

public class GUITest extends Application {
    private static GUIController guiController;
    private static Stage mainStage;

    public static void changeScene(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GUIApplication.class.getResource(fxml));
        fxmlLoader.setController(guiController);
        mainStage.getScene().setRoot(fxmlLoader.load());
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GUIApplication.class.getResource("/fxml/game.fxml"));

        VirtualServer server = null;
        VirtualView client = null;
        guiController = new GUIController(client, server);

        fxmlLoader.setController(guiController);
        mainStage = stage;


        //guiController.setStarterCardImage(84);
        //guiController.setObjectiveCardImages(90, 91);

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

        guiController.initTable(4, 61, 11, 42, 27, 63, 3);
        guiController.highlightCurrentPlayerTable();
    }

    public void runGui(GUIController guiController) {
        this.guiController = guiController;
        launch(); // create the UI thread
    }
}
