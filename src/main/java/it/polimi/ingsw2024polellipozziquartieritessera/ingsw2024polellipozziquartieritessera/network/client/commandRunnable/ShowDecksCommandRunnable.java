package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

public class ShowDecksCommandRunnable extends CommandRunnable{
    @Override
    public void executeCLI() {
        clientContainer.getCliController().showDecks();
    }

    @Override
    public void executeGUI() {
        ;
    }
}
