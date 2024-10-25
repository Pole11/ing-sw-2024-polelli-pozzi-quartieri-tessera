package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

/**
 * Allows the client to show the colors
 */
public class ShowColorsCommand extends CommandRunnable {
    @Override
    public void executeCLI() {
        clientContainer.getCliController().showColors();
    }

    @Override
    public void executeGUI() {

    }

    @Override
    public void executeHelp() {
        System.out.print("SHOWCOLORS\nthe command shows the color chosen by every player\n>");
    }
}
