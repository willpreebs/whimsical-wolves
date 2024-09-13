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

/**
 * Represents a Player that can communicate remotely in order to 
 * determine its behavior.
 * 
 * Is called by the Q Referee and then sends the method calls as JSON
 * data over a remote connection. The result of the method call is then
 * expected to come back over the remote connection, which this Player
 * Proxy converts into either a TurnAction to send to the Referee or if
 * the return type is void, the PlayerProxy simply returns.
 */
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

    /**
     * Throws an exception if the element received is not "void"
     * @param e
     * @throws IllegalStateException
     */
    private void assertVoidReturn(JsonElement e) throws IllegalStateException {
        String v = JsonConverterUtil.getAsString(e);
        if (!v.equals("void")) {
            throw new IllegalStateException("Client must return \"void\"");
        }
    }

    /**
     * Given a method name and an array of arguments, returns a JsonArray
     * containing both.
     * @param methodCall
     * @param args
     * @return
     */
    private JsonElement buildFunctionCallJson(String methodCall, JsonArray args) {
        JsonArray a = new JsonArray();
        a.add(new JsonPrimitive(methodCall));
        a.add(args);
        return a;
    }
  
    /**
     * From an array of JsonElements, returns a JsonArray containing all of them.
     * @param elements
     * @return
     */
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

    /**
     * Sends the given PlayerGameState as JSON over the remote connection.
     * Expects a TurnAction (formatted in json) from the remote Player.
     */
    @Override
    public TurnAction takeTurn(IPlayerGameState state) throws IllegalStateException {
        JsonArray args = buildArgArray(ObjectToJson.playerStateToJPub(state));
        JsonElement e = buildFunctionCallJson("take-turn", args);
        sendOverConnection(e);
        JsonElement r = receive();
        return ObjectToJson.jChoiceToTurnAction(r);
    }

    /**
     * Sends a PlayerGameState and a Bag of Tiles as JSON over the remote connection.
     * Expects "void" in return.
     */
    @Override
    public void setup(IPlayerGameState state, Bag<Tile> tiles) throws IllegalStateException {
        System.out.println("PlayerProxy: setup called");
        JsonArray a = buildArgArray(ObjectToJson.playerStateToJPub(state), 
            ObjectToJson.jTilesFromTiles(tiles.getItems()));
        sendOverConnection(buildFunctionCallJson("setup", a));
        JsonElement e = receive();
        assertVoidReturn(e);
    }

    /**
     * Sends a Bag of Tiles as JSON over the remote connection.
     * Expects "void" in return.
     */
    @Override
    public void newTiles(Bag<Tile> tiles) throws IllegalStateException {
        JsonArray a = buildArgArray(ObjectToJson.jTilesFromTiles(tiles.getItems()));
        sendOverConnection(buildFunctionCallJson("new-tiles", a));
        JsonElement e = receive();
        assertVoidReturn(e);
    }

    /**
     * Sends a boolean as JSON over the remote connection that specifies
     * whether the player has won or not. Expects "void" in return.
     */
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
