package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

public class
ShowHandCommandRunnable extends  CommandRunnable{


    @Override
    public void executeCLI() {
        clientContainer.getCliController().showHand();

    }

    @Override
    public void executeGUI() {
        ;
    }

    @Override
    public void executeHelp() {
        System.out.print("SHOWHAND\nthe command shows the hand of the player who invokes it\n>");

    }
}
