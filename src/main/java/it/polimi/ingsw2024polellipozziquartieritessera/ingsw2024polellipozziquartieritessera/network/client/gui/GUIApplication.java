package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.Client;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.ViewModel;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.controllers.GUIController;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.VirtualServer;
import javafx.application.*;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.*;

import java.io.*;
import java.util.HashMap;

public class GUIApplication extends Application {
    private static GUIController guiController;
    private static ViewModel viewModel;
    private static Stage mainStage;
    private static VirtualView client;
    private static VirtualServer server;
    private static Client clientContainer;

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
        });
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/game.fxml"));
        Parent root = fxmlLoader.load();

        guiController = fxmlLoader.getController();
        guiController.setClient(client);
        guiController.setServer(server);
        guiController.setClientContainer(clientContainer);
        guiController.setViewModel(viewModel);
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

    public void runGui(VirtualView client, VirtualServer server, Client clientContainer, ViewModel viewModel) {
        GUIApplication.client = client;
        GUIApplication.server = server;
        GUIApplication.clientContainer = clientContainer;
        GUIApplication.viewModel = viewModel;
        launch(); // create the UI thread
    }
}
