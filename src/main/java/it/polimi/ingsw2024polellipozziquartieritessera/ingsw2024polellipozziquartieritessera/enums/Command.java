package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.Client;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.VirtualServer;

import java.util.function.Supplier;

public enum Command {
     HELP(HelpCommandRunnable::new, "Local"),
     ADDUSER(AdduserCommandRunnable::new, "Network"),
     START(StartCommandRunnable::new, "Network"),
     CHOOSESTARTER(ChooseStarterCommandRunnable::new, "Network"),
     CHOOSECOLOR(ChooseColorCommandRunnable::new, "Network"),
     CHOOSEOBJECTIVE(ChooseObjectiveCommandRunnable::new, "Network"),
     PLACECARD(PlaceCardCommandRunnable::new, "Network"),
     DRAWCARD(DrawCardCommandRunnable::new, "Network"),
     FLIPCARD(FlipCardCommandRunnable::new, "Network"),
     OPENCHAT(OpenChatCommandRunnable::new, "Network"),
     ADDMESSAGE(AddMessageCommandRunnable::new, "Network"),
     PING(PingCommandRunnable::new, "Network"),
     SHOWSECRETOBJECTIVES(ShowSecretObjectiveCommandRunnable::new, "Local"),
     SHOWHAND(ShowHandCommandRunnable::new, "Local"),
     SHOWBOARD(ShowBoardCommandRunnable::new, "Local");

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