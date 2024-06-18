package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

public class ShowPointsCommandRunnable extends CommandRunnable{
    @Override
    public void executeCLI() {
        clientContainer.getCliController().showPoints();
    }

    @Override
    public void executeGUI() {
        ;
    }

    @Override
    public void executeHelp() {
        System.out.print("SHOWPOINTS\nthe command shows the scores of every player\n>");
    }
}
