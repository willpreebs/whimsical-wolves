package qgame.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

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

    public PlayerProxy(String name, Socket clientSocket) {
        this.name = name;
        this.clientSocket = clientSocket;
        try {
            writer = new OutputStreamWriter(clientSocket.getOutputStream());
        } catch (IOException e) {
            //...
        }
    }

    private void sendOverConnection(JsonElement e) throws IOException {
        // JsonElement[] a = JsonConverter.getAsElementArray(e);
        // // validate a[1]
        // JsonElement[] arguments = JsonConverter.getAsElementArray(a[1]);
        
        // String methodName = JsonConverter.getAsString(a[0]);

        // String arguments = 

        // String serialized = e.getAsString()
        // String 
        writer.write(e.toString());
    }

    private JsonElement receive() {
        // TODO
        return null;
    }

    @Override
    public String name() {
        return name; 
        // sendOverConnection(//...)
    }

    @Override
    public TurnAction takeTurn(IPlayerGameState state) throws IllegalStateException, IOException {
        sendOverConnection(JsonConverter.playerStateToJPub(state));
        JsonElement e = receive();
        // TODO: convert jAction to TurnAction
        // return JsonConverter.jaction
        return null;
    }

    @Override
    public void setup(IPlayerGameState state) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setup'");
    }

    @Override
    public void newTiles(Bag<Tile> tiles) throws IllegalStateException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'newTiles'");
    }

    @Override
    public void win(boolean w) throws IllegalStateException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'win'");
    }
    
}
