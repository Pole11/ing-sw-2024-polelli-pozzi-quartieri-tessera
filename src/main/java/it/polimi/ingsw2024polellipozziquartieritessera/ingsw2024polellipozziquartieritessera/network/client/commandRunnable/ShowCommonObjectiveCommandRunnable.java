package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

/**
 * Allows the client to show the objectives cards
 */
public class ShowCommonObjectiveCommandRunnable extends CommandRunnable{
    @Override
    public void executeCLI() {
      clientContainer.getCliController().ShowCommonObjective();
    }

    @Override
    public void executeGUI() {
        ;
    }

    @Override
    public void executeHelp() {
        System.out.print("SHOWCOMMONOBJECTIVES\nthe command shows the objective card shared between all the players\n>");
    }
}
