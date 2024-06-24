package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.Client;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.VirtualServer;

import java.util.function.Supplier;

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
     OPENCHAT(OpenChatCommandRunnable::new, "Local"),
     START(StartCommandRunnable::new, "Network"),
     GAMEENDED(GameEndedCommandRunnable::new, "Network");


     private final Supplier<? extends CommandRunnable> commandSupplier;

     //private final CommandRunnable commandRunnable;
     private final String type;

     Command(Supplier<? extends CommandRunnable> commandSupplier, String type) {
          this.commandSupplier = commandSupplier;
          this.type = type;
     }

     public String getType() {
          return type;
     }

     public CommandRunnable getCommandRunnable(String[] message, VirtualServer server, Client clientContainer, VirtualView client) {
          CommandRunnable commandRunnable = commandSupplier.get();
          commandRunnable.setServer(server);
          commandRunnable.setMessageFromCli(message);
          commandRunnable.setClientContainer(clientContainer);
          commandRunnable.setClient(client);
          return commandRunnable;
     }
}