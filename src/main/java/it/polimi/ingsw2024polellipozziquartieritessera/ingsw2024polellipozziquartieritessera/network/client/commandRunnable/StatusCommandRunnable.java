package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

public class StatusCommandRunnable extends CommandRunnable{
    @Override
    public void executeCLI() {
        clientContainer.getCliController().status();
    }

    @Override
    public void executeGUI() {
        ;
    }

    @Override
    public void executeHelp() {
        System.out.print("STATUS\nshows current phase, turn and player\n >");
    }
}
