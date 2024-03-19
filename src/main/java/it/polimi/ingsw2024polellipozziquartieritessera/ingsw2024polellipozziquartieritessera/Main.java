package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Resource;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.card.*;


public class Main {
    public static void main(String argv[]) throws IOException{
        boolean store = existStore(); //FA persistance
        if (store){
            // takes data from store
        } else {
            populate();
        }
    }

    private static boolean existStore(){
        //check if exists store
        return false;
    }

    private static String readJSON(String fileName) throws IOException {
        BufferedReader reader= null;
        try {reader = new BufferedReader(new FileReader(fileName));}
        catch (IOException e) {
            System.out.println(e);// da sistemare
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

    private static void populate() throws IOException{
        String filePath = new File("").getAbsolutePath();
        // path poi da mettere in config
        String jsonString = readJSON(filePath + "/src/main/java/it/polimi/ingsw2024polellipozziquartieritessera/ingsw2024polellipozziquartieritessera/cards.json");
        Gson gson = new Gson();
        Map cards = gson.fromJson(jsonString, Map.class);
        HashMap cardsMap = new HashMap<Integer, Card>();
        for (Object key : cards.keySet()) {
            Map card = gson.fromJson(cards.get(key).toString(), Map.class);
            Integer id = Integer.parseInt(key.toString());
            if (card.get("Type").equals("Objective")){
                //
            } else {
                System.out.println(card.get("FrontCorner"));
                Corner[] frontCorners = null;
                Corner[] backCorners = null;
                /*switch (card.get("Type")){
                    case "Resource":
                        cardsMap.put(id, new ResourceCard(id.intValue(), card.get("ResourceType"), card.get(("Points")), frontCorners, backCorners, centerBackResource)) );
                }*/
            }


        }

    }
}