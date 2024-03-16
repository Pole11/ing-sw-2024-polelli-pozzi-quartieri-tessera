package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import com.google.gson.Gson;
import java.util.Map;
import java.util.stream.*;

public class Main {
    public static void main(String argv[]) throws IOException{
        boolean store = existStore();
        if (store){
            //
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
        Map map = gson.fromJson(jsonString, Map.class);
        map.entrySet().stream().forEach();

    }
}