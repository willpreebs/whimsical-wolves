package qgame.server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;

import qgame.player.Player;

/**
 * A Client represents a remote actor that is responsible for setting up a single
 * remote player
 * Contains a Player that determines how the game is played.
 * Contains a Referee Proxy that receives messages over a socket connection
 * and calls the Player methods determined by the message.
 */
public class Client implements Runnable {

    private Socket socket;
    private Player player;

    // in:
    private JsonStreamParser parser;

    // out:
    private PrintWriter printer;

    private RefereeProxy refProxy;

    // Creates a Client with a new socket instance from a given ServerSocket
    public Client(ServerSocket server, Player player) throws IOException {
        this.player = player;
        socket = new Socket(server.getInetAddress(), server.getLocalPort());
        parser = new JsonStreamParser(new InputStreamReader(socket.getInputStream()));
        printer = new PrintWriter(socket.getOutputStream(), true);  
        this.refProxy = new RefereeProxy(printer, parser, player);
    }

    public Socket getSocket() {
        return this.socket;
    }

    public RefereeProxy getRefereeProxy() {
        return this.refProxy;
    }

    public JsonStreamParser getParser() {
        return this.parser;
    }

    public PrintWriter getPrinter() {
        return this.printer;
    }

    public Player getPlayer() {
        return this.player;
    }

    protected void sendPlayerName() {
        printer.println(new JsonPrimitive(this.player.name()));
    }
    /**
     * Runs this Client including sending its player's name and starting the RefereeProxy
     * so it can handle incoming messages.
     */
    @Override
    public void run() {

        sendPlayerName();

        try {
            refProxy.listenForMessages();
        } catch (IOException e) {
            System.out.println("Ref proxy threw error");
            e.printStackTrace();
        }
    }
}
