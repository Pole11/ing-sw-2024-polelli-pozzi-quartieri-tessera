package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.controllers;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable.ChooseStarterCommandRunnable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class GUIControllerStarterCard extends GUIController {
    @FXML private StackPane starterCardImageContainerFront;
    @FXML private StackPane starterCardImageContainerBack;

    public GUIControllerStarterCard() {}

    @FXML
    public void handleChooseFrontStarter(ActionEvent event) {
        handleChooseSideStarter(Side.FRONT);
    }

    @FXML
    public void handleChooseBackStarter(ActionEvent event) {
        handleChooseSideStarter(Side.BACK);
    }

    private void handleChooseSideStarter(Side side) {
        /*try {
            getServer().chooseInitialStarterSide(getClient(), side);
        } catch (RemoteException e) {
            Platform.runLater(() -> {
                setServerError("There was an error while choosing the side of the starter card, please try again");
            });
        }*/

        ChooseStarterCommandRunnable command = new ChooseStarterCommandRunnable();
        command.setSide(side);
        addCommand(command, this);
    }

    public void setStarterCardImage(int id) {

        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                ImageView starterCardImageViewFront = createCardImageView("/img/carte_fronte/" + id + ".jpg", 180);
                starterCardImageViewFront.getStyleClass().add("clickable");
                ImageView starterCardImageViewBack = createCardImageView("/img/carte_retro/" + id + ".jpg", 180);
                starterCardImageViewBack.getStyleClass().add("clickable");

                starterCardImageViewFront.setOnMouseClicked(mouseEvent -> {
                    if (!starterCardImageViewBack.getParent().getStyleClass().contains("greenBackground"))
                        starterCardImageViewFront.getParent().getStyleClass().add("greenBackground");
                    handleChooseSideStarter(Side.FRONT);
                });
                starterCardImageViewBack.setOnMouseClicked(mouseEvent -> {
                    if (!starterCardImageViewFront.getParent().getStyleClass().contains("greenBackground"))
                        starterCardImageViewBack.getParent().getStyleClass().add("greenBackground");
                    handleChooseSideStarter(Side.BACK);

                });
                starterCardImageContainerFront.getChildren().add(starterCardImageViewFront);
                starterCardImageContainerBack.getChildren().add(starterCardImageViewBack);
                addHoverRotate(starterCardImageViewFront);
                addHoverRotate(starterCardImageViewBack);
            }
        });
    }

    @Override
    public void update() {
        Platform.runLater(() -> {
            setStarterCardImage(getViewModel().getStarterCard());
        });
    }
}
