package qgame.server;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;

import qgame.json.JsonPrintWriter;
import qgame.player.Player;

public class MockRefereeProxy extends RefereeProxy {

    List<String> messages = new ArrayList<>();

    public MockRefereeProxy(JsonPrintWriter out, JsonStreamParser parser, Player p) {
        super(out, parser, p, false);
    }

    public List<String> getIncomingMessages() {
        return this.messages;
    }

    @Override
    public void listenForMessages() {
        JsonStreamParser p = super.getParser();
        while (true) {
            JsonElement element = p.next();
            messages.add(element.toString());
            System.out.println("Receive: " + element);
        }
    }
    
}
