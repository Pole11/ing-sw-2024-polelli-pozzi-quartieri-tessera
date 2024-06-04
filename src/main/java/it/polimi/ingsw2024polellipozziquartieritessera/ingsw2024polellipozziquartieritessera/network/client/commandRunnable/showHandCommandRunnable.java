package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

import java.util.NoSuchElementException;

public class showHandCommandRunnable extends  CommandRunnable{


    @Override
    public void executeCLI() {
        try {
            clientContainer.getCliController().showHand();
        } catch (Exception r){
            System.err.println("Error: hand not initialized yet\n> ");
        }
    }

    @Override
    public void executeGUI() {
        ;
    }
}
