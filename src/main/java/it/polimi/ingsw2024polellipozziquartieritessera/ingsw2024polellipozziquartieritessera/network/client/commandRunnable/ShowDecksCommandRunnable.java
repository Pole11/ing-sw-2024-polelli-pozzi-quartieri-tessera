package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

/**
 * Allows the client to show the decks
 */
public class ShowDecksCommandRunnable extends CommandRunnable{
    @Override
    public void executeCLI() {
        clientContainer.getCliController().showDecks();
    }

    @Override
    public void executeGUI() {
        ;
    }

    @Override
    public void executeHelp() {
        System.out.print("SHOWDECKS\nthe command shows the main board of the game, including the 4 shared cards and the 2 decks\n>");
    }
}
