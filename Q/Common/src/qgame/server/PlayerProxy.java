package qgame.server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;

import qgame.action.TurnAction;
import qgame.json.JsonConverter;
import qgame.json.JsonPrintWriter;
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
    private JsonPrintWriter printer;

    // in stream: 
    private JsonStreamParser parser;

    // TODO: Fix bug to avoid workaround:
    // private BufferedReader reader;

    private boolean quiet = false;

    private final DebugStream DEBUG_STREAM = DebugStream.ERROR;

    public PlayerProxy(String name, JsonStreamParser parser, JsonPrintWriter printer, int timeout) {
        this.name = name;
        this.parser = parser;
        this.printer = printer;
        quiet = false;

    }

    public PlayerProxy(String name, JsonStreamParser parser, JsonPrintWriter printer, boolean quiet) {
        this.name = name;
        // this.reader = reader;
        this.parser = parser;
        this.printer = printer;
        this.quiet = quiet;
    }

    protected void sendOverConnection(JsonElement el) throws IOException {
        log("Sending " + el);
        printer.sendJson(el);
    }

    public void log(Object message) {
        if (!quiet) {
            DEBUG_STREAM.s.println("Player proxy of: " + this.name + ": " + message);
        }
    }
    

    private JsonElement receive() throws IllegalStateException {
        JsonElement element = null;
        try {
            //TODO: Currently a workaround for weird bug with connection resetting on win
            element = parser.next();
        } catch (JsonParseException e) {
            log("Player proxy got weird message from client. Throwing exception");
            throw new IllegalStateException("Remote player must communicate with well-formed JSON. "
             + e.getLocalizedMessage());
        }
        log("Receive " + element);
        return element;
    }

    /**
     * Gets the next JsonElement from the remote connection.
     * @return
     * @throws IllegalStateException If the message received is not well formed JSON
     */
    private JsonElement receiveAndReturnVoidIfIOException() throws IllegalStateException {
        JsonElement element = null;
        try {

            element = parser.next();
        } catch (JsonParseException e) {
            //TODO: Currently a workaround for weird bug with connection resetting on win
            log("Player proxy got weird message from client. Simply returning void (workaround)");
            element = new JsonPrimitive("void");
        }
        log("Receive " + element);
        return element;
    }

    /**
     * Throws an exception if the element received is not "void"
     * @param e
     * @throws IllegalStateException
     */
    private void assertVoidReturn(JsonElement e) throws IllegalStateException {
        String v = JsonConverter.getAsString(e);
        if (!v.equals("void")) {
            log("Expected void but received " + e);
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
     * From an array of JsonElements, returns a JsonArray containing all elements.
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
        JsonArray args = buildArgArray(JsonConverter.playerStateToJPub(state));
        JsonElement e = buildFunctionCallJson("take-turn", args);
        try {
            sendOverConnection(e);
        } catch (IOException ex) {
            log("Encountered problem while sending: " + e);
            throw new IllegalStateException(ex);
        }
        JsonElement r = receive();
        return JsonConverter.jChoiceToTurnAction(r);
    }

    /**
     * Sends a PlayerGameState and a Bag of Tiles as JSON over the remote connection.
     * Expects "void" in return.
     */
    @Override
    public void setup(IPlayerGameState state, Bag<Tile> tiles) throws IllegalStateException {
        JsonArray a = buildArgArray(JsonConverter.playerStateToJPub(state), 
            JsonConverter.jTilesFromTiles(tiles.getItems()));
        try {
            sendOverConnection(buildFunctionCallJson("setup", a));
        } catch (IOException e) {
            log("Encountered problem while sending: " + e);
            throw new IllegalStateException(e);
        }
        JsonElement e = receive();
        assertVoidReturn(e);
    }

    /**
     * Sends a Bag of Tiles as JSON over the remote connection.
     * Expects "void" in return.
     */
    @Override
    public void newTiles(Bag<Tile> tiles) throws IllegalStateException {
        JsonArray a = buildArgArray(JsonConverter.jTilesFromTiles(tiles.getItems()));
        try {
            sendOverConnection(buildFunctionCallJson("new-tiles", a));
        } catch (IOException e) {
            log("Encountered problem while sending: " + e);
            throw new IllegalStateException(e);
        }
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
        try {
            sendOverConnection(buildFunctionCallJson("win", a));
        } catch (IOException e) {
            log("Encountered problem while sending: " + e);            
            throw new IllegalStateException(e);
        }
        JsonElement e;
        e = receiveAndReturnVoidIfIOException();

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
