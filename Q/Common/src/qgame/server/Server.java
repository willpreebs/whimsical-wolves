package qgame.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;

import netscape.javascript.JSException;
import qgame.json.JsonConverter;
import qgame.player.Player;
import qgame.referee.GameResults;
import qgame.referee.IReferee;
import qgame.referee.QReferee;
import qgame.state.IPlayerGameState;

public class Server implements Runnable {
// public class Server {

    private ServerSocket server;

    // waiting period in milliseconds
    private int WAITING_PERIOD = 20000;
    private int TIMEOUT_FOR_NAME_SUBMISSION = 3000;

    private int NUMBER_WAITING_PERIODS = 2;

    private int MINIMUM_CLIENTS = 2;
    private int MAXIMUM_CLIENTS = 4;

    public Server(int tcpPort) throws IOException {
        // this.server = new ServerSocket(tcpPort);
        this.server = new ServerSocket(tcpPort);
    }

    public ServerSocket getServer() {
        return this.server;
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        private String playerName;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            System.out.println("Running client handler");
            try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(
              new InputStreamReader(clientSocket.getInputStream()));
            }catch(IOException e){}
            
            JsonStreamParser parser = new JsonStreamParser(in);
            playerName = parser.next().getAsString();
            System.out.println("Client handler: received player name: " + playerName);
            out.println("test");
            // TODO: need to keep ClientHandler alive
        }
        public String getPlayerName() {
            return this.playerName;
        }
        public Socket getSocket() {
            return this.clientSocket;
        }
    }

    @Override
    public void run() {
        List<Player> proxies = new ArrayList<>();

        int currentWaitingPeriod = 0;

        // while (true)
        //     try {
        //         new ClientHandler(server.accept()).start();
        //     } catch (IOException e) {
        //         // TODO Auto-generated catch block
        //         e.printStackTrace();
        //     }
        while (proxies.size() < MINIMUM_CLIENTS && currentWaitingPeriod < NUMBER_WAITING_PERIODS) {
            getPlayerProxiesWithinTimeout(server, proxies);
            currentWaitingPeriod++;
        }

        if (proxies.size() < MINIMUM_CLIENTS) {
            sendEmptyGameResult(proxies);
            return;
        }

        IReferee ref = new QReferee();
        GameResults r = ref.playGame(proxies);

        try {
            server.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // Assumes all Players in given list are PlayerProxy
    private void sendEmptyGameResult (List<Player> proxies) {
        JsonArray emptyResult = new JsonArray();
        emptyResult.add(new JsonArray());
        emptyResult.add(new JsonArray());
        for (Player p : proxies) {
            PlayerProxy proxy = (PlayerProxy) p;
            proxy.sendOverConnection(emptyResult);
        }
    }

    private PlayerProxy getPlayerProxyFromJsonElement(JsonStreamParser parser, Socket clientSocket) {
        JsonElement e = parser.next();
        System.out.println("server received: " + e.toString());
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

                // Future<String> playerName = new ClientHandler(server.accept()).start();
                ClientHandler h = new ClientHandler(server.accept());
                // Future<?> runH = executor.submit(h);
                // try {
                //     runH.get(TIMEOUT_FOR_NAME_SUBMISSION, TimeUnit.MILLISECONDS);
                // } catch (InterruptedException | ExecutionException | TimeoutException e) {
                //     // TODO Auto-generated catch block
                //     e.printStackTrace();
                // }
                h.run();
                String playerName = h.getPlayerName();
                System.out.println("Server: creating and returning playerproxy: " + playerName);
                // h.interrupt();
                return new PlayerProxy(playerName, h.getSocket());
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }

                // JsonStreamParser parser = new JsonStreamParser
                //     (new InputStreamReader(clientSocket.getInputStream()));
                // Future<PlayerProxy> futureProxy = executor.submit(() -> getPlayerProxyFromJsonElement(parser, clientSocket));
                // return futureProxy.get(TIMEOUT_FOR_NAME_SUBMISSION, TimeUnit.MILLISECONDS);
            // } catch (IOException | InterruptedException | ExecutionException | TimeoutException e) {
            //     // allow another connection to the socket
            // }
        }
    }


    private List<Player> getPlayerProxiesWithinTimeout(ServerSocket server, List<Player> proxies) {
        ThreadFactory factory = Thread.ofVirtual().factory();
        ExecutorService executor = Executors.newFixedThreadPool(1, factory);

        LocalTime start = LocalTime.now();

        while (proxies.size() < MAXIMUM_CLIENTS) {

            LocalTime now = LocalTime.now();
            long millisFromStart = start.until(now, ChronoUnit.MILLIS);
            // Future<PlayerProxy> futureProxy = executor.submit(() -> getPlayerProxy(server));
            // TODO timeout
            PlayerProxy p = getPlayerProxy(server);
            System.out.println("Server: Adding player proxy to list: " + p.name());
            proxies.add(p);
            // try {
            //     System.out.println("Server: get a player proxy");
            //     PlayerProxy proxy = futureProxy.get(WAITING_PERIOD - millisFromStart, TimeUnit.MILLISECONDS);
            //     proxies.add(proxy);
            // }
            // catch (InterruptedException | ExecutionException | TimeoutException e) {
            //     return proxies;
            // }
        }
        return proxies;        
    }
}
