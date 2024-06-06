package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server;

import com.google.gson.Gson;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.Config;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Element;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.NotUniquePlayerColorException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.NotUniquePlayerNicknameException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.WrongStructureConfigurationSizeException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.Challenge;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.CoverageChallenge;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.ElementChallenge;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.StructureChallenge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Populate {

    public static boolean existStore(){
        //check if exists store
        return false;
    }

    public static String readJSON(String fileName) throws IOException {
        BufferedReader reader= null;
        try {reader = new BufferedReader(new FileReader(fileName));}
        catch (IOException e) {
            e.printStackTrace();// da sistemare
        }
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        String ls = System.getProperty("line.separator");
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        reader.close();
        String content = stringBuilder.toString();
        return content;
    }

    public static void populate(GameState gameState) throws WrongStructureConfigurationSizeException, IOException {
        String filePath = new File("").getAbsolutePath();
        String jsonString = readJSON(filePath + Config.CARD_JSON_PATH);
        Gson gson = new Gson();
        Map cards = gson.fromJson(jsonString, Map.class);

        for (Object key : cards.keySet()) {

            Map card = gson.fromJson(cards.get(key).toString(), Map.class);
            Integer id = Integer.parseInt(key.toString());

            // ------ creating challenges ------
            Challenge challenge = createChallenge(card);

            if (card.get("Type").equals("Objective")){
                if (id < Config.firstObjectiveCardId) Config.firstObjectiveCardId = id;
                gameState.addCardToCardsMap(id, new ObjectiveCard(id, challenge, (int) Double.parseDouble(card.get("Points").toString())  ));
            } else {

                // ------ creating corners ------
                Corner[] frontCorners = createCorners("FrontCorners", card, id);
                Corner[] backCorners = createCorners("BackCorners", card, id);


                // ------ creating cards ------
                if (card.get("Type").equals("Resource")){
                    if (id < Config.firstResourceCardId) Config.firstResourceCardId = id;
                    ResourceCard resourceCard = new ResourceCard(id, Element.valueOf(card.get("ResourceType").toString().toUpperCase()), (int) Double.parseDouble(card.get("Points").toString()), frontCorners, backCorners);
                    gameState.addCardToCardsMap(id, resourceCard);
                    gameState.getMainBoard().addToResourceDeck(resourceCard);
                } else if (card.get("Type").equals("Gold")){
                    if (id < Config.firstGoldCardId) Config.firstGoldCardId = id;
                    ArrayList<Element> elements = new ArrayList<>();
                    for (Object e : (ArrayList) card.get("ResourceNeeded")){
                        elements.add(Element.valueOf(e.toString().toUpperCase()));
                    }
                    GoldCard goldCard = new GoldCard(id, Element.valueOf(card.get("ResourceType").toString().toUpperCase()), challenge, elements, (int) Double.parseDouble(card.get("Points").toString()) , frontCorners, backCorners);
                    gameState.addCardToCardsMap(id, goldCard);
                    gameState.getMainBoard().addToGoldDeck(goldCard);
                } else if (card.get("Type").equals("Starter")){
                    if (id < Config.firstStarterCardId) Config.firstStarterCardId = id;
                    ArrayList<Element> elements = new ArrayList<>();
                    for (Object e : (ArrayList) card.get("CenterResources")){
                        elements.add(Element.valueOf(e.toString().toUpperCase()));
                    }
                    gameState.addCardToCardsMap(id, new StarterCard(id, frontCorners, backCorners, elements));
                }
            }
        }
    }

    private static Challenge createChallenge(Map card) throws WrongStructureConfigurationSizeException {
        Challenge challenge = null;
        if (card.get("Type").equals("Objective") || card.get("Type").equals("Gold")){
            if (card.get("ChallengeType").equals("ElementChallenge")){
                ArrayList<Element> elements = new ArrayList<>();
                for (Object e : (ArrayList) card.get("ChallengeElements")){
                    elements.add(Element.valueOf(e.toString().toUpperCase()));
                }

                challenge = new ElementChallenge(elements);
            } else if (card.get("ChallengeType").equals("StructureChallenge")){
                Element[][] configuration = new Element[Config.N_STRUCTURE_CHALLENGE_CONFIGURATION][Config.N_STRUCTURE_CHALLENGE_CONFIGURATION];
                for (int i = 0; i < Config.N_STRUCTURE_CHALLENGE_CONFIGURATION; i++){
                    for (int j = 0; j < Config.N_STRUCTURE_CHALLENGE_CONFIGURATION; j++){
                        configuration[i][j] = Element.valueOf( ( (ArrayList) card.get("Structure") ).get(3*i+j).toString().toUpperCase() );
                    }
                }
                challenge = new StructureChallenge(configuration);
            } else if (card.get("ChallengeType").equals("CoverageChallenge")){
                challenge = new CoverageChallenge();
            } else if (card.get("ChallengeType").equals("NoChallenge")){

            } else {
                System.out.println(card.get("ChallengeType"));
                throw new RuntimeException(); //sistema
            }
        }
        return challenge;
    }

    private static Corner[] createCorners(String side, Map card, Integer id){
        Corner[] corners = new Corner[Config.N_CORNERS];
        //for in frontCorners
        for (int i = 0; i < Config.N_CORNERS; i++){
            String element = ((ArrayList) card.get(side)).get(i).toString();
            // card.get("FrontCorners")).getClass() returns ArrayList so casting should be good
            if (element.equals("Empty")){
                //probably can be removed because it falls in the else clause, also in line 135
                corners[i] = new Corner(Element.EMPTY, id, false);
            } else if(element.equals("Hidden")) {
                corners[i] = new Corner(Element.EMPTY, id, true);;
            } else {
                corners[i] = new Corner(Element.valueOf(element.toUpperCase()), id, false);
            }
        }
        return corners;
    }
}
