package qgame.server;

import java.io.PrintWriter;

import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;

public class MockPlayerProxy extends PlayerProxy {

    public MockPlayerProxy(String name, JsonStreamParser parser, PrintWriter writer) {
        super(name, parser, writer);
    }

    @Override
    public void sendOverConnection(JsonElement e) {
        
    }
    
}
