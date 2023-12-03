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

import qgame.json.JsonPrintWriter;
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

    // in:
    private JsonStreamParser parser;
    // out:
    private JsonPrintWriter printer;


    private Player player;
    private String host;
    private int port;
    

    private final int SOCKET_RETRIES = 20;
    private final int TIME_BETWEEN_RETRIES = 250;

    private boolean quiet = false;
    private final DebugStream DEBUG_STREAM = DebugStream.ERROR;

    public Client(ServerSocket server, Player player) throws IOException {
        this.player = player;
        this.host = server.getInetAddress().getHostName();
        this.port = server.getLocalPort();
    }

    public Client(int port, ClientConfig config, Player player) throws IOException {
        this.quiet = config.isQuiet();
        this.player = player;
        this.host = config.getHost();
        this.port = port;
        // createSocket(config.getHost(), port);
        // this.parser = new JsonStreamParser(new InputStreamReader(socket.getInputStream()));
        // this.printer = new PrintWriter(socket.getOutputStream(), true);
        // this.refProxy = new RefereeProxy(printer, parser, player, this.quiet);
    }

    private void createSocket() throws IOException {

        for (int retry = 0; retry < SOCKET_RETRIES; retry++) {
            try {
                this.socket = new Socket(this.host, this.port);
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

    private void createStreams() throws IOException {
        this.parser = new JsonStreamParser(new InputStreamReader(socket.getInputStream()));
        this.printer = new JsonPrintWriter(new PrintWriter(socket.getOutputStream(), true));
    }

    // public Socket getSocket() {
    //     return this.socket;
    // }

    // public JsonStreamParser getParser() {
    //     return this.parser;
    // }

    // public PrintWriter getPrinter() {
    //     return this.printer;
    // }

    public Player getPlayer() {
        return this.player;
    }

    /**
     * Sends the name of the player this Client controls
     *  to the server so it can start the game
     */
    protected void sendPlayerName() {
        log("Sending " + this.player.name());
        try {
            printer.sendJson(new JsonPrimitive(this.player.name()));
        } catch (IOException e) {
            log("IOException. Failed to send player name");
        }
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

        try {
            createSocket();
            createStreams();
        } catch (IOException e) {
            log("Socket or stream creation failed. Shutting down");
            System.exit(1);
        }

        sendPlayerName();

        RefereeProxy refProxy = new RefereeProxy(printer, parser, player, this.quiet);

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
