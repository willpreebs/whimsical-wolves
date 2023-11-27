package qgame.server;

import static qgame.util.ValidationUtil.validateArg;

import java.io.IOException;
import java.io.InputStreamReader;
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
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonStreamParser;

import qgame.json.JsonConverter;
import qgame.player.Player;
import qgame.referee.GameResults;
import qgame.referee.IReferee;
import qgame.referee.QReferee;


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

    private ServerSocket server;

    // Waiting period for client signup in milliseconds
    private final int WAITING_PERIOD;

    // Allowed time between a client connection to the socket and their name submission
    // in milliseconds
    private final int TIMEOUT_FOR_NAME_SUBMISSION;

    private final int NUMBER_WAITING_PERIODS;

    private final int MINIMUM_CLIENTS = 2;
    private final int MAXIMUM_CLIENTS = 4;

    private JsonElement refConfig = null;

    public Server(int tcpPort) throws IOException {
        validateArg((a) -> a >= 0 && a <= 65535, tcpPort, "Port must be between 0 and 65535");
        this.server = new ServerSocket(tcpPort);

        WAITING_PERIOD = 20000;

        // Allowed time between a client connection to the socket and their name submission
        // in milliseconds
        TIMEOUT_FOR_NAME_SUBMISSION = 3000;

        NUMBER_WAITING_PERIODS = 2;
    }

    public Server(int tcpPort, int serverTries, int serverWait, int waitForSignup, boolean quiet, JsonElement refConfig) throws IOException {
        validateArg((a) -> a >= 0 && a <= 65535, tcpPort, "Port must be between 0 and 65535");
        this.server = new ServerSocket(tcpPort);
        
        WAITING_PERIOD = serverWait * 1000;
        TIMEOUT_FOR_NAME_SUBMISSION = waitForSignup * 1000;
        NUMBER_WAITING_PERIODS = serverTries;

        this.refConfig = refConfig;
    }

    public ServerSocket getServer() {
        return this.server;
    }

    /**
     * Gets the required number of Players for a game within
     * a number of waiting periods.
     * @return
     */
    protected List<Player> getProxies() {

        List<Player> proxies = new ArrayList<>();
        int currentWaitingPeriod = 0;
        
        while (proxies.size() < MINIMUM_CLIENTS && currentWaitingPeriod < NUMBER_WAITING_PERIODS) {
            getPlayerProxiesWithinTimeout(proxies);
            currentWaitingPeriod++;
        }

        return proxies;
    }

    /**
     * Runs this server. Connects to several clients and either calls a Referee to play a Q game
     * with the remote players or sends an empty game result to all remote clients.
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
        System.out.println(JsonConverter.jResultsFromGameResults(r));

        try {
            server.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
        for (Player p : proxies) {
            PlayerProxy proxy = (PlayerProxy) p;
            proxy.sendOverConnection(emptyResult);
        }
    }

    /**
     * Get a remote Player, represented as a PlayerProxy. 
     * This method will block until there is a socket connection and
     * the remote client sends a json formatted name over the connection.
     * 
     * Between the socket connection being established and the client sending
     * a name, there is a time limit specified by TIMEOUT_FOR_NAME_SUBMISSION.
     * @param server
     * @return
     */
    private PlayerProxy getPlayerProxy(ServerSocket server) {

        ThreadFactory factory = Thread.ofVirtual().factory();
        ExecutorService executor = Executors.newFixedThreadPool(1, factory);

        while (true) {
            Socket s;
            PrintWriter out;
            JsonStreamParser parser;

            try {
                s = server.accept();
                out = new PrintWriter(s.getOutputStream(), true);
                parser = new JsonStreamParser(new InputStreamReader(s.getInputStream()));
            } catch (IOException e) {
                // Problem connecting to client, try again
                continue;
            }

            Future<JsonElement> playerNameJson = executor.submit(() -> parser.next());
            String playerName;
            try {
                playerName = playerNameJson.get(TIMEOUT_FOR_NAME_SUBMISSION, TimeUnit.MILLISECONDS).getAsString();
            } catch (InterruptedException | TimeoutException e) {
                // PlayerName not submitted in time...
                System.out.println("playerName not submitted in time");
                continue;
            } catch (JsonParseException | ExecutionException e) {
                // playerName not formatted json...
                System.out.println("playerName not formatted json");
                continue;
            }

            return new PlayerProxy(playerName, parser, out);
        }
    }

    /**
     * Returns a list of PlayerProxies that represent remote Players.
     * Connects up to a maximum number of clients within a time limit
     * specified by WAITING_PERIOD. If the time limit is reached, then
     * returns all of the players connected so far.
     * 
     * The waiting period does not reset between remote connections, instead
     * the start time is calculated and all clients must connect before
     * the amount of time specified by WAITING_PERIOD has passed.
     * @param proxies
     * @return
     */
    protected List<Player> getPlayerProxiesWithinTimeout(List<Player> proxies) {

        ThreadFactory factory = Thread.ofVirtual().factory();
        ExecutorService executor = Executors.newFixedThreadPool(1, factory);

        LocalTime start = LocalTime.now();
        while (proxies.size() < MAXIMUM_CLIENTS) {
            // calculate the time left in the waiting period
            LocalTime now = LocalTime.now();
            long waitingPeriodRemaining = WAITING_PERIOD - start.until(now, ChronoUnit.MILLIS);

            try {
                Future<PlayerProxy> proxy = executor.submit(() -> getPlayerProxy(server));
                PlayerProxy p = proxy.get(waitingPeriodRemaining, TimeUnit.MILLISECONDS);
                proxies.add(p);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                // ran out of time in waiting period, returning all of the proxies we have so far.
                return proxies;
            }
        }
        return proxies;        
    }
}
