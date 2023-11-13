package qgame.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;

import netscape.javascript.JSException;
import qgame.json.JsonConverter;
import qgame.player.Player;
import qgame.referee.GameResults;
import qgame.referee.IReferee;
import qgame.referee.QReferee;
import qgame.state.IPlayerGameState;

public class Server {

    private ServerSocket server;

    // waiting period in milliseconds
    private int WAITING_PERIOD = 20000;
    private int TIMEOUT_FOR_NAME_SUBMISSION = 3000;

    private int NUMBER_WAITING_PERIODS = 2;

    private int MINIMUM_CLIENTS = 2;
    private int MAXIMUM_CLIENTS = 4;

    public Server(int tcpPort) throws IOException {
        this.server = new ServerSocket(tcpPort);
    }

    public ServerSocket getServer() {
        return this.server;
    }

    public void getClientsAndRunGame() {
        List<Player> proxies = new ArrayList<>();

        int currentWaitingPeriod = 0;

        while (proxies.size() < MINIMUM_CLIENTS && currentWaitingPeriod < NUMBER_WAITING_PERIODS) {
            getPlayerProxiesWithinTimeout(server, proxies);
            currentWaitingPeriod++;
        }

        if (proxies.size() < MINIMUM_CLIENTS) {
            // return default result
            GameResults r = new GameResults(new ArrayList<>(), new ArrayList<>());
            sendResults(r, proxies);
        }

        IReferee ref = new QReferee();
        GameResults r = ref.playGame(proxies);
        sendResults(r, proxies);
    }

    private void sendResults(GameResults r, List<Player> proxies) {
        // TODO: serialize and send gameresults
    }

    private PlayerProxy getPlayerProxyFromJsonElement(JsonElement e, Socket clientSocket) {

        String playerName = JsonConverter.getAsString(e);
        return new PlayerProxy(playerName, clientSocket);
    }

    /**
     * Get the list of player proxies. This method will block until
     * there is a socket connection.
     * @param server
     * @return
     */
    private PlayerProxy getPlayerProxy(ServerSocket server) {

        ThreadFactory factory = Thread.ofVirtual().factory();
        ExecutorService executor = Executors.newFixedThreadPool(1, factory);


        // TODO: timeout for sending a name: 3 seconds
        while (true) {
            // Socket client = null;
            try {
                Socket clientSocket = server.accept();
                JsonStreamParser parser = new JsonStreamParser
                    (new InputStreamReader(clientSocket.getInputStream()));
                Future<PlayerProxy> futureProxy = executor.submit(() -> getPlayerProxyFromJsonElement(parser.next(), clientSocket));
                return futureProxy.get(TIMEOUT_FOR_NAME_SUBMISSION, TimeUnit.MILLISECONDS);
            } catch (IOException | InterruptedException | ExecutionException | TimeoutException e) {
                // socket exception TODO
            }
        }
    }


    private List<Player> getPlayerProxiesWithinTimeout(ServerSocket server, List<Player> proxies) {
        ThreadFactory factory = Thread.ofVirtual().factory();
        ExecutorService executor = Executors.newFixedThreadPool(1, factory);

        // List<Player> proxies = new ArrayList<>();

        LocalTime start = LocalTime.now();

        while (proxies.size() < MAXIMUM_CLIENTS) {
            long millisFromStart = start.until(LocalTime.now(), ChronoUnit.MILLIS);
            Future<PlayerProxy> futureProxy = executor.submit(() -> getPlayerProxy(server));
            try {
                PlayerProxy proxy = futureProxy.get(WAITING_PERIOD - millisFromStart, TimeUnit.MILLISECONDS);
                proxies.add(proxy);
            }
            catch (InterruptedException | ExecutionException | TimeoutException e) {
                return proxies;
            }
        }
        return proxies;        
    }
}
