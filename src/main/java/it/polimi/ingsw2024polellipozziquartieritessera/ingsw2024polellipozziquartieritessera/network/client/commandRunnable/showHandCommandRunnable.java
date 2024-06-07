package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

import java.util.NoSuchElementException;

public class showHandCommandRunnable extends  CommandRunnable{


    @Override
    public void executeCLI() {
        clientContainer.getCliController().showHand();

    }

    @Override
    public void executeGUI() {
        ;
    }
}
