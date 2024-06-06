package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

public class HelpCommandRunnable extends CommandRunnable {

    @Override
    public void executeCLI() {
        clientContainer.getCliController().printAllCommands();
    }

    @Override
    public void executeGUI() {

    }

    @Override
    public void executeHelp() {

    }
}
