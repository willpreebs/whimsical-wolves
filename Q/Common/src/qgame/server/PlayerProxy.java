package qgame.server;

import java.io.PrintWriter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;

import qgame.action.TurnAction;
import qgame.json.JsonConverter;
import qgame.player.Player;
import qgame.state.Bag;
import qgame.state.IPlayerGameState;
import qgame.state.map.Tile;

public class PlayerProxy implements Player {

    private String name;

    // out stream:
    private PrintWriter printer;

    // in stream: 
    private JsonStreamParser parser;

    public PlayerProxy(String name, JsonStreamParser parser, PrintWriter writer) {
        this.name = name;
        this.parser = parser;
        this.printer = writer;
    }
    
    protected void sendOverConnection(JsonElement el) throws IllegalStateException {
        System.out.println("Player proxy sending: " + el);
        printer.println(el);
    }

    /**
     * Gets the next JsonElement from the remote connection.
     * @return
     * @throws IllegalStateException If the message received is not well formed JSON
     */
    private JsonElement receive() throws IllegalStateException {
        JsonElement element;
        try {
            element = parser.next();
        } catch (JsonParseException e) {
            throw new IllegalStateException("Remote player must communicate with well-formed JSON. "
             + e.getLocalizedMessage());
        }
        System.out.println("Player proxy receive: " + element);
        return element;
    }

    private void assertVoidReturn(JsonElement e) throws IllegalStateException {
        String v = JsonConverter.getAsString(e);
        if (!v.equals("void")) {
            throw new IllegalStateException("Client must return \"void\"");
        }
    }

    private JsonElement buildFunctionCallJson(String methodCall, JsonArray args) {
        JsonArray a = new JsonArray();
        a.add(new JsonPrimitive(methodCall));
        a.add(args);
        return a;
    }
  
    private JsonArray buildArgArray(JsonElement... elements) {
        JsonArray a = new JsonArray();
        for (JsonElement e : elements) {
            a.add(e);
        }
        return a;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public TurnAction takeTurn(IPlayerGameState state) throws IllegalStateException {
        JsonArray args = buildArgArray(JsonConverter.playerStateToJPub(state));
        JsonElement e = buildFunctionCallJson("take-turn", args);
        sendOverConnection(e);
        JsonElement r = receive();
        return JsonConverter.jChoiceToTurnAction(r);
    }

    @Override
    public void setup(IPlayerGameState state, Bag<Tile> tiles) throws IllegalStateException {
        System.out.println("PlayerProxy: setup called");
        JsonArray a = buildArgArray(JsonConverter.playerStateToJPub(state), 
            JsonConverter.jTilesFromTiles(tiles.getItems()));
        sendOverConnection(buildFunctionCallJson("setup", a));
        JsonElement e = receive();
        assertVoidReturn(e);
    }

    @Override
    public void newTiles(Bag<Tile> tiles) throws IllegalStateException {
        JsonArray a = buildArgArray(JsonConverter.jTilesFromTiles(tiles.getItems()));
        sendOverConnection(buildFunctionCallJson("new-tiles", a));
        JsonElement e = receive();
        assertVoidReturn(e);
    }

    @Override
    public void win(boolean w) throws IllegalStateException {
        JsonArray a = buildArgArray(new JsonPrimitive(w));
        sendOverConnection(buildFunctionCallJson("win", a));
        JsonElement e = receive();
        assertVoidReturn(e);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Player) {
            Player p = (Player) other;
            return this.name.equals(p.name());
        }
        else {
            return false;
        }
    }

}
