package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

public class ShowSecretObjectiveCommandRunnable extends CommandRunnable{
    @Override
    public void executeCLI() {
      clientContainer.getCliController().ShowSecretObjective();
    }

    @Override
    public void executeGUI() {
        ;
    }
}
