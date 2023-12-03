package qgame.server;
 
import static qgame.util.ValidationUtil.validateArg;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;

import qgame.json.JsonConverter;
import qgame.json.JsonPrintWriter;
import qgame.player.Player;
import qgame.referee.GameResults;
import qgame.referee.IReferee;
import qgame.referee.QReferee;
import qgame.referee.RefereeConfig;


/**
 * Represents a Server that can connect over TCP to several remote Clients and start a Q Game.
 * 
 * Connects to Clients one by one until the maximum number is reached or the waiting period expires.
 * If a client connects to the socket, they must provide a name for the player within a timeout period.
 * 
 * After a number of waiting periods, if the minimum number of clients connect then a game is played
 * with the remote players.
 * 
 * To connect to this Server, a Client creates a socket connection with the same address
 * and port of the ServerSocket that is contained in this class. After a connection is
 * established, the Client is expected to send a player name in json over the connection.
 * 
 */
public class Server implements Runnable {

    private ServerSocket serverSocket;

    // Waiting period for client signup in milliseconds
    private final int WAITING_PERIOD;

    // Allowed time between a client connection to the socket and their name submission
    // In milliseconds
    private final int TIMEOUT_FOR_NAME_SUBMISSION;
    
    // Number of times a server will have a waiting period to connect to clients
    private final int NUMBER_WAITING_PERIODS;

    private final int MINIMUM_CLIENTS = 2;
    private final int MAXIMUM_CLIENTS = 4;

    private RefereeConfig refConfig;

    private PrintStream resultsStream = System.out;

    private List<Socket> sockets = new ArrayList<>();

    private boolean quiet = false;
    private final DebugStream DEBUG_STREAM = DebugStream.ERROR;

    public Server(int tcpPort) throws IOException {
        validateArg((a) -> a >= 0 && a <= 65535, tcpPort, "Port must be between 0 and 65535");
        this.serverSocket = new ServerSocket(tcpPort);

        WAITING_PERIOD = 20000;
        TIMEOUT_FOR_NAME_SUBMISSION = 3000;

        NUMBER_WAITING_PERIODS = 2;
    }

    public Server(int tcpPort, ServerConfig config) throws IOException {
        validateArg((a) -> a >= 0 && a <= 65535, tcpPort, "Port must be between 0 and 65535");
        this.serverSocket = new ServerSocket(tcpPort);
        this.quiet = config.isQuiet();
        WAITING_PERIOD = config.getServerWait() * 1000;
        TIMEOUT_FOR_NAME_SUBMISSION = config.getWaitForSignup() * 1000;
        NUMBER_WAITING_PERIODS = config.getServerTries();

        this.refConfig = config.getRefSpec();
    }

    public ServerSocket getServerSocket() {
        return this.serverSocket;
    }

    /**
     * Enables the options of setting a PrintStream to get
     * the results of a game.
     * @param s
     */
    public void setResultStream(PrintStream s) {
        this.resultsStream = s;
    }

    public void log(Object message) {
        if (!quiet) {
            DEBUG_STREAM.s.println("Server: " + message);
        }
    }

    private void printOutResults(GameResults r) {
        resultsStream.println(JsonConverter.jResultsFromGameResults(r));
    }
    
    /**
     * Runs this server. Connects to several clients and either calls a Referee to play a Q game
     * with the remote players or sends an empty game result to all remote clients if the minimum
     * number of clients hasn't been reached.
     * 
     * Steps:
     * 1. Connect to Clients to get Players for a game
     * 2. Sends empty game result if the minimum number of players isn't enough.
     * 3. Create a QReferee to run the game
     * 4. Call playGame on the Referee
     * 5. Print out the results of the game
     * 6. Close the socket connections
     * 7. Close the server socket
     * 8. Use System.exit to kill any child processes that might still be running
     */
    @Override
    public void run() {

        List<Player> proxies = getProxies();

        if (proxies.size() < MINIMUM_CLIENTS) {
            sendEmptyGameResult(proxies);
            return;
        }

        IReferee ref = this.refConfig == null ?
        new QReferee() : new QReferee(this.refConfig);
        
        GameResults r = ref.playGame(proxies);
        printOutResults(r);

        closeSocketConnections();
        log("Closed all client sockets");
        try {
            serverSocket.close();
        } catch (IOException e) {
            log("Error while closing server socket");
        }
        log("Closed server socket. Shutting down");
        System.exit(0);
    }

    /**
     * Closes all connections to sockets.
     */
    public void closeSocketConnections() {
        for (Socket s : this.sockets) {
            try {
                s.close();
            } catch (IOException e) {
                log("Error while closing socket");
            }
        }
    }

    /**
     * Gets the required number of Players for a game within
     * a number of waiting periods.
     * @return
     */
    protected List<Player> getProxies() {

        List<Player> proxies = new ArrayList<>();

        for (int i = 0; i < NUMBER_WAITING_PERIODS; i++) {
            getPlayerProxiesWithinTimeout(proxies);
            if (proxies.size() >= MINIMUM_CLIENTS) {
                break;
            }
        }

        return proxies;
    }

    /**
     * Appends the given list with the connected Players
     * 
     * Connects up to a maximum number of clients within a time limit
     * specified by WAITING_PERIOD and adds them to the given list.
     * 
     * The waiting period does not reset between remote connections, instead
     * the start time is calculated and all clients must connect before
     * the amount of time specified by WAITING_PERIOD has passed.
     * @param proxies
     * @return
     */
    protected void getPlayerProxiesWithinTimeout(List<Player> proxies) {

        ThreadFactory factory = Thread.ofVirtual().factory();
        ExecutorService executor = Executors.newFixedThreadPool(1, factory);

        LocalTime start = LocalTime.now();
        while (proxies.size() < MAXIMUM_CLIENTS) {
            // calculate the time left in the waiting period
            LocalTime now = LocalTime.now();
            long waitingPeriodRemaining = WAITING_PERIOD - start.until(now, ChronoUnit.MILLIS);
            log("Waiting period remaining: " + waitingPeriodRemaining);
            try {
                Future<PlayerProxy> proxy = executor.submit(() -> connectToPlayerProxy(serverSocket));
                PlayerProxy p = proxy.get(waitingPeriodRemaining, TimeUnit.MILLISECONDS);
                proxies.add(p);
            } catch (InterruptedException | TimeoutException e) {
                // ran out of time in waiting period or an error occurred while connecting to a client
                log("Ran out of time in the waiting period");
                return;
            }
            catch (ExecutionException e) {
                // A player failed to deliver their name in time, or some other error.
                log("Encountered error while connecting to a client");
                continue;
            }
        }        
    }

    /**
     * Get a remote Player, represented as a PlayerProxy. 
     * This method will block until there is a socket connection and
     * the remote client sends a json formatted name over the connection.
     * 
     * Between the socket connection being established and the client sending
     * a name, there is a time limit specified by TIMEOUT_FOR_NAME_SUBMISSION.
     * 
     *  
     * 
     * @param server
     * @return
     */
    private PlayerProxy connectToPlayerProxy(ServerSocket server) throws IOException, ExecutionException {

        ThreadFactory factory = Thread.ofVirtual().factory();
        ExecutorService executor = Executors.newFixedThreadPool(1, factory);

        Socket s = server.accept();
        log("Socket connection accepted for new player proxy");
        JsonPrintWriter out = new JsonPrintWriter(new PrintWriter(s.getOutputStream(), true));
        JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(s.getInputStream()));

        Future<JsonElement> playerNameJson = executor.submit(() -> parser.next());
        try {
            String playerName = playerNameJson.get(TIMEOUT_FOR_NAME_SUBMISSION, TimeUnit.MILLISECONDS).getAsString();
            log("get player name: " + playerName);
            sockets.add(s);
            return new PlayerProxy(playerName, parser, out, this.quiet);
        } catch (TimeoutException | InterruptedException e) {
            log("Ran out of time getting player's name");
            throw new ExecutionException(e);
        }
    }

    /**
     * Sends an empty game result to all Players
     * Assumes all Players in given list are PlayerProxy
     * @param proxies
     */
    protected void sendEmptyGameResult (List<Player> proxies) {
        JsonArray emptyResult = new JsonArray();
        emptyResult.add(new JsonArray());
        emptyResult.add(new JsonArray());
        log("Sending: " + emptyResult);
        for (Player p : proxies) {
            PlayerProxy proxy = (PlayerProxy) p;
            try {
                proxy.sendOverConnection(emptyResult);
            } catch (IOException e) {
                log("Failed to send empty game result to player");
                continue;
            }
        }
    }
}
