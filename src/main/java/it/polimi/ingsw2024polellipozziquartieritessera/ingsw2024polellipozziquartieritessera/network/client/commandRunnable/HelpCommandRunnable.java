package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

/**
 * Allows a client to ask for help commands
 */
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
        System.out.print("HELP\nmore information with HELP -h -h\n> ");
    }
}
