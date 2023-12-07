package qgame.server;

import static qgame.util.ValidationUtil.validateArg;

import java.io.IOException;
import java.util.NoSuchElementException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;

import qgame.action.TurnAction;
import qgame.json.JsonConverter;
import qgame.json.JsonConverterUtil;
import qgame.json.JsonPrintWriter;
import qgame.json.JsonToObject;
import qgame.json.ObjectToJson;
import qgame.player.Player;
import qgame.state.Bag;
import qgame.state.IPlayerGameState;
import qgame.state.map.Tile;

/**
 * Represents a proxy for the Referee that exists remotely. Deserializes messages
 * sent over TCP in order to call Player methods.
 */
public class RefereeProxy {

    private Player p;
    private JsonStreamParser parser;
    private JsonPrintWriter out;

    private boolean quiet = false;

    private final JsonElement VOID_ELEMENT = new JsonPrimitive("void");

    private final DebugStream DEBUG_STREAM = DebugStream.ERROR;

    public RefereeProxy(JsonPrintWriter out, JsonStreamParser parser, Player p, boolean quiet) {
        this.out = out;
        this.parser = parser;
        this.p = p;
        this.quiet = quiet;
    }

    public JsonStreamParser getParser() {
        return this.parser;
    }

    public JsonPrintWriter getOut() {
        return this.out;
    }

    public Player getPlayer() {
        return this.p;
    }

    public void log(Object message) {

        if (!quiet) {
            DEBUG_STREAM.s.println("Referee proxy of " + this.p.name() + ": " + message);
        }
    }

    private String getMethodName(JsonElement e) {
        JsonElement[] a = JsonConverterUtil.getAsElementArray(e);
        return JsonConverterUtil.getAsString(a[0]);
    } 

    private JsonArray getArgs(JsonElement e) {
        JsonElement[] a = JsonConverterUtil.getAsElementArray(e);
        return JsonConverterUtil.getAsArray(a[1]);
    }

    private void sendOverConnection(JsonElement e) throws IOException {
        log("Sending " + e);
        out.sendJson(e);
    }

    /**
     * Listens for messages continuously and calls Player methods until the game is over.
     * Determines the game to be over when a message is sent with the method name "win".
     * Assumes messages will come over the TCP connection in the following format:
     * ["methodName", [Argument...]].
     * 
     * After the Player returns, the result is sent back as JSON over the same remote connection
     * to the PlayerProxy.
     * @throws IOException If a problem occurs with the TCP connection.
     */
    public void listenForMessages() throws IOException {

        log("Start listening for messages");
        
        boolean gameOver = false;
        
        while (!gameOver) {
            String methodName = null;
            JsonArray args = null;
            try {
                JsonElement element = receiveMessage();

                log("Receive " + element);
                methodName = getMethodName(element);
                args = getArgs(element);

                if (methodName.equals("win")) {
                    log("Received win, game is over");
                    gameOver = true;
                }
            }
            catch (JsonParseException | IllegalArgumentException | NoSuchElementException e) {
                log("Received unexpected or badly formed message from server. Shutting down");
                break;
            }
            
            JsonElement result = makeMethodCall(methodName, args);
            sendOverConnection(result);
        }
    }

    private JsonElement receiveMessage() throws JsonParseException {

        JsonElement element = parser.next();
        return element;
    }

    /**
     * Calls a player method given a method name and an array of arguments.
     * @param methodName
     * @param args
     * @return A JsonElement representing the result of the method call
     */
    private JsonElement makeMethodCall(String methodName, JsonArray args) {
        return switch (methodName) {
            case "setup" -> setup(args);
            case "take-turn" -> takeTurn(args);
            case "new-tiles" -> newTiles(args);
            case "win" -> win(args);
            default -> throw new IllegalArgumentException("Method Name does not exist");
        };
    }

    /**
     * Calls the setup Player method
     * @param args An array of two JsonElements, the first one representing an
     * IPlayerGameState (the starting state of the game) and the second one representing
     * the player's starting tiles. 
     * @return "void" if setup returns successfully.
     */
    private JsonElement setup(JsonArray args) {
        validateArg(a -> a.size() == 2, args, "Setup takes two arguments");
        IPlayerGameState state = JsonToObject.playerGameStateFromJPub(args.get(0));
        Bag<Tile> tiles = new Bag<>(JsonToObject.tilesFromJTileArray(args.get(1)));
        this.p.setup(state, tiles);
        return VOID_ELEMENT;
    }

    /**
     * Calls the takeTurn Player method.
     * @param args A JsonArray with one JsonElement, representing the current player
     * state of the game. 
     * @return the resulting TurnAction as a JsonElement.
     */
    private JsonElement takeTurn(JsonArray args) {
        validateArg(a -> a.size() == 1, args, "takeTurn takes one argument");
        IPlayerGameState state = JsonToObject.playerGameStateFromJPub(args.get(0));
        TurnAction t = this.p.takeTurn(state);
        return ObjectToJson.actionToJChoice(t);
    }

    /**
     * Calls the newTiles Player method.
     * @param args A JsonArray with one JsonElement, representing the 
     * Player's new bag of tiles.
     * @return "void" if newTiles returns successfully.
     */
    private JsonElement newTiles(JsonArray args) {
        validateArg(a -> a.size() == 1, args, "takeTurn takes one argument");
        Bag<Tile> tiles = new Bag<>(JsonToObject.tilesFromJTileArray(args.get(0)));
        this.p.newTiles(tiles);
        return VOID_ELEMENT;
    }

    /**
     * Calls the win Player method.
     * @param args A JsonArray with one JsonElement, which is a boolean that 
     * specifies if this Player won the game or not.
     * @return "void" if win returns successfully.
     */
    private JsonElement win(JsonArray args) {
        validateArg(a -> a.size() == 1, args, "win takes one argument");
        this.p.win(args.getAsBoolean());
        return VOID_ELEMENT;
    }
}
