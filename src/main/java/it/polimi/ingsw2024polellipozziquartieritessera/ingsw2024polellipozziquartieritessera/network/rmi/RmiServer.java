package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.rmi;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.controller.Controller;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.VirtualServer;

public class RmiServer implements VirtualServer {
    final Controller controller;

    public RmiServer(Controller controller) {
        this.controller = controller;
    }

    // setUsername()
    // controlla se lo puoi fare (fase del gioco giusta e non duplicato)
    // this.controller.setUsername

    // main
}
