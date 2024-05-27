package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.controllers;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.ViewModel;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.GUIApplication;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.VirtualServer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

abstract public class GUIController {
    @FXML private Label serverMessageLabel;
    @FXML private Label serverErrorLabel;
    // add virtual model
    private VirtualView client; // temp
    private VirtualServer server; // temp
    private static ViewModel tempViewModel;
    private boolean isSceneLoaded = false;
    private String[] args;

    @FXML
    private void initialize() {
        isSceneLoaded = true;
        System.out.println("[DEBUG] Scene is loaded with " + this.getClass());
    }

    public boolean isSceneLoaded() {
        return isSceneLoaded;
    }

    abstract public void update();

    public ViewModel getTempViewModel() {
        return tempViewModel;
    }

    public void setTempViewModel(ViewModel tempViewModel) {
        this.tempViewModel = tempViewModel;
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
                serverMessageLabel.setText("INFO FROM SERVER: " + serverMessage);
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
                serverErrorLabel.setText("ERROR FROM SERVER: " + serverMessage);
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
        try {
            GUIApplication.changeScene(fxml);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void goToScene(String fxml, ViewModel tempViewModel) {
        try {
            GUIApplication.changeScene(fxml);
            setTempViewModel(tempViewModel);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void goToScene(String fxml, ViewModel tempViewModel, String[] args) {
        try {
            GUIApplication.changeScene(fxml, args);
            setTempViewModel(tempViewModel);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public String[] getArgs() {
        return this.args;
    }

    public ImageView createCardImageView(String url, int width) {
        URL resource = GUIController.class.getResource(url);
        if (resource == null) return null;
        String imageUrl = resource.toExternalForm();
        Image image = new Image(imageUrl);

        PixelReader reader = image.getPixelReader();
        WritableImage imageWritable = new WritableImage(reader, 103, 101, 823, 547);

        ImageView imageView = new ImageView(imageWritable);
        imageView.getStyleClass().add("imageWithBorder");

        imageView.setFitHeight(width);
        imageView.setPreserveRatio(true);

        return imageView;
    }

    public void addHover(Node node) {
        Platform.runLater(() -> {
            node.setOnMouseEntered(mouseEvent -> { node.getStyleClass().add("rotateYes"); node.getStyleClass().remove("rotateNo"); });
            node.setOnMouseExited(mouseEvent -> { node.getStyleClass().add("rotateNo"); node.getStyleClass().remove("rotateYes"); });
        });
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
