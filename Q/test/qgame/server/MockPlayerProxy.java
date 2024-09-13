package qgame.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;

import qgame.json.JsonPrintWriter;

public class MockPlayerProxy extends PlayerProxy {

    public MockPlayerProxy(String name, JsonStreamParser parser, JsonPrintWriter writer) {
        super(name, parser, writer, 6);
    }

    @Override
    public void sendOverConnection(JsonElement e) {
        
    }
    
}
