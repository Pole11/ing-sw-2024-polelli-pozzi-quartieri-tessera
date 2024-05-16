package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewModel {
    int playerIndex;
    HashMap<Integer,String> nicknamesMap;

    public ViewModel() {
        nicknamesMap = new HashMap<>();
    }

    public void addNickname(String nickname, int pos) {
        this.nicknamesMap.put(pos, nickname);
    }
}
