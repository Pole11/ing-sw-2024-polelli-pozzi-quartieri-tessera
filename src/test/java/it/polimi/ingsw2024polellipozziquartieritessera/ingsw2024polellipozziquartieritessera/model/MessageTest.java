package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Color;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.time.*;
import java.util.ArrayList;

public class MessageTest {
    @Test
    void messageTestConstructor() {
        int k = 1000;

        Player author = new Player("Author Name", Color.RED);
        LocalDateTime dt = LocalDateTime.now();

        ArrayList<Message> messages = new ArrayList<Message>();
        for (int i = 0; i < k; i++) {
            String content = "contenuto " + i;
            Message m = new Message(author, dt, content);
            messages.add(m);
        }

        for (int i = 0; i < k; i++) {
            for (int j = i + 1; j < k; j++) {
                assertNotEquals(messages.get(i).getId(), messages.get(j).getId());
            }
        }
    }
}
