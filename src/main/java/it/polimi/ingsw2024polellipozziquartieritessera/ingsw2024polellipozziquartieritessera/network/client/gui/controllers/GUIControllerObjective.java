package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.rmi.RemoteException;

public class GUIControllerObjective extends GUIController{
    @FXML
    private StackPane firstObjectiveImageContainer;
    @FXML private StackPane secondObjectiveImageContainer;

    public GUIControllerObjective() {
    }

    @FXML
    public void handleChooseFirstObjective(ActionEvent event) {
        handleChooseObjective(0);
    }

    @FXML
    public void handleChooseSecondObjective(ActionEvent event) {
        handleChooseObjective(1);
    }

    public void handleChooseObjective(int index) {
        try {
            getServer().chooseInitialObjective(getClient(), index);
        } catch (RemoteException e) {
            Platform.runLater(() -> {
                setServerError("There was an error while choosing the initial objective card, please try again");
            });
        }
    }


    public void setObjectiveCardImages(int id1, int id2) {
        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                ImageView firstObjectiveImageView = createCardImageView("/img/carte_fronte/" + id1 + ".jpg", 180);
                if (firstObjectiveImageView == null) return;
                firstObjectiveImageView.getStyleClass().add("clickable");
                ImageView secondObjectiveImageView = createCardImageView("/img/carte_fronte/" + id2 + ".jpg", 180);
                if (secondObjectiveImageView == null) return;
                secondObjectiveImageView.getStyleClass().add("clickable");

                addHover(firstObjectiveImageView);
                addHover(secondObjectiveImageView);

                firstObjectiveImageView.setOnMouseClicked(mouseEvent -> {
                    // OPZIONE 1
                    if (!secondObjectiveImageView.getParent().getStyleClass().contains("greenBackground"))
                        firstObjectiveImageView.getParent().getStyleClass().add("greenBackground");
                    // OPZIONE 2
                    //firstObjectiveImageView.setOnMouseClicked(null);
                    //secondObjectiveImageView.setOnMouseClicked(null);
                    handleChooseObjective(0);
                });
                secondObjectiveImageView.setOnMouseClicked(mouseEvent -> {
                    // OPZIONE 1
                    if (!firstObjectiveImageView.getParent().getStyleClass().contains("greenBackground"))
                        secondObjectiveImageView.getParent().getStyleClass().add("greenBackground");
                    // OPZIONE 2
                    //firstObjectiveImageView.setOnMouseClicked(null);
                    //secondObjectiveImageView.setOnMouseClicked(null);
                    handleChooseObjective(1);

                });
                firstObjectiveImageContainer.getChildren().add(firstObjectiveImageView);
                secondObjectiveImageContainer.getChildren().add(secondObjectiveImageView);
            }
        });
    }

    @Override
    public void update() {
        Platform.runLater(() -> {
            setObjectiveCardImages(getViewModel().getSecretObjectiveCards()[0], getViewModel().getSecretObjectiveCards()[1]);
        });
    }
}
