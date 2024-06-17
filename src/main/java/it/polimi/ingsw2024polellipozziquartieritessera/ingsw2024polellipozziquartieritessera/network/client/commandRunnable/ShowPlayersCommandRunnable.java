package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

public class ShowPlayersCommandRunnable extends CommandRunnable {
    @Override
    public void executeCLI() {
        clientContainer.getCliController().showPlayers();
    }

    @Override
    public void executeGUI() {
        ;
    }

    @Override
    public void executeHelp() {
        System.out.print("SHOWPLAYERS\nthe command shows the nicknames and corresponding index of every player\n>");

    }
}
