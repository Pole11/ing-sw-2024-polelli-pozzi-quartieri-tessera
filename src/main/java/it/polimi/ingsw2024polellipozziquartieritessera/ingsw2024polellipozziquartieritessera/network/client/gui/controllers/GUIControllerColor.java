package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.controllers;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Color;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Command;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.Client;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.ViewModel;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable.ChooseColorCommandRunnable;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable.ChooseStarterCommandRunnable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.rmi.RemoteException;

public class GUIControllerColor extends GUIController {
    @FXML private VBox mainContainerPrep;

    @FXML
    private void handleChooseBlueColor(ActionEvent event) {
        handleChooseColor(Color.BLUE);
    }

    @FXML
    private void handleChooseGreenColor(ActionEvent event) {
        handleChooseColor(Color.GREEN);
    }

    @FXML
    private void handleChooseYellowColor(ActionEvent event) {
        handleChooseColor(Color.YELLOW);
    }

    @FXML
    private void handleChooseRedColor(ActionEvent event) {
        handleChooseColor(Color.RED);
    }

    private void handleChooseColor(Color color) {
        /*try {
            getServer().chooseInitialColor(getClient(), color);
        } catch (RemoteException e) {
            Platform.runLater(() -> {
                setServerError("There was an error while choosing the color, please try again");
            });
        }*/
        ChooseColorCommandRunnable command = new ChooseColorCommandRunnable();
        command.setColor(color);
        addCommand(command, this);
    }

    private void changeMainContainerBorder() {
        Platform.runLater(() -> {
            for (Color colorIterator : Color.values()) { mainContainerPrep.getStyleClass().remove(colorIterator.toString().toLowerCase() + "Border"); }
            Color color = getViewModel().getColorsMap(getViewModel().getPlayerIndex());
            if (color != null) mainContainerPrep.getStyleClass().add(color.toString().toLowerCase() + "Border");
        });
    }

    private void disableChooseColorBtns(Color color) { // TODO: call this method when the ack from the server that the color is correct, but it is not mandatory, !!! even without it works great !!!
        Platform.runLater(() -> {
            for (Color colorIterator : Color.values()) {
                Node button = mainContainerPrep.lookup("#" + colorIterator.toString().toLowerCase() + "Button");
                if (button != null) ((Button) button).setOnAction(null);
            }
        });
    }

    @Override
    public void update() {
        Platform.runLater(() -> {
            setNewMessageChat();
            populateChatListview();
            changeMainContainerBorder();
        });
    }
}
