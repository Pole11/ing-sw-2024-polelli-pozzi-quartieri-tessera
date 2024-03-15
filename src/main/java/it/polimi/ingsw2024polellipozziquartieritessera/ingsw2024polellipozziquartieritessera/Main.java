package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera;

import javax.json.Json;
import javax.json.stream.JsonParser;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;

public class Main {
    public void main(String argv[]){
        try (FileReader reader = new FileReader("cards.json")) {
            return
        }
        catch (FileNotFoundException e) {}
        catch (IOException e) {}
        catch (ParseException e) {}
    }

    private String readJSON () {

    }

    private void populate(){
        JsonParser parser = null;
        parser = Json.createParser(new StringReader(reader));

        parser.getObjectStream();
    }

}
