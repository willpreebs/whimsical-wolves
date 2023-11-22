package qgame.server;

import static qgame.util.ValidationUtil.validateArg;

import java.io.IOException;
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
 * Represents a proxy for the Referee that exists remotely. Deserializes messages
 * sent over TCP in order to call Player methods.
 */
public class RefereeProxy {

    private Player p;
    private JsonStreamParser parser;
    private PrintWriter out;

    private final JsonElement VOID_ELEMENT = new JsonPrimitive("void");

    public RefereeProxy(PrintWriter out, JsonStreamParser parser, Player p) {
        this.out = out;
        this.parser = parser;
        this.p = p;
    }

    public JsonStreamParser getParser() {
        return this.parser;
    }

    public PrintWriter getOut() {
        return this.out;
    }

    public Player getPlayer() {
        return this.p;
    }

    private String getMethodName(JsonElement e) {
        JsonElement[] a = JsonConverter.getAsElementArray(e);
        return JsonConverter.getAsString(a[0]);
    } 

    private JsonArray getArgs(JsonElement e) {
        JsonElement[] a = JsonConverter.getAsElementArray(e);
        return JsonConverter.getAsArray(a[1]);
    }

    private void sendOverConnection(JsonElement e) throws IOException {
        System.out.println("Ref proxy sending: " + e);
        out.println(e.toString());
    }

    /**
     * Listens for messages continuously and calls Player methods until the game is over.
     * Determines the game to be over when a message is sent with the method name "win".
     * Assumes messages will come over the TCP connection in the following format:
     * ["methodName", [Argument...]]
     * @throws IOException If a problem occurs with the TCP connection.
     */
    public void listenForMessages() throws IOException {

        boolean gameOver = false;
        while (!gameOver) {
            // JsonElement element = null;
            String methodName = null;
            JsonArray args = null;
            try {
                JsonElement element = parser.next();
                methodName = getMethodName(element);
                args = getArgs(element);
                // System.out.println("Ref proxy receive: " + element);
            } catch (JsonParseException | IllegalArgumentException e) {
                // Server sent message that is not well formed
                // or is not the expected format.
                // TODO: Inform server?
                e.printStackTrace(out);
                continue;
            }
            if (methodName.equals("win")) {
                gameOver = true;
            }
            JsonElement result = makeMethodCall(methodName, args);
            sendOverConnection(result);
        }
    }

    private JsonElement makeMethodCall(String methodName, JsonArray args) {
        return switch (methodName) {
            case "setup" -> setup(args);
            case "take-turn" -> takeTurn(args);
            case "new-tiles" -> newTiles(args);
            case "win" -> win(args);
            default -> throw new IllegalArgumentException("Method Name does not exist");
        };
    }

    private JsonElement setup(JsonArray args) {
        validateArg(a -> a.size() == 2, args, "Setup takes two arguments");
        IPlayerGameState state = JsonConverter.playerGameStateFromJPub(args.get(0));
        Bag<Tile> tiles = new Bag<>(JsonConverter.tilesFromJTileArray(args.get(1)));
        this.p.setup(state, tiles);
        return VOID_ELEMENT;
    }

    private JsonElement takeTurn(JsonArray args) {
        validateArg(a -> a.size() == 1, args, "takeTurn takes one argument");
        IPlayerGameState state = JsonConverter.playerGameStateFromJPub(args.get(0));
        TurnAction t = this.p.takeTurn(state);
        return JsonConverter.actionToJChoice(t);
    }

    private JsonElement newTiles(JsonArray args) {
        validateArg(a -> a.size() == 1, args, "takeTurn takes one argument");
        Bag<Tile> tiles = new Bag<>(JsonConverter.tilesFromJTileArray(args.get(0)));
        this.p.newTiles(tiles);
        return VOID_ELEMENT;
    }

    private JsonElement win(JsonArray args) {
        validateArg(a -> a.size() == 1, args, "win takes one argument");
        this.p.win(args.getAsBoolean());
        return VOID_ELEMENT;
    }
}
