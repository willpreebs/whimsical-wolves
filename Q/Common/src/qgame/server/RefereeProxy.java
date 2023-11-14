package qgame.server;

import static qgame.util.ValidationUtil.validateArg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;

import qgame.action.TurnAction;
import qgame.json.JsonConverter;
import qgame.player.Player;
import qgame.referee.GameResults;
import qgame.referee.IReferee;
import qgame.state.Bag;
import qgame.state.IGameState;
import qgame.state.IPlayerGameState;
import qgame.state.map.Tile;

// public class RefereeProxy implements IReferee
public class RefereeProxy {

    private Socket socket;
    private Player p;
    private JsonStreamParser parser;
    private BufferedReader in; 
    private OutputStreamWriter w;

    // assumes socket is already connected to the player proxy
    public RefereeProxy(Socket socket, Player p) {
        this.socket = socket;
        try {
            this.parser = new JsonStreamParser(new InputStreamReader(socket.getInputStream()));
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.w = new OutputStreamWriter(socket.getOutputStream());
        } 
        catch (IOException e) {
            // socket failed
            // ?
        }
        this.p = p;
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
        w.write(e.toString());
    }

    public void listenForMessages() throws IOException {
        System.out.println("Ref Proxy: listening for messages");
        boolean gameOver = false;
        while (!gameOver) {
            String e = in.readLine();
            // JsonElement e;
            // try {  
            //     e = parser.next();
            // } catch(JsonParseException ex) {
            //     return;
            // }
            System.out.println("Ref proxy receive: " + e);
            // String methodName = getMethodName(e);
            // if (methodName.equals("win")) {
            //     gameOver = true;
            // }
            // JsonArray args = getArgs(e);
            // JsonElement result = makeMethodCall(methodName, args);
            // sendOverConnection(result);
            break;
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
        return new JsonPrimitive("void");
    }

    private JsonElement takeTurn(JsonArray args) {
        validateArg(a -> a.size() == 1, args, "takeTurn takes one argument");
        IPlayerGameState state = JsonConverter.playerGameStateFromJPub(args.get(0));
        TurnAction t = this.p.takeTurn(state);
        return JsonConverter.actionToJson(t);
    }

    private JsonElement newTiles(JsonArray args) {
        validateArg(a -> a.size() == 1, args, "takeTurn takes one argument");
        Bag<Tile> tiles = new Bag<>(JsonConverter.tilesFromJTileArray(args.get(0)));
        this.p.newTiles(tiles);
        return new JsonPrimitive("void");
    }

    private JsonElement win(JsonArray args) {
        validateArg(a -> a.size() == 1, args, "win takes one argument");
        this.p.win(args.getAsBoolean());
        return new JsonPrimitive("void");
    }
}
