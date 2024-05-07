package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui;

import javafx.application.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.*;

import java.io.*;

public class GUIApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        //FXMLLoader fxmlLoader = new FXMLLoader(GUIApplication.class.getResource("/it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui/game.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(GUIApplication.class.getResource("/game.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Codex Naturalis");
        stage.setScene(scene);
        stage.show();
    }

    public void runGui() {
        launch(); // create the UI thread
    }
}
