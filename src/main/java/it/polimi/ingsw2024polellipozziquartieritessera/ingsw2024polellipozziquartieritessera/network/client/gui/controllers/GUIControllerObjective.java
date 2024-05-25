package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.controllers;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Command;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.Client;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.ViewModel;
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
        setObjectiveCardImages(91, 94);
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
        String message = Command.CHOOSEOBJECTIVE + " " + index;
        //Client.manageInputCli(getServer(), message.split(" "), getClient());

    }


    public void setObjectiveCardImages(int id1, int id2) {
        ImageView firstObjectiveImageView = createCardImageView("/img/carte_fronte/" + id1 + ".jpg", 180);
        firstObjectiveImageView.getStyleClass().add("clickable");
        ImageView secondObjectiveImageView = createCardImageView("/img/carte_fronte/" + id2 + ".jpg", 180);
        secondObjectiveImageView.getStyleClass().add("clickable");

        addHover(firstObjectiveImageView);
        addHover(secondObjectiveImageView);

        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
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
    public void update(ViewModel viewModel) {

    }
}
