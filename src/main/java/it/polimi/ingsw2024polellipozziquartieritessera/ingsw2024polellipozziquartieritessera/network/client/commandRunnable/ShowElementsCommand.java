package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

public class ShowElementsCommand extends CommandRunnable{
    @Override
    public void executeCLI() {
        clientContainer.getCliController().showElements();
    }

    @Override
    public void executeGUI() {

    }

    @Override
    public void executeHelp() {

    }
}
