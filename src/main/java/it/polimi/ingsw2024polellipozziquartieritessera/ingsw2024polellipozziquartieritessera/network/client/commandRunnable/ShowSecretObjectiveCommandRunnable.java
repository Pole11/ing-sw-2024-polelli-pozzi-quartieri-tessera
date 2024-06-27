package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

/**
 * Allows the client to show his secret objective
 */
public class ShowSecretObjectiveCommandRunnable extends CommandRunnable{
    @Override
    public void executeCLI() {
      clientContainer.getCliController().ShowSecretObjective();
    }

    @Override
    public void executeGUI() {
        ;
    }

    @Override
    public void executeHelp() {
        System.out.print("SHOWSECRETOBJECTIVE\nthe command prints your secret objective, if you already chose it, or the objectives you have to choose from\n> ");
    }
}
