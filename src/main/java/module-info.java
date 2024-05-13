module it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires com.google.gson;
    requires java.rmi;
    requires java.smartcardio;
    requires java.sql;

    opens it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera to javafx.fxml;
    exports it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera;
    opens it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model to javafx.fxml;
    exports it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;
    opens it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server to javafx.fxml;
    exports it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server;

    opens it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui to javafx.fxml;
    exports it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui;
    opens it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client to java.rmi;
    exports it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client;

}