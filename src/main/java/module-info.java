module it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.google.gson;
    requires java.rmi;

    opens it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera to javafx.fxml;
    exports it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera;
    exports it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.tutorial;
    opens it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.tutorial to javafx.fxml;
    exports it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;
    opens it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model to javafx.fxml;
}