package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.CornerPos;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;

import java.rmi.RemoteException;

public class PlaceCardCommandRunnable extends CommandRunnable{
    private int placingCardId;
    private int tableCardId;
    private CornerPos tableCornerPos;
    private Side placingCardSide;

    @Override
    public void executeCLI() {
        try {
            int placingCardId;
            int tableCardId;
            CornerPos tableCornerPos;
            Side placingCardSide;
            try {
                placingCardId = Integer.parseInt(messageFromCli[1]);
            } catch (IllegalArgumentException e) {
                System.err.print(("Invalid id of the placing card, please insert a valid id\n> "));
                return;
            }
            try {
                tableCardId = Integer.parseInt(messageFromCli[2]);
            } catch (IllegalArgumentException e) {
                System.err.print(("Invalid id of the table card, please insert a valid id\n> "));
                return;
            }
            try {
                tableCornerPos = CornerPos.valueOf(messageFromCli[3].toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.print(("Invalid corner, please insert a valid corner position (Upleft / Upright / Downleft / Downright)\n> "));
                return;
            }
            try {
                placingCardSide = Side.valueOf(messageFromCli[4].toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.print(("Invalid side, please insert a valid side (Front / Back)\n> "));
                return;
            }
            try {
                server.placeCard(clientContainer.getClient(), placingCardId, tableCardId, tableCornerPos, placingCardSide);
            } catch (RemoteException e) {
                serverDisconnected();
            }
        } catch(IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            System.err.print("INVALID COMMAND\n> ");
        }
    }

    public void setParams(int placingCardId, int tableCardId, CornerPos tableCornerPos, Side placingCardSide){
        this.placingCardId = placingCardId;
        this.tableCardId = tableCardId;
        this.tableCornerPos = tableCornerPos;
        this.placingCardSide = placingCardSide;
    }

    public void executeGUI() {
        try {
            server.placeCard(clientContainer.getClient(), placingCardId, tableCardId, tableCornerPos, placingCardSide);
        } catch (RemoteException e) {
            serverDisconnected();
        }
    }

}
