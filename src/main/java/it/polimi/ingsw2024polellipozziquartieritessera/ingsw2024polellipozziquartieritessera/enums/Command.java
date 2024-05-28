package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.Client;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.VirtualServer;

public enum Command {
     HELP(new HelpCommandRunnable(), "Local"),
     ADDUSER(new AdduserCommandRunnable(), "Network"),
     START(new StartCommandRunnable(), "Network"),
     CHOOSESTARTER(new ChooseStarterCommandRunnable(), "Network"),
     CHOOSECOLOR(new ChooseColorCommandRunnable(), "Network"),
     CHOOSEOBJECTIVE(new ChooseObjectiveCommandRunnable(), "Network"),
     PLACECARD(new PlaceCardCommandRunnable(), "Network"),
     DRAWCARD(new DrawCardCommandRunnable(), "Network"),
     FLIPCARD(new FlipCardCommandRunnable(), "Network"),
     OPENCHAT(new OpenChatCommandRunnable(), "Network"),
     ADDMESSAGE(new AddMessageCommandRunnable(), "Network"),
     PING(new PingCommandRunnable(), "Network");

     private final CommandRunnable commandRunnable;
     private final String type;

     private Command(final CommandRunnable commandRunnable, final String type) {
          this.commandRunnable = commandRunnable;
          this.type = type;
     }

     public String getType() {
          return type;
     }

     public CommandRunnable getCommandRunnable(String[] message, VirtualServer server, Client clientContainer, VirtualView client) {
          commandRunnable.setServer(server);
          commandRunnable.setMessageFromCli(message);
          commandRunnable.setClientContainer(clientContainer);
          commandRunnable.setClient(client);
          return commandRunnable;
     }
}