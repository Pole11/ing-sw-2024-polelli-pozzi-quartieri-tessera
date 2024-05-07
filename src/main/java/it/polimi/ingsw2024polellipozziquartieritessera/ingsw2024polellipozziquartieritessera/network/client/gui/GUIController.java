package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class GUIController {
    @FXML
    private Label serverMessageLabel;

    @FXML
    public void setServerMessage(String serverMessage) { serverMessageLabel.setText(serverMessage);     }
}
