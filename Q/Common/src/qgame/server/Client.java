package qgame.server;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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

    public Client(ServerSocket server, Player player) {
        this.player = player;
        this.host = server.getInetAddress().getHostName();
        this.port = server.getLocalPort();
    }

    public Client(int port, ClientConfig config, Player player) {
        this.quiet = config.isQuiet();
        this.player = player;
        this.host = config.getHost();
        this.port = port;
    }

    /**
     * Instantiates this.socket
     * 
     * If the socket creation fails, retries for a certain number of times set
     * by SOCKET_RETRIES and sleeps for a time specified by TIME_BETWEEN_RETRIES
     * between attempts.
     * @throws IOException If all the attempts failed.
     */
    public void createSocket() throws IOException {

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

    /**
     * Initializes the in and out streams from the socket.
     * Assumes this.socket is not null.
     * @throws IOException
     */
    public void createStreams() throws IOException {
        assertNotNull(this.socket);
        this.parser = new JsonStreamParser(new InputStreamReader(socket.getInputStream()));
        this.printer = new JsonPrintWriter(new PrintWriter(socket.getOutputStream(), true));
    }

    public Player getPlayer() {
        return this.player;
    }

    /**
     * Sends the name of the player this Client controls
     *  to the server so it can start the game
     */
    protected void sendPlayerName() throws IOException {
        log("Sending " + this.player.name());
        printer.sendJson(new JsonPrimitive(this.player.name()));
    }

    public void log(Object message) {
        if (!quiet) {
            DEBUG_STREAM.s.println("Client of " + player.name() + ": " + message);
        }
    }

    /**
     * Runs this Client
     * Steps:
     * 1. Create socket
     * 2. Create streams from socket
     * 3. send Players name over connection
     * 4. Create a RefereeProxy
     * 5. Make the RefereeProxy listen to incoming messages
     * 6. When RefereeProxy stops listening, close the socket
     * 7. System.exit() to kill all child threads that might still
     * be running 
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

        try {
            sendPlayerName();
        } catch (IOException e) {
            log("IOException. Failed to send player name. Shutting down");
            System.exit(1);
        }

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
