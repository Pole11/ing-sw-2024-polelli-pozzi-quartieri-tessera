package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

public class ShowCommonObjectiveCommandRunnable extends CommandRunnable{
    @Override
    public void executeCLI() {
      clientContainer.getCliController().ShowCommonObjective();
    }

    @Override
    public void executeGUI() {
        ;
    }
}
