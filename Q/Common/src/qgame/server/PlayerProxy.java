package qgame.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
    private Socket clientSocket;

    private OutputStreamWriter writer;
    private JsonStreamParser parser;

    public PlayerProxy(String name, Socket clientSocket) {
        this.name = name;
        this.clientSocket = clientSocket;
        try {
            writer = new OutputStreamWriter(clientSocket.getOutputStream());
            parser = new JsonStreamParser(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            //...
        }
    }

    public void sendOverConnection(JsonElement el) throws IllegalStateException {
        try {
            System.out.println("Player proxy sending: " + el);
            writer.write(el.toString());
        } catch (IOException e) {
            throw new IllegalStateException("Problem with socket: " + e.getMessage());
        } 
    }

    private JsonElement receive() {
        JsonElement e = parser.next();
        System.out.println("Player proxy receive: " + e);
        return e;
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
        JsonElement e = buildFunctionCallJson("setup", a);
        sendOverConnection(e);
        JsonElement r = receive();
        assertVoidReturn(r);
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
