package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.Client;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.VirtualServer;

import java.util.function.Supplier;

/**
 * Enumeration of all possible client to server commands
 */
public enum Command {
     ADDMESSAGE(AddMessageCommandRunnable::new, "Network"),
     ADDUSER(AdduserCommandRunnable::new, "Network"),
     CHOOSECOLOR(ChooseColorCommandRunnable::new, "Network"),
     CHOOSEOBJECTIVE(ChooseObjectiveCommandRunnable::new, "Network"),
     CHOOSESTARTER(ChooseStarterCommandRunnable::new, "Network"),
     DRAWCARD(DrawCardCommandRunnable::new, "Network"),
     FLIPCARD(FlipCardCommandRunnable::new, "Network"),
     HELP(HelpCommandRunnable::new, "Local"),
     PING(PingCommandRunnable::new, "Network"),
     PLACECARD(PlaceCardCommandRunnable::new, "Network"),
     SHOWBOARD(ShowBoardCommandRunnable::new, "Local"),
     SHOWCOLORS(ShowColorsCommand::new, "Local"),
     SHOWCOMMONOBJECTIVE(ShowCommonObjectiveCommandRunnable::new, "Local"),
     SHOWDECKS(ShowDecksCommandRunnable::new, "Local"),
     SHOWELEMENTS(ShowElementsCommand::new, "Local"),
     SHOWHAND(ShowHandCommandRunnable::new, "Local"),
     SHOWPLAYERS(ShowPlayersCommandRunnable::new, "Local"),
     SHOWPOINTS(ShowPointsCommandRunnable::new, "Local"),
     SHOWSECRETOBJECTIVES(ShowSecretObjectiveCommandRunnable::new, "Local"),
     STATUS(StatusCommandRunnable::new,"Local"),
     OPENCHAT(OpenChatCommandRunnable::new, "Local"),
     START(StartCommandRunnable::new, "Network"),
     GAMEENDED(GameEndedCommandRunnable::new, "Network");


    private final Supplier<? extends CommandRunnable> commandSupplier;
    private final String type;

    /**
     * Constructs a command with a command supplier and its type.
     *
     * @param commandSupplier The supplier providing the command runnable.
     * @param type            The type of the command.
     */
    Command(Supplier<? extends CommandRunnable> commandSupplier, String type) {
        this.commandSupplier = commandSupplier;
        this.type = type;
    }

    /**
     * Returns the type of the command.
     *
     * @return The type of the command.
     */
    public String getType() {
        return type;
    }

    /**
     * Retrieves the command runnable instance based on the supplied parameters.
     *
     * @param message         The message associated with the command.
     * @param server          The virtual server handling the command.
     * @param clientContainer The client container related to the command.
     * @param client          The virtual view of the client executing the command.
     * @return The command runnable instance configured with provided parameters.
     */
    public CommandRunnable getCommandRunnable(String[] message, VirtualServer server, Client clientContainer, VirtualView client) {
        CommandRunnable commandRunnable = commandSupplier.get();
        commandRunnable.setServer(server);
        commandRunnable.setMessageFromCli(message);
        commandRunnable.setClientContainer(clientContainer);
        commandRunnable.setClient(client);
        return commandRunnable;
    }
}