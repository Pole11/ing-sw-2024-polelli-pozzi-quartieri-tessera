package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.controllers;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Command;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.Client;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.ViewModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.rmi.RemoteException;

public class GUIControllerStarterCard extends GUIController {
    @FXML private StackPane starterCardImageContainerFront;
    @FXML private StackPane starterCardImageContainerBack;

    public GUIControllerStarterCard() {
        setStarterCardImage(84);
    }

    @FXML
    public void handleChooseFrontStarter(ActionEvent event) {
        handleChooseSideStarter(Side.FRONT);
    }

    @FXML
    public void handleChooseBackStarter(ActionEvent event) {
        handleChooseSideStarter(Side.BACK);
    }

    private void handleChooseSideStarter(Side side) {
        String message = Command.CHOOSESTARTER + " " + side;
        //Client.manageInputCli(getServer(), message.split(" "), getClient());

    }

    public void setStarterCardImage(int id) {
        ImageView starterCardImageViewFront = createCardImageView("/img/carte_fronte/" + id + ".jpg", 180);
        starterCardImageViewFront.getStyleClass().add("clickable");
        ImageView starterCardImageViewBack = createCardImageView("/img/carte_retro/" + id + ".jpg", 180);
        starterCardImageViewBack.getStyleClass().add("clickable");

        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                starterCardImageViewFront.setOnMouseClicked(mouseEvent -> {
                    // OPZIONE 1
                    if (!starterCardImageViewBack.getParent().getStyleClass().contains("greenBackground"))
                        starterCardImageViewFront.getParent().getStyleClass().add("greenBackground");
                    // OPZIONE 2
                    //starterCardImageViewFront.setOnMouseClicked(null);
                    //starterCardImageViewBack.setOnMouseClicked(null);
                    handleChooseSideStarter(Side.FRONT);
                });
                starterCardImageViewBack.setOnMouseClicked(mouseEvent -> {
                    // OPZIONE 1
                    if (!starterCardImageViewFront.getParent().getStyleClass().contains("greenBackground"))
                        starterCardImageViewBack.getParent().getStyleClass().add("greenBackground");
                    // OPZIONE 2
                    //starterCardImageViewFront.setOnMouseClicked(null);
                    //starterCardImageViewBack.setOnMouseClicked(null);
                    handleChooseSideStarter(Side.BACK);

                });
                starterCardImageContainerFront.getChildren().add(starterCardImageViewFront);
                starterCardImageContainerBack.getChildren().add(starterCardImageViewBack);
                addHover(starterCardImageViewFront);
                addHover(starterCardImageViewBack);
            }
        });
    }

    @Override
    public void update(ViewModel viewModel) {

    }
}
