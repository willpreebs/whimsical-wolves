package qgame.server;

import static qgame.util.ValidationUtil.validateArg;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;

import qgame.player.Player;

/**
 * A Client represents a remote actor that is responsible for setting up a single
 * remote player
 * Contains a Player that makes turns on the game board.
 * Contains a Referee Proxy that receives messages over a socket connection
 * and calls the Player methods determined by the message.
 * 
 * A Client may be run as a seperate thread, which calls run() upon starting
 * the thread.
 * 
 * On construction, if a Client fails to create a socket after a certain number of 
 * attempts, it throws an IOException
 */
public class Client implements Runnable {

    private Socket socket;
    private Player player;

    // in:
    private JsonStreamParser parser;

    // out:
    private PrintWriter printer;

    private RefereeProxy refProxy;

    

    private final int SOCKET_RETRIES = 3;
    private final int TIME_BETWEEN_RETRIES = 2000;

    private boolean quiet = false;
    private final DebugStream DEBUG_STREAM = DebugStream.DEBUG;

    public Client(ServerSocket server, Player player) throws IOException {
        this.player = player;
        createSocket(server.getInetAddress().getHostName(), server.getLocalPort());
        parser = new JsonStreamParser(new InputStreamReader(socket.getInputStream()));
        printer = new PrintWriter(socket.getOutputStream(), true);  
        this.refProxy = new RefereeProxy(printer, parser, player, this.quiet);
    }

    public Client(int port, ClientConfig config, Player player) throws IOException {
        this.quiet = config.isQuiet();
        this.player = player;
        createSocket(config.getHost(), port);
        this.parser = new JsonStreamParser(new InputStreamReader(socket.getInputStream()));
        this.printer = new PrintWriter(socket.getOutputStream(), true);
        this.refProxy = new RefereeProxy(printer, parser, player, this.quiet);
    }

    private void createSocket(String addr, int port) throws IOException {

        for (int retry = 0; retry < SOCKET_RETRIES; retry++) {
            try {
                this.socket = new Socket(addr, port);
                log("Socket successfully created");
                break;
            } 
            catch (IOException e) {
                log("Socket creation failed, trying again in " + TIME_BETWEEN_RETRIES + " milliseconds");
                try {
                    Thread.sleep(this.TIME_BETWEEN_RETRIES);
                } catch (InterruptedException e1) {
                    throw new IOException(
                        "Client thread interrupted while pausing between socket connection tries");
                }
            }
        }
        if (this.socket == null) {
            throw new IOException("Unable to create socket");
        }
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

    /**
     * Sends the name of the player this Client controls
     *  to the server so it can start the game
     */
    protected void sendPlayerName() {
        log("Sending " + this.player.name());
        printer.println(new JsonPrimitive(this.player.name()));
    }

    public void log(Object message) {
        if (!quiet) {
            DEBUG_STREAM.s.println("Client of " + player.name() + ": " + message);
        }
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
            log("Encountered error in ref proxy");
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            log("Error while closing socket");
        }
        log("Shutting down.");
        System.exit(0);
    }
}
