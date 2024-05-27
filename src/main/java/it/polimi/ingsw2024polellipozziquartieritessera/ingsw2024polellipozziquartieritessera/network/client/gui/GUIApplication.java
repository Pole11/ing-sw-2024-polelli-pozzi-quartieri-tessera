package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.ViewModel;
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
    private static ViewModel viewModel;
    private static Stage mainStage;
    private static VirtualView client;
    private static VirtualServer server;

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
             guiController.setTempViewModel(viewModel);
         });
    }

    public static void changeScene(String fxml, String[] args) { // this was used when viewModel was not static
        Platform.runLater(() -> {
            FXMLLoader fxmlLoader = new FXMLLoader(GUIApplication.class.getResource(fxml));
            try {
                mainStage.getScene().setRoot(fxmlLoader.load());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            guiController = fxmlLoader.getController();
            guiController.setArgs(args);
            guiController.setClient(client);
            guiController.setServer(server);
            guiController.setTempViewModel(viewModel);
        });
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/lobby.fxml"));
        Parent root = fxmlLoader.load();

        guiController = fxmlLoader.getController();
        guiController.setClient(client);
        guiController.setServer(server);
        guiController.setTempViewModel(viewModel);
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
    public void runGui(VirtualView client, VirtualServer server, ViewModel viewModel) {
        GUIApplication.client = client;
        GUIApplication.server = server;
        GUIApplication.viewModel = viewModel;
        launch(); // create the UI thread
    }
}
